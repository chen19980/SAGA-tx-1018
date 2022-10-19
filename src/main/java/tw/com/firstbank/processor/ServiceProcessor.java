package tw.com.firstbank.processor;

import ch.qos.logback.classic.Level;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import tw.com.firstbank.vo.Service1RepVo;
import tw.com.firstbank.vo.Service1ReqVo;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class ServiceProcessor {

    private static Map<String, Service1RepVo> service1ResponseMap;


    static {
        service1ResponseMap = new HashMap<String, Service1RepVo>();
        Service1RepVo service1RepVo1 = new Service1RepVo();
        service1RepVo1.setSeq("12345");
        service1RepVo1.setSourceAmt("1000");
        service1RepVo1.setSourceCurrency("NTD");
        service1ResponseMap.put("success", service1RepVo1);
        service1ResponseMap.put("wait", service1RepVo1);

    }

    public Service1RepVo doService1(Exchange exchange) {
        Service1RepVo rep = null;
        try {

            ObjectMapper objectMapper = new ObjectMapper();
            String jsonString = objectMapper.writeValueAsString(exchange.getIn().getBody());
            log.info("doService1 input: {}", jsonString);

            //Service1ReqVo req = objectMapper.readValue(jsonString, Service1ReqVo.class);
            //rep = processService1(req);

            rep = processService1((Service1ReqVo) exchange.getIn().getBody());
        } catch (Exception e) {
            log.error("error.", e);
        }

        return rep;
    }

    public Service1RepVo compensationService1(Exchange exchange) {
        Service1RepVo rep = null;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonString = objectMapper.writeValueAsString(exchange.getIn().getBody());

            log.info("compensationService1 input: {}", jsonString);
            Service1ReqVo req = objectMapper.readValue(jsonString, Service1ReqVo.class);
            rep = processService1(req);
        } catch (Exception e) {
            log.error("error.", e);
        }
        return rep;
    }

    private Service1RepVo processService1(Service1ReqVo input) throws InterruptedException {
        Service1RepVo ret = null;
        log.debug("service1 {}", input.toString());

        if(input.getId().equals("wait")) {
            Thread.sleep(6000l);
        }

        ret = service1ResponseMap.get(input.getId());
        return ret;
    }

    public static void debugLog(Logger logger, String message, Throwable t) {
        if (logger.isDebugEnabled()) {
            logger.debug(message, t);
        }
    }

    public static void debugLog(Logger logger, String message, Object... params) {
        if (logger.isDebugEnabled()) {
            logger.debug(message, params);
        }
    }


}
