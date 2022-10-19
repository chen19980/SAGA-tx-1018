package tw.com.firstbank.adapter.gateway.outbound;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import tw.com.firstbank.vo.QuotaRqVo;
import tw.com.firstbank.vo.QuotaRsVo;


@FeignClient(value = "quotaApiClient", url = "http://localhost:7072/quota/")
public interface QuotaApiClient {
    @RequestMapping(method = RequestMethod.POST, value = "/tx", produces = "application/json")
    QuotaRsVo doTx(@RequestBody QuotaRqVo vo);

    @RequestMapping(method = RequestMethod.POST, value = "/compensate", produces = "application/json")
    QuotaRsVo doCompensate(@RequestBody QuotaRqVo vo);


}
