package tw.com.firstbank.rest;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import tw.com.firstbank.service.QuotaService;
import tw.com.firstbank.vo.QuotaRqVo;
import tw.com.firstbank.vo.QuotaRsVo;
import tw.com.firstbank.processor.QuotaSagaProcessor;



@Api(tags="Quota", produces= MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@RestController
@Validated
@RequestMapping("/quota")
public class QuotaApiController {

    @Autowired
    QuotaService quotaService;

    @Autowired
    QuotaSagaProcessor quotaSagaProcessor;


    @PostMapping(value = "/tx", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public QuotaRsVo doTx(@RequestBody QuotaRqVo vo) {
        QuotaRsVo ret = new QuotaRsVo();
        try {
            log.debug("doTx {}", vo.toString());
            ret = quotaService.doTransaction(vo);
        } catch(Exception e) {
            log.error(e.getMessage(), e);
        } finally {

        }
        return ret;
    }

    @PostMapping(value = "/compensate", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public QuotaRsVo doCompensate(@RequestBody QuotaRqVo vo) {
        QuotaRsVo ret = new QuotaRsVo();
        try {
            log.debug("doCompensate {}", vo.toString());
            ret = quotaService.compensateTransaction(vo);
        } catch(Exception e) {
            log.error(e.getMessage(), e);
        } finally {

        }
        return ret;
    }
}
