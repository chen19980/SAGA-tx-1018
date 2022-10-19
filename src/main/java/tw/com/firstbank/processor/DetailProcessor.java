package tw.com.firstbank.processor;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tw.com.firstbank.service.HostService;
import tw.com.firstbank.vo.DetailRqVo;
import tw.com.firstbank.vo.DetailRsVo;
import tw.com.firstbank.vo.HostRqVo;
import tw.com.firstbank.vo.HostRsVo;

@Component
@Slf4j
public class DetailProcessor {
    @Autowired
    HostService hostService;

    public HostRsVo doTransaction(Exchange exchange) {
        return hostService.doTransaction((HostRqVo) exchange.getIn().getBody());
    }

    public HostRsVo compensateTransaction(Exchange exchange) {
        return hostService.compensateTransaction((HostRqVo) exchange.getIn().getBody());
    }
}
