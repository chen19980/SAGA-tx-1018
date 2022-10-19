package tw.com.firstbank.adapter.gateway.outbound;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import tw.com.firstbank.vo.MasterRqVo;
import tw.com.firstbank.vo.MasterRsVo;

@FeignClient(value = "masterApiClient", url = "http://localhost:7071/master/")
public interface MasterApiClient {
    @RequestMapping(method = RequestMethod.POST, value = "/tx", produces = "application/json")
    MasterRsVo doTx(@RequestBody MasterRqVo vo);

    @RequestMapping(method = RequestMethod.POST, value = "/compensate", produces = "application/json")
    MasterRsVo doCompensate(@RequestBody MasterRqVo vo);
}
