package tw.com.firstbank.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tw.com.firstbank.service.HostService;
import tw.com.firstbank.vo.HostRqVo;
import tw.com.firstbank.vo.HostRsVo;

import javax.transaction.Transactional;
import java.util.Random;

@Service
@Slf4j
public class HostServiceImpl implements HostService {
    Random rand = new Random();

    @Override
    @Transactional
    public HostRsVo doTransaction(HostRqVo rq) {
        HostRsVo ret = new HostRsVo();

        if (rq.getAmt().intValue() == 3003) {
            log.debug("Host Service timeout before");
            try {
                Thread.sleep(10 * 1000);

            } catch (Exception e) {
                //ignored
            }
        }

        log.info(">>> Request to Host {}", rq);
        BeanUtils.copyProperties(rq, ret);
        ret.setId(rand.nextLong());

        if (rq.getAmt().intValue() == 3001) {
            log.debug("Host Service transaction fail");
            throw new IllegalStateException("doTransaction fail");
        }
        if (rq.getAmt().intValue() == 3002) {
            log.debug("Host Service timeout after");
            try {
                Thread.sleep(10 * 1000);

            } catch (Exception e) {
                //ignored
            }
        }

        log.info(">>> Host Response {}", ret);

        return ret;
    }

    @Override
    @Transactional
    public HostRsVo compensateTransaction(HostRqVo rq) {
        HostRsVo ret = new HostRsVo();
        BeanUtils.copyProperties(rq, ret);
        return ret;
    }


}
