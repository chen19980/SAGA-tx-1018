package tw.com.firstbank.processor;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tw.com.firstbank.service.QuotaService;
import tw.com.firstbank.vo.QuotaRqVo;
import tw.com.firstbank.vo.QuotaRsVo;

@Component
@Slf4j
public class QuotaProcessor {
    @Autowired
    QuotaService quotaService;

    public QuotaRsVo doTransaction(Exchange exchange) {
        return quotaService.doTransaction((QuotaRqVo) exchange.getIn().getBody());
    }

    public QuotaRsVo compensateTransaction(Exchange exchange) {
        return quotaService.compensateTransaction((QuotaRqVo) exchange.getIn().getBody());
    }

}
