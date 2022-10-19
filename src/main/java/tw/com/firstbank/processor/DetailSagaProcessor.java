package tw.com.firstbank.processor;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.model.SagaPropagation;
import org.springframework.stereotype.Component;
import tw.com.firstbank.fcbcore.fcbframework.core.saga.annotation.SagaInfo;
import tw.com.firstbank.vo.DetailRqVo;
import tw.com.firstbank.vo.DetailRsVo;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@SagaInfo(name = "detail_saga")
public class DetailSagaProcessor extends BaseSagaProcessor<DetailRqVo, DetailRsVo> {

    private void createSAGAFlow() {
        from(SAGA_ROUTE_NAME)
                .autoStartup(true)
                .saga()
                .timeout(SAGA_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .propagation(SagaPropagation.REQUIRES_NEW) //.propagation(SagaPropagation.SUPPORTS)
                .option(SAGA_DATA_NAME, body())  // 存放 SAGA 的輸入資料， compensation, complete 要用的資料

                // local mode
//                .option(PARENT_SAGA_DATA_NAME, header(PARENT_SAGA_DATA_NAME))
//                .option(SAGA_ID_NAME, header(SAGA_ID_NAME))
//                .option(SAGA_SEQ_NAME, setSeq("02"))
//                .setProperty(SAGA_DATA_NAME, body()) // 存放 後續流程 要用的 原始輸入資料
//                .setProperty(PARENT_SAGA_DATA_NAME, header(PARENT_SAGA_DATA_NAME))
//                .setProperty(SAGA_ID_NAME, header(SAGA_ID_NAME))
//                .setProperty(SAGA_SEQ_NAME, getSeq())

                // remote mode to local mode
                // parent data should be with the body
                .option(SAGA_ID_NAME, simple("${body.sagaId}"))
                .option(SAGA_SEQ_NAME, simple("M-${body.sagaSeq}"))
                .setProperty(SAGA_DATA_NAME, body()) // 存放 後續流程 要用的 原始輸入資料
                .setProperty(SAGA_ID_NAME, simple("${body.sagaId}"))
                .setProperty(SAGA_SEQ_NAME, simple("M-${body.sagaSeq}"))

                .compensation(SAGA_COMPENSATE_NAME)
                .completion(SAGA_COMPLETE_NAME)
                .choice()
                    .when(canExecuteSaga())
                        .to("bean:detailProcessor?method=doTransaction")
                        .bean(this, "toRs")
                        .bean(this, "sagaDone")
                    .otherwise()
                        .log(SAGA_ROUTE_NAME + " CAN NOT execute! ")
                .end()

                .log(SAGA_ROUTE_NAME + " Done");

        from(SAGA_COMPLETE_NAME)
                .autoStartup(true)
                .choice()
                .when(canCompleteSaga())
                .transform(header(SAGA_DATA_NAME))
                //.process(completeProcessor())
                //.bean(this, "txRqToJournalRq")
                //.to("bean:journalProcessor?method=doTransaction")

                .otherwise()
                .end()
                .bean(this, "sagaCompleted")
                .log(">>>complete ${body} ");

        from(SAGA_COMPENSATE_NAME)
                .autoStartup(true)
                .setHeader(SAGA_ID_NAME, simple("${body.sagaId}"))
                .setHeader(SAGA_SEQ_NAME, simple("M-${body.sagaSeq}"))
                .choice()
                    .when(canCompensateSaga())
                        // 補償 steps
                        .to("bean:detailProcessor?method=compensateTransaction")
                        .bean(this, "compensateResultToRs")
                    .otherwise()
                        .log("無法補償 CAN'T compensate")
                        .bean(this, "compensateReturnDefaultRs")
                .end()
                .bean(this, "sagaCompensated")
                .log(">>>compensate ${body} ");
    }

    @Override
    public void configureSAGA() throws Exception {
        createSAGAFlow();
    }

    public Object toRs(Exchange exchange) {
        DetailRsVo rs = (DetailRsVo) exchange.getIn().getBody();
        String sagaId = exchange.getProperty(SAGA_ID_NAME, String.class);
        String sagaSeq = exchange.getProperty(SAGA_SEQ_NAME, String.class);

        rs.setSagaId(sagaId);
        rs.setSagaSeq(sagaSeq);

        exchange.getIn().setHeader(SAGA_DATA_NAME, rs);
        return rs;
    }

    public Object compensateResultToRs(Exchange exchange) {
        DetailRsVo rs = (DetailRsVo) exchange.getIn().getBody();
        String sagaId = exchange.getIn().getHeader(SAGA_ID_NAME, String.class);
        String sagaSeq = exchange.getIn().getHeader(SAGA_SEQ_NAME, String.class);
        rs.setSagaId(sagaId);
        rs.setSagaSeq(sagaSeq);

        return rs;
    }

    public Object compensateReturnDefaultRs(Exchange exchange) {
        DetailRsVo rs = new DetailRsVo();
        String sagaId = exchange.getIn().getHeader(SAGA_ID_NAME, String.class);
        String sagaSeq = exchange.getIn().getHeader(SAGA_SEQ_NAME, String.class);
        rs.setSagaId(sagaId);
        rs.setSagaSeq(sagaSeq);
        return rs;
    }

}
