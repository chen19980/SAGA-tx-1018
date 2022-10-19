package tw.com.firstbank;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteConfigurationBuilder;
import org.apache.camel.component.mock.MockEndpoint;

import org.apache.camel.test.junit5.CamelTestSupport;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.camel.test.spring.junit5.DisableJmx;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("dev")
@CamelSpringBootTest
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DisableJmx(true)
public class MyRouteTest extends CamelTestSupport {

    @Autowired
    private CamelContext camelContext;

    @Override
    protected CamelContext createCamelContext() throws Exception {
        return camelContext;
    }

    @EndpointInject(uri = "direct:myEndpoint")
    private ProducerTemplate endpoint;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        camelContext.addRoutes(new RouteConfigurationBuilder() {
            @Override
            public void configuration() throws Exception {
                routeConfiguration("javaError")
                        .onException(Exception.class)
                        .maximumRedeliveries(0)
                        .handled(true)
                        .log("Java WARN: ${exception.message}");
            }
        });
    }

    @Override
    public String isMockEndpointsAndSkip() {
        return "myEndpoint:put*";
    }

    @Test
    public void shouldSucceed() throws Exception {
        assertNotNull(camelContext);
        assertNotNull(endpoint);

        String expectedValue = "expectedValue";
        MockEndpoint mock = getMockEndpoint("mock:myEndpoint:put");
        mock.expectedMessageCount(1);
        mock.allMessages().body().isEqualTo(expectedValue);
        mock.allMessages().header("hdr").isEqualTo("testHeader");
        endpoint.sendBodyAndHeader("test", "hdr", "testHeader");

        mock.assertIsSatisfied();
    }
}
