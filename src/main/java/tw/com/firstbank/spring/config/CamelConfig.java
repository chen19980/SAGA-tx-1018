package tw.com.firstbank.spring.config;

import javax.annotation.PostConstruct;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.*;
import org.apache.camel.builder.ErrorHandlerBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.engine.DefaultResourceResolvers;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.saga.InMemorySagaService;
import org.apache.camel.spi.Resource;
import org.apache.camel.spi.RoutesLoader;
import org.apache.camel.spi.Tracer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.io.FileNotFoundException;
import java.util.stream.Collectors;

@Configuration
@Slf4j
public class CamelConfig extends RouteBuilder {
	
	@Autowired
    CamelContext camelContext;

	@Override
	public void configure() throws Exception {

		camelContext.addService(new InMemorySagaService());
		//camelContext.setAutoStartup(true);

        //
        // Configure the LRA saga service
        //org.apache.camel.service.lra.LRASagaService sagaService = new org.apache.camel.service.lra.LRASagaService();
        //sagaService.setCoordinatorUrl("http://lra-service-host");
        //sagaService.setLocalParticipantUrl("http://my-host-as-seen-by-lra-service:8080/context-path");

        // Add it to the Camel context
        //camelContext.addService(sagaService);

		camelContext.setUseMDCLogging(true);
        camelContext.setLogMask(true);
        
        //trace
        camelContext.setTracingStandby(true);
        Tracer tracer = camelContext.getTracer();
        tracer.setEnabled(true);

        //log 
        camelContext.getGlobalOptions().put(Exchange.LOG_EIP_NAME, "tw.com.firstbank");

        //default 300 seconds
		camelContext.getShutdownStrategy().setTimeout(30);
        //--
		log.debug(">>>> camel context = {}", camelContext.getName());

		//TODO: be sure
        //camelContext.start();
		camelContext.getRouteController().startAllRoutes();
		log.debug(">>>> camel endpoints = {}", camelContext.getEndpoints().stream().map(p -> p.toString()).collect( Collectors.joining( ", " )));
	}

	@Bean
	public ProducerTemplate createCamelProducerTemplate() {
		return camelContext.createProducerTemplate();
	}

	@PostConstruct
	void loadRoutes() {
		//loadYamlRoute("saga.yaml");
		//loadYamlRoute("saga.xml");
	}

	private void loadYamlRoute(String name) {
		ExtendedCamelContext extendedCamelContext = camelContext.adapt(ExtendedCamelContext.class);
		RoutesLoader loader = extendedCamelContext.getRoutesLoader();
		try (DefaultResourceResolvers.ClasspathResolver resolver = new DefaultResourceResolvers.ClasspathResolver()) {
			resolver.setCamelContext(camelContext);
      
      //YAML
      //Resource resource = ResourceHelper.fromString("any.yaml", myRoute);
      //loader.loadRoutes(resource);
      
			Resource resource = resolver.resolve("classpath:camel/" + name);
			loader.loadRoutes(resource);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
