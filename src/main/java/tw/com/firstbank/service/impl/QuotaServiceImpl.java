package tw.com.firstbank.service.impl;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tw.com.firstbank.entity.Quota;
import tw.com.firstbank.repository.QuotaRepository;
import tw.com.firstbank.service.QuotaService;
import tw.com.firstbank.vo.QuotaRqVo;
import tw.com.firstbank.vo.QuotaRsVo;
import javax.transaction.Transactional;

@Service
@Slf4j
public class QuotaServiceImpl implements QuotaService {

    @Autowired
    private QuotaRepository quotaRepository;


    @Override
    @Transactional
    public QuotaRsVo doTransaction(QuotaRqVo rq) {
        QuotaRsVo ret = new QuotaRsVo();
        Quota quota = new Quota();

        quota.setAccount((rq.getAccount()));
        BeanUtils.copyProperties(rq, quota);

        if (rq.getAmt().intValue() == 2003) {
            log.debug("Quota Service timeout before save");
            try {
                Thread.sleep(10 * 1000);

            } catch (Exception e) {
                //ignored
            }
        }

        quotaRepository.save(quota);

        ret.setAccount(quota.getAccount());
        BeanUtils.copyProperties(quota, ret);

        if (rq.getAmt().intValue() == 2001) {
            log.debug("Quota Service transaction fail");
            throw new IllegalStateException("doTransaction fail");
        }
        if (rq.getAmt().intValue() == 2002) {
            log.debug("Quota Service timeout after save");
            try {
                Thread.sleep(10 * 1000);

            } catch (Exception e) {
                //ignored
            }
        }

        return ret;
    }

    @Override
    @Transactional
    public QuotaRsVo compensateTransaction(QuotaRqVo rq) {
        QuotaRsVo ret = new QuotaRsVo();

        //TODO: fix this
//        if (quotaRepository.findById(rq.getAccount()).isPresent()) {
//            quotaRepository.deleteById(rq.getAccount());
//        }

        BeanUtils.copyProperties(rq, ret);
        return ret;
    }
}
