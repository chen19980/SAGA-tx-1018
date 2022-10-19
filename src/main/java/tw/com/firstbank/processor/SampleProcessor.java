package tw.com.firstbank.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.*;
import org.apache.camel.model.SagaPropagation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tw.com.firstbank.vo.Service1RepVo;
import tw.com.firstbank.vo.Service1ReqVo;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Slf4j
@Component
public class SampleProcessor extends BaseSagaProcessor<Service1ReqVo, Service1RepVo> {

    @Autowired
    private ObjectMapper objectMapper;

    public static final String SAGA_NAME = "saga4";
    public static final Integer SAGA_TIMEOUT_SECONDS = 5;

    private static final String SAGA_ROUTE_NAME = "direct:" + SAGA_NAME;
    private static final String SAGA_COMPENSATE_NAME = "direct:compensation" + SAGA_NAME;
    private static final String SAGA_COMPLETE_NAME = "direct:complete" + SAGA_NAME;
    private static final String SAGA_DATA_NAME = SAGA_NAME + "_data";

    private static final String SAGA_SUB_ROUTE_PREFIX = "direct:" + SAGA_NAME + "_";
    private static final String SAGA_DEBUG_ROUTE_NAME = "direct:" + SAGA_NAME + "_debug";

    private void createSAGAFlow() {
        String subRouteName1 = SAGA_SUB_ROUTE_PREFIX+"1";

        from(SAGA_ROUTE_NAME)
                .saga()
                .timeout(SAGA_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .propagation(SagaPropagation.REQUIRED)
                .log(SAGA_ROUTE_NAME + " ${body}")
                .option(SAGA_DATA_NAME, body())  // 存放 compensation, complete 要用的資料
                .compensation(SAGA_COMPENSATE_NAME)
                .completion(SAGA_COMPLETE_NAME)
                .choice()
                    .when(canExecuteSaga())
                        // 交易 steps
                        // 呼叫本地服務
                        .to("bean:serviceProcessor?method=doService1")
                        .to(SAGA_DEBUG_ROUTE_NAME)
                        .to(subRouteName1)
                        .bean(this, "doDebug")
                    .otherwise()
                        .log(SAGA_ROUTE_NAME + " CAN NOT execute! ")
                .end()
                .log(SAGA_ROUTE_NAME + " Done");

        from(SAGA_COMPLETE_NAME)
                .transform(header(SAGA_DATA_NAME))
                .process(completeProcessor())
                .log(">>>complete ${body} ");

        from(SAGA_COMPENSATE_NAME)
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

        from(subRouteName1)
                .transform(body())
                //.setHeader(Exchange.HTTP_METHOD, constant("POST"))
                //.setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                //.setHeader(Exchange.HTTP_QUERY, constant("q=test&lr=lang_en"))
                //.to("http://www.google.com/search")
                .log(">>> " + subRouteName1 + " ${body} ");
    }

    @Override
    public void configureSAGA() throws Exception {
        createSAGAFlow();
    }

    public Object doDebug(Exchange exchange) {
        log.debug("{}", exchange.getIn().getBody());
        return exchange.getIn().getBody();
    }

    public Processor compensateProcessor() {
        //因為補償所以這裏 input == output == Service1ReqVo
        Function<Service1ReqVo, Service1ReqVo> func = p -> {
            log.debug("Compensate {}", p.toString());
            return p;
        };
        return compensateProcessorFactory(func, Service1ReqVo.class);
    }

    public Processor completeProcessor() {
        //因為正常結束所以這裏 input == output == Service1ReqVo
        Function<Service1ReqVo, Service1ReqVo> func = p -> {
            log.debug("Complete {}", p.toString());
            return p;
        };
        return completeProcessorFactory(func, Service1ReqVo.class);
    }

}
