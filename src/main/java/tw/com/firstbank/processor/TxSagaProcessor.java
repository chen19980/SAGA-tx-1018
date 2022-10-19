package tw.com.firstbank.processor;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.model.SagaPropagation;
import org.slf4j.MDC;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tw.com.firstbank.adapter.gateway.outbound.MasterApiClient;
import tw.com.firstbank.fcbcore.fcbframework.core.saga.annotation.SagaInfo;
import tw.com.firstbank.vo.*;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Slf4j
@Component("txSagaProcessor")
@SagaInfo(name = "tx_saga")
public class TxSagaProcessor extends BaseSagaProcessor<TxRqVo, TxRsVo> {

    private final String SAGA_DEBUG_ROUTE_NAME = "direct:" + SAGA_NAME + "_debug";
    private final String SAGA_BALANCE_DATA_NAME = SAGA_NAME + "_data_balance";

    private void createSAGAFlow() {
        // 由 local 改為 remote
        String subRouteName1 = "direct:remote_master_saga";
        String subRouteName2 = "direct:remote_quota_saga";
        String subRouteName3 = "direct:remote_host_saga";


        from(SAGA_ROUTE_NAME)
                .autoStartup(true)
                .saga()
                .timeout(SAGA_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .propagation(SagaPropagation.REQUIRED)
                .log(SAGA_ROUTE_NAME + " ${body}")

                // 存放 SAGA 的輸入資料， compensation, complete 要用的資料，saga 會再設定於 header
                .option(SAGA_DATA_NAME, body())
                .option(SAGA_ID_NAME, setIdByInputMethodName("getTxGuid", TxRqVo.class))
                .option(SAGA_SEQ_NAME, getId())

                // property: 共用 存放 後續流程 要用的 原始輸入資料
                .setProperty(SAGA_DATA_NAME, body())
                .setProperty(SAGA_ID_NAME, getId())  // parent saga 的 id
                .setProperty(SAGA_SEQ_NAME, getId()) // parent saga 的 seq ??

                // header: 共用 存放 後續流程 要用的 原始輸入資料

                .compensation(SAGA_COMPENSATE_NAME)
                .completion(SAGA_COMPLETE_NAME)

                        .bean(this, "txRqToMasterRq")
                        .to(subRouteName1)

                        .bean(this, "masterRsToQuotaRq")
                        .to(subRouteName2)

                        .bean(this, "quotaRsToHostRq")
                        .to(subRouteName3)

                        .bean(this, "hostRsToTxRs")

                .log(SAGA_ROUTE_NAME + " Done");

        from(SAGA_COMPLETE_NAME)
                .autoStartup(true)
                .transform(header(SAGA_DATA_NAME))
                .log(">>>complete ${body} ");

        from(SAGA_COMPENSATE_NAME)
                .autoStartup(true)
                .transform(header(SAGA_DATA_NAME))
                // 補償 steps
                .process(compensateProcessor())
                .log(">>>compensate ${body} ");

        from(SAGA_DEBUG_ROUTE_NAME)
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        System.out.println("put a breakpoint here");
                    }
                })
                .log(LoggingLevel.DEBUG, org.slf4j.LoggerFactory.getLogger("tw.com.firstbank"), "Processing ${body}")
                .transform(body());

    }

    @Override
    public void configureSAGA() throws Exception {
        createSAGAFlow();
    }

    // TxRqVo, TxRsVo
    public Processor compensateProcessor() {
        Function<TxRqVo, TxRsVo> func = p -> {
            log.debug("Compensate {}", p.toString());
            TxRsVo ret = new TxRsVo();
            return ret;
        };
        return compensateProcessorFactory(func, TxRqVo.class);
    }

    public Processor completeProcessor() {
        //因為正常結束所以這裏 input == output == Service1ReqVo
        Function<TxRqVo, TxRsVo> func = p -> {
            log.debug("Complete {}", p.toString());
            TxRsVo ret = new TxRsVo();
            return ret;
        };
        return completeProcessorFactory(func, TxRqVo.class);
    }

    public Object txRqToMasterRq(Exchange exchange) {
        TxRqVo txRqVo = (TxRqVo) exchange.getIn().getBody();
        exchange.setProperty(SAGA_DATA_NAME, txRqVo);

        // set parent option
        exchange.getIn().setHeader(SAGA_DATA_NAME, txRqVo);

        MasterRqVo masterRqVo = new MasterRqVo();
        BeanUtils.copyProperties(txRqVo, masterRqVo);

        return masterRqVo;
    }

    public Object masterRsToQuotaRq(Exchange exchange) {
        QuotaRqVo quotaRqVo = new QuotaRqVo();

        MasterRsVo masterRsVo = (MasterRsVo) exchange.getIn().getBody();
        TxRqVo txRqVo = (TxRqVo) exchange.getProperty(SAGA_DATA_NAME);

        // set parent option
        exchange.getIn().setHeader(SAGA_DATA_NAME, txRqVo);

        exchange.setProperty(SAGA_BALANCE_DATA_NAME, masterRsVo.getBalance());

        BeanUtils.copyProperties(masterRsVo, quotaRqVo);
        // 不可以複製 master 的 saga seq !!
        quotaRqVo.setSagaSeq("02");
        quotaRqVo.setAmt(txRqVo.getAmt());
        quotaRqVo.setTxGuid(txRqVo.getTxGuid());

        return quotaRqVo;
    }

    public Object quotaRsToHostRq(Exchange exchange) {

        HostRqVo hostRqVo = new HostRqVo();
        QuotaRsVo quotaRsVo = (QuotaRsVo) exchange.getIn().getBody();
        TxRqVo txRqVo = (TxRqVo) exchange.getProperty(SAGA_DATA_NAME);

        // set parent option
        exchange.getIn().setHeader(SAGA_DATA_NAME, txRqVo);

        BeanUtils.copyProperties(quotaRsVo, hostRqVo);
        hostRqVo.setTxGuid(txRqVo.getTxGuid());
        hostRqVo.setSagaSeq("03");
        hostRqVo.setStatus("start");

        return hostRqVo;
    }

    public Object hostRsToTxRs(Exchange exchange) throws InterruptedException {
        TxRsVo txRsVo = new TxRsVo();
        HostRsVo hostRsVo = (HostRsVo) exchange.getIn().getBody();
        BigDecimal balance = (BigDecimal) exchange.getProperty(SAGA_BALANCE_DATA_NAME);
        BeanUtils.copyProperties(hostRsVo, txRsVo);
        txRsVo.setBalance(balance);

        if (balance.intValue() == 2007) {
            throw new IllegalStateException("doTransaction fail");
        }
        if (balance.intValue() == 2008) {
            Thread.sleep(10 * 1000);
        }

        return txRsVo;
    }

}
