package tw.com.firstbank.processor;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.spi.BrowsableEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class BrowseBean {

    @Autowired
    private CamelContext context;

    public void inspectMessageBrowse() {
        BrowsableEndpoint browse = context.getEndpoint("browse:messageBrowse", BrowsableEndpoint.class);
        List<Exchange> exchanges = browse.getExchanges();

        // then we can inspect the list of received exchanges from Java
        for (Exchange exchange : exchanges) {
            String payload = exchange.getIn().getBody().toString();
            log.debug("Inspect = {}", payload);
        }
    }
}