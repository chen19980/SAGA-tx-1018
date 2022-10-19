package tw.com.firstbank.route;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.SagaPropagation;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.model.rest.RestParamType;
import org.springframework.stereotype.Component;
import tw.com.firstbank.vo.Service1RepVo;
import tw.com.firstbank.vo.Service1ReqVo;
import tw.com.firstbank.vo.Service2RepVo;

import java.util.Random;
import java.util.concurrent.TimeUnit;

//@Component
public class CamelApiRoutes extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        restConfiguration()
                .component("netty-http")
                .port(9090)
                .bindingMode(RestBindingMode.auto)
                .consumerProperty("httpMethodRestrict", "GET,POST")
                .dataFormatProperty("prettyPrint", "true")
                .clientRequestValidation(true)
                .dataFormatProperty("json.in.disableFeatures", "FAIL_ON_UNKNOWN_PROPERTIES")
                //.corsHeaderProperty("Access-Control-Allow-Methods", "GET, POST")
                .apiContextPath("/saga/api-doc")
                .apiVendorExtension(true)
                .apiProperty("api.title", "Saga Service API")
                .apiProperty("api.version", "1.0.0")
                .apiProperty("cors", "true")
        ;

        createDoService1Route();


    }

    private void createCompensateService1Route() {
        rest().description("Saga Service").path("/saga/api")
                .consumes("application/json")
                .produces("application/json")
                .post("/compensation-service1")
                .description("Compensate Service1.")
                .param().name("id").type(RestParamType.body).description("The tx id.").dataType("string").endParam()
                .outType(Service1RepVo.class)
                .to("bean:serviceProcessor?method=compensationService1")
        ;
    }

    private void createCompensateService2Route() {
        rest().description("Saga Service").path("/saga/api")
                .consumes("application/json")
                .produces("application/json")
                .post("/compensation-service2")
                .description("Compensate Service2.")
                .param().name("id").type(RestParamType.body).description("The tx id.").dataType("string").endParam()
                .outType(Service2RepVo.class)
                .to("bean:service2Processor?method=compensationService2");
    }

    private void createDoService1Route() {
        rest().description("Saga Service").path("/saga/api")
                .consumes("application/json")
                .produces("application/json")
                //
                .post("/do-service2")
                .description("Do Service2.")
                .param().name("id").type(RestParamType.body).description("The tx id.").dataType("string").endParam()
                .outType(Service2RepVo.class)
                .to("bean:service2Processor?method=doService2");
    }

    private void createService1Route() {
        rest().description("Saga Service").path("/saga/api")
                .consumes("application/json")
                .produces("application/json")
                //
                .post("/do-service1")
                .description("Do Service1.")
                .param().name("id").type(RestParamType.body).description("The tx id.").dataType("string").endParam()
                .outType(Service1RepVo.class)
                // saga
                .route()
                .log("############################ ROUTE Body ${body} ")
                .saga()
                .timeout(5, TimeUnit.SECONDS)
                .log("############################ SAGA Body ${body} ")
                .propagation(SagaPropagation.REQUIRED)
                //.setHeader("body", body())
                //.setProperty("body", body())  // completion 收不到
                .option("body", body())  // 存放 compensation, complete 要用的資料
                .compensation("direct:compensationService1")
                .completion("direct:completeService1")
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        System.out.println("put a breakpoint here");
                    }
                })
                .log("############################ SAGA Do Try ${body} " + header("Exchange.SAGA_LONG_RUNNING_ACTION"))

                .doTry()
                .to("bean:serviceProcessor?method=doService1")
                // .process(doService1Processor)
                .doCatch(Exception.class)
                .log("############################ " + exceptionMessage().toString())
                .throwException(new RuntimeException("############################  failed"))
                .endDoTry()
                .log("############################ done")
        //--saga
        ;
        // 補償應使用 Exchange.SAGA_LONG_RUNNING_ACTION 來取得相關資料
        // any compensating action must be idempotent
        from("direct:compensationService1")
                .transform(header("body"))
                .log(">>>compensate ${body} ")
                .to("bean:serviceProcessor?method=compensationService1")
                .log("############################ has been cancelled");

        // property, header 都收不到, 要使用 option
        from("direct:completeService1")
                .transform(header("body"))
                .log(">>>complete ${body} ")
                .doTry()
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        System.out.println("put a breakpoint here");
                    }
                })
                .doCatch(Exception.class)
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        System.out.println(".");
                    }
                })
                .endDoTry()
                .log("############################ BBBB ${body}")
                .log("############################ has been completed");
    }
}
