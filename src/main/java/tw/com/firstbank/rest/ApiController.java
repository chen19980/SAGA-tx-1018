package tw.com.firstbank.rest;

import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import tw.com.firstbank.processor.SagaProcessor;
import tw.com.firstbank.vo.Service1RepVo;
import tw.com.firstbank.vo.Service1ReqVo;

@Api(tags="Saga", produces= MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@RestController
@Validated
//@RequestMapping("/api")
@RequestMapping(
        value = "/api"
        , produces = {MediaType.APPLICATION_JSON_VALUE}
        , headers = {"content-type=application/x-www-form-urlencoded"}
        , consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE, MediaType.APPLICATION_JSON_VALUE}
)
public class ApiController {

    @Autowired
    private SagaProcessor<Service1ReqVo, Service1RepVo> processor;

    @PostMapping(value = "/saga3", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public Service1RepVo do1(@RequestBody Service1ReqVo vo) {

        Service1RepVo ret = null;
        try {
            log.debug("do1 {}", vo.toString());
            //todo: hide the route name
            ret = processor.doSAGA("direct:saga3", vo, Service1RepVo.class);
        } catch(Exception e) {

            log.error(e.getMessage(), e);
        } finally {

        }
        return ret;
    }

    @RequestMapping(method = RequestMethod.POST
            , value = "/saga4"
            , produces = {MediaType.APPLICATION_JSON_VALUE}
            , consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_FORM_URLENCODED_VALUE}
    )
    public ResponseEntity<Service1RepVo> do2(@RequestBody Service1ReqVo vo) {
        ResponseEntity<Service1RepVo> ret = null;
        try {

            log.debug("do2 {}", vo.toString());
            //todo: hide the route name
            Service1RepVo rep = processor.doSAGA("direct:saga4", vo, Service1RepVo.class);
            ret = new ResponseEntity<Service1RepVo>(rep, HttpStatus.OK);

        } catch(Exception e) {

            log.error(e.getMessage(), e);
        } finally {

        }
        return ret;
    }

}
