package tw.com.firstbank.rest;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import tw.com.firstbank.processor.DetailSagaProcessor;
import tw.com.firstbank.service.HostService;

import tw.com.firstbank.vo.DetailRqVo;
import tw.com.firstbank.vo.DetailRsVo;
import tw.com.firstbank.vo.HostRqVo;
import tw.com.firstbank.vo.HostRsVo;


@Api(tags="Host", produces= MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@RestController
@Validated
@RequestMapping("/host")
public class HostApiController {
    @Autowired
    HostService hostService;

    @PostMapping(value = "/tx", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public HostRsVo doTx(@RequestBody HostRqVo vo) {
        HostRsVo ret = new HostRsVo();
        try {
            log.debug("doTx {}", vo.toString());
            ret = hostService.doTransaction(vo);
        } catch(Exception e) {
            log.error(e.getMessage(), e);
        } finally {

        }
        return ret;
    }

    @PostMapping(value = "/compensate", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public HostRsVo doCompensate(@RequestBody HostRqVo vo) {
        HostRsVo ret = new HostRsVo();
        try {
            log.debug("doCompensate {}", vo.toString());
            ret = hostService.compensateTransaction(vo);
        } catch(Exception e) {
            log.error(e.getMessage(), e);
        } finally {

        }
        return ret;
    }
}
