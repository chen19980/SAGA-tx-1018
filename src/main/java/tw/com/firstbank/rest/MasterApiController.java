package tw.com.firstbank.rest;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import tw.com.firstbank.processor.MasterSagaProcessor;
import tw.com.firstbank.service.MasterService;
import tw.com.firstbank.vo.MasterRqVo;
import tw.com.firstbank.vo.MasterRsVo;


@Api(tags="Master", produces= MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@RestController
@Validated
@RequestMapping("/master")
public class MasterApiController {

    @Autowired
    MasterService masterService;

    @Autowired
    MasterSagaProcessor masterSagaProcessor;

    @PostMapping(value = "/tx", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public MasterRsVo doTx(@RequestBody MasterRqVo vo) {
        MasterRsVo ret = new MasterRsVo();
        try {
            log.debug("doTx {}", vo.toString());
            ret = masterService.doTransaction(vo);

        } catch(Exception e) {
            log.error(e.getMessage(), e);
        } finally {

        }
        return ret;
    }

    @PostMapping(value = "/compensate", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public MasterRsVo doCompensate(@RequestBody MasterRqVo vo) {
        MasterRsVo ret = new MasterRsVo();
        try {
            log.debug("doCompensate {}", vo.toString());
            ret = masterService.compensateTransaction(vo);

        } catch(Exception e) {
            log.error(e.getMessage(), e);
        } finally {

        }
        return ret;
    }

}
