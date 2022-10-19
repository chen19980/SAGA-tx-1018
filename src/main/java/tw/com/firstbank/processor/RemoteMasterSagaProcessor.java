package tw.com.firstbank.processor;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.model.SagaPropagation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tw.com.firstbank.adapter.gateway.outbound.MasterApiClient;
import tw.com.firstbank.fcbcore.fcbframework.core.saga.annotation.SagaInfo;
import tw.com.firstbank.vo.MasterRqVo;
import tw.com.firstbank.vo.MasterRsVo;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@SagaInfo(name = "remote_master_saga")
public class RemoteMasterSagaProcessor extends BaseSagaProcessor<MasterRqVo, MasterRsVo> {

    @Autowired
    MasterApiClient masterApiClient;
    private static final String SAGA_SEQ = "01";

    private void createSAGAFlow() {
        from(SAGA_ROUTE_NAME)
                .autoStartup(true)
                .saga()
                .timeout(SAGA_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .propagation(SagaPropagation.SUPPORTS)
                .option(SAGA_DATA_NAME, body())  // 存放 SAGA 的輸入資料， compensation, complete 要用的資料

                // local mode to remote mode
                .option(PARENT_SAGA_DATA_NAME, header(PARENT_SAGA_DATA_NAME))
                .option(SAGA_ID_NAME, header(SAGA_ID_NAME))
                .option(SAGA_SEQ_NAME, setSeq(SAGA_SEQ))
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
        MasterRsVo rs = null;
        MasterRqVo rq = (MasterRqVo) exchange.getIn().getBody();
        String sagaId = exchange.getProperty(SAGA_ID_NAME, String.class);
        rq.setSagaId(sagaId);
        rq.setSagaSeq(SAGA_SEQ);
        rs = masterApiClient.doTx(rq);

        // check api result
        if (rs.getBalance() == null) {
            throw new IllegalStateException("Remote doTransaction fail");
        }

        return rs;
    }

    public Object doCompensateByApiClient(Exchange exchange) {
        MasterRsVo rs = new MasterRsVo();
        MasterRqVo rq = (MasterRqVo) exchange.getIn().getBody();
        String sagaId = exchange.getIn().getHeader(SAGA_ID_NAME, String.class);
        rq.setSagaId(sagaId);
        rq.setSagaSeq(SAGA_SEQ);
        rs = masterApiClient.doCompensate(rq);
        return rs;
    }
}
