package tw.com.firstbank.processor;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tw.com.firstbank.service.MasterService;
import tw.com.firstbank.vo.MasterRqVo;
import tw.com.firstbank.vo.MasterRsVo;
import tw.com.firstbank.vo.TxRqVo;

@Component
@Slf4j
public class MasterProcessor {

    @Autowired
    MasterService masterService;

    public MasterRsVo doTransaction(Exchange exchange) {
        return masterService.doTransaction((MasterRqVo) exchange.getIn().getBody());
    }

    public MasterRsVo compensateTransaction(Exchange exchange) {
        return masterService.compensateTransaction((MasterRqVo) exchange.getIn().getBody());
    }

}
