package tw.com.firstbank.processor;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.model.SagaPropagation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import tw.com.firstbank.adapter.gateway.outbound.QuotaApiClient;
import tw.com.firstbank.fcbcore.fcbframework.core.saga.annotation.SagaInfo;

import tw.com.firstbank.vo.QuotaRqVo;
import tw.com.firstbank.vo.QuotaRsVo;
import java.util.concurrent.TimeUnit;



@Slf4j
@Component
@SagaInfo(name = "remote_quota_saga")
public class RemoteQuotaSagaProcessor extends BaseSagaProcessor<QuotaRqVo, QuotaRsVo> {
    @Autowired
    QuotaApiClient quotaApiClient;

    private static final String SAGA_SEQ = "02";

    private void createSAGAFlow() {
        from(SAGA_ROUTE_NAME)
                .autoStartup(true)
                .saga()
                .timeout(SAGA_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .propagation(SagaPropagation.SUPPORTS)

                // local mode to remote mode
                .option(PARENT_SAGA_DATA_NAME, header(PARENT_SAGA_DATA_NAME))
                .option(SAGA_ID_NAME, header(SAGA_ID_NAME))
                .option(SAGA_SEQ_NAME, setSeq(SAGA_SEQ))
                .option(SAGA_DATA_NAME, body())
                .setProperty(SAGA_DATA_NAME, body()) // 存放 後續流程 要用的 原始輸入資料
                .setProperty(PARENT_SAGA_DATA_NAME, header(PARENT_SAGA_DATA_NAME))
                .setProperty(SAGA_ID_NAME, header(SAGA_ID_NAME))
                .setProperty(SAGA_SEQ_NAME, getSeq())

                .compensation(SAGA_COMPENSATE_NAME)
                .completion(SAGA_COMPLETE_NAME)

                .bean(this, "doTxByApiClient")

                .log(SAGA_ROUTE_NAME + " Done");

        from(SAGA_COMPLETE_NAME)
                .autoStartup(true)
                .transform(header(SAGA_DATA_NAME))
                .log(">>>complete ${body} ");

        from(SAGA_COMPENSATE_NAME)
                .autoStartup(true)
                .transform(header(SAGA_DATA_NAME))
                // 補償 steps
                .bean(this, "doCompensateByApiClient")
                .log(">>>compensate ${body} ");
    }

    @Override
    public void configureSAGA() throws Exception {
        createSAGAFlow();
    }

    public Object doTxByApiClient(Exchange exchange) {
        QuotaRsVo rs = new QuotaRsVo();
        QuotaRqVo rq = (QuotaRqVo) exchange.getIn().getBody();
        String sagaId = exchange.getProperty(SAGA_ID_NAME, String.class);
        rq.setSagaId(sagaId);
        rq.setSagaSeq(SAGA_SEQ);
        rs = quotaApiClient.doTx(rq);

        // check api result
        if (rs.getAccount() == null) {
            throw new IllegalStateException("Remote doTransaction fail");
        }

        return rs;
    }

    public Object doCompensateByApiClient(Exchange exchange) {
        QuotaRsVo rs = new QuotaRsVo();
        QuotaRqVo rq = (QuotaRqVo) exchange.getIn().getBody();
        String sagaId = exchange.getIn().getHeader(SAGA_ID_NAME, String.class);
        rq.setSagaId(sagaId);
        rq.setSagaSeq(SAGA_SEQ);
        rs = quotaApiClient.doCompensate(rq);
        return rs;
    }
}