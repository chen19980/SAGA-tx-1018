package tw.com.firstbank;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.support.ClassPathXmlApplicationContext;

//@SpringBootTest(properties = {"camel.springboot.java-routes-include-pattern=**/Foo*"})
public class CamelTest {

	@Test
	public void moveFolderContentSpringDSLTest() throws Exception {
		
		CamelContext camelContext = new DefaultCamelContext();
//	    camelContext.addRoutes(new RouteBuilder() {
//	      @Override
//	      public void configure() throws Exception {
//	        from("file://src/test/source-folder?delete=false").process(
//	          new FileProcessor()).to("file://src/test/target-folder");
//	      }
//	    });
	    camelContext.start();		
	    Thread.sleep(10000);
	    camelContext.stop();	    
	}
	
	class FileProcessor implements Processor {
	    public void process(Exchange exchange) throws Exception {
	        String originalFileName = (String) exchange.getIn().getHeader(
	          Exchange.FILE_NAME, String.class);

	        Date date = new Date();
	        SimpleDateFormat dateFormat = new SimpleDateFormat(
	          "yyyy-MM-dd HH-mm-ss");
	        String changedFileName = dateFormat.format(date) + originalFileName;
	        exchange.getIn().setHeader(Exchange.FILE_NAME, changedFileName);
	    }
	}
}
