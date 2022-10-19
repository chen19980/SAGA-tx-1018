package tw.com.firstbank.adapter.gateway.outbound;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import tw.com.firstbank.vo.HostRqVo;
import tw.com.firstbank.vo.HostRsVo;


@FeignClient(value = "hostApiClient", url = "http://localhost:7073/host/")
public interface HostApiClient {
    @RequestMapping(method = RequestMethod.POST, value = "/tx", produces = "application/json")
    HostRsVo doTx(@RequestBody HostRqVo vo);

    @RequestMapping(method = RequestMethod.POST, value = "/compensate", produces = "application/json")
    HostRsVo doCompensate(@RequestBody HostRqVo vo);
}
