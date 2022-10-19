package tw.com.firstbank.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;
import tw.com.firstbank.vo.Service1RepVo;
import tw.com.firstbank.vo.Service1ReqVo;
import tw.com.firstbank.vo.Service2RepVo;
import tw.com.firstbank.vo.Service2ReqVo;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class Service2Processor {
    private static Map<String, Service2RepVo> service2ResponseMap;


    static {
        service2ResponseMap = new HashMap<String, Service2RepVo>();
        Service2RepVo service2RepVo1 = new Service2RepVo();
        service2RepVo1.setSeq("12345");
        service2RepVo1.setSourceAmt("1000");
        service2RepVo1.setSourceCurrency("NTD");
        service2ResponseMap.put("success", service2RepVo1);
        service2ResponseMap.put("wait", service2RepVo1);
    }

    public Service2RepVo doService2(Exchange exchange) {
        Service2RepVo rep = null;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonString = objectMapper.writeValueAsString(exchange.getIn().getBody());

            log.info("doService2 input: {}", jsonString);
            Service2ReqVo req = objectMapper.readValue(jsonString, Service2ReqVo.class);
            rep = processService2(req);
        } catch (Exception e) {
            log.error("error.", e);
        }

        return rep;
    }

    public Service2RepVo compensationService1(Exchange exchange) {
        Service2RepVo rep = null;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonString = objectMapper.writeValueAsString(exchange.getIn().getBody());

            log.info("compensationService2 input: {}", jsonString);
            Service2ReqVo req = objectMapper.readValue(jsonString, Service2ReqVo.class);
            rep = processService2(req);
        } catch (Exception e) {
            log.error("error.", e);
        }
        return rep;
    }

    private Service2RepVo processService2(Service2ReqVo input) throws InterruptedException {
        Service2RepVo ret = null;
        log.debug("service1 {}", input.toString());

        if(input.getId().equals("wait")) {
            Thread.sleep(6000l);
        }

        ret = service2ResponseMap.get(input.getId());
        return ret;
    }
}
