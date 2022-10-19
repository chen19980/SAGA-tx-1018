package tw.com.firstbank.rest;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import tw.com.firstbank.processor.SagaProcessor;
import tw.com.firstbank.vo.TxRqVo;
import tw.com.firstbank.vo.TxRsVo;
import org.springframework.beans.factory.annotation.Qualifier;

@Api(tags="Saga", produces= MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@RestController
@Validated
@RequestMapping("/api")
public class TxApiController {

    @Autowired
    @Qualifier("txSagaProcessor")
    private SagaProcessor<TxRqVo, TxRsVo> txProcessor;

    @PostMapping(value = "/tx", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public TxRsVo doTx(@RequestBody TxRqVo vo) {
        TxRsVo ret = new TxRsVo();
        try {
            log.debug("doTx {}", vo.toString());
            //todo: hide the route name
            ret = txProcessor.doSAGA("direct:tx_saga", vo, TxRsVo.class);
        } catch(Exception e) {
            log.error(e.getMessage(), e);
        } finally {

        }
        return ret;
    }
}
