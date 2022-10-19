package tw.com.firstbank.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tw.com.firstbank.entity.Detail;
import tw.com.firstbank.entity.Master;
import tw.com.firstbank.repository.DetailRepository;
import tw.com.firstbank.repository.MasterRepository;
import tw.com.firstbank.service.MasterService;
import tw.com.firstbank.vo.DetailRqVo;
import tw.com.firstbank.vo.DetailRsVo;
import tw.com.firstbank.vo.MasterRqVo;
import tw.com.firstbank.vo.MasterRsVo;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Optional;

@Service
@Slf4j
public class MasterServiceImpl implements MasterService {

    private static final String HOLD = "hold";
    private static final String CLEAR = "";

    @Autowired
    MasterRepository masterRepository;

    @Autowired
    DetailRepository detailRepository;

    @Override
    @Transactional
    public MasterRsVo doTransaction(MasterRqVo rq) throws IllegalStateException {
        if (canDoTransaction(rq.getAccount())) {
            if (rq.getAmt().intValue() == 1003) {
                log.debug("Master Service timeout before save");
                try {
                    Thread.sleep(10 * 1000);

                } catch (Exception e) {
                    //ignored
                }
            }

            MasterRsVo ret = processMaster(rq);

            processDetail(masterRsToDetailRq(ret, rq.getAmt()));

            if (rq.getAmt().intValue() == 1001) {
                log.debug("Master Service transaction fail");
                throw new IllegalStateException("doTransaction fail");
            }

            if (rq.getAmt().intValue() == 1002) {
                log.debug("Master Service timeout after save");
                try {
                    Thread.sleep(10 * 1000);

                } catch (Exception e) {
                    //ignored
                }
            }
            return ret;
        }
        throw new IllegalStateException("doTransaction Master already hold");
    }

    /**
     * 無論如何都不能失敗
     * @param rq
     * @return
     * @throws IllegalStateException
     */
    @Override
    @Transactional
    public MasterRsVo compensateTransaction(MasterRqVo rq) throws IllegalStateException {
        MasterRsVo rs = new MasterRsVo();
        String id = rq.getAccount();
        rq.setAmt(rq.getAmt().multiply(BigDecimal.valueOf(-1)));
        log.debug("Compensate Master {}", rq.toString());
        if (canDoTransaction(id)) {
            try {
                return processMaster(rq);

            } catch (Exception e) {
                //todo: 補償失敗!!
                log.error("補償失敗!! {}", rq.toString());
                BeanUtils.copyProperties(rq, rs);
            }
        }
        return rs;
    }

    private MasterRsVo processMaster(MasterRqVo rq) {
        MasterRsVo rs = new MasterRsVo();
        try {

            mark(rq.getAccount(), HOLD);
            Master master = masterRepository.findById(rq.getAccount()).get();
            master.setBalance(master.getBalance().add(rq.getAmt()));
            masterRepository.save(master);
            masterRepository.flush();

            //TODO:新增setsagaID
            rs.setSagaId(rq.getSagaId());
            rs.setBalance(master.getBalance());
            rs.setAccount(master.getId());
        } catch(Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        } finally {
            mark(rq.getAccount(), CLEAR);
        }
        return rs;
    }

    private DetailRqVo masterRsToDetailRq(MasterRsVo masterRsVo, BigDecimal amt) {
        DetailRqVo detailRqVo = new DetailRqVo();

        BeanUtils.copyProperties(masterRsVo, detailRqVo);
        // 不可以複製 master 的 saga seq !!
        detailRqVo.setTxGuid(masterRsVo.getSagaId());
        detailRqVo.setSagaSeq("01");
        detailRqVo.setAmt(amt);


        return detailRqVo;
    }

    private DetailRsVo processDetail(DetailRqVo rq) {
        DetailRsVo ret = new DetailRsVo();
        Detail detail = new Detail();

        BeanUtils.copyProperties(rq, detail);
        detail.setIrMasterId(rq.getAccount());
        detail.setTxGuid(rq.getTxGuid());

        //TODO:
        detailRepository.save(detail);

        ret.setAccount(detail.getIrMasterId());
        BeanUtils.copyProperties(detail, ret);
        return ret;
    }


    private Boolean canDoTransaction(String id) {
        Optional<Master> opt = masterRepository.findById(id);
        if (opt.isEmpty()) return true;

        String m = opt.get().getHoldMark();
        if (!StringUtils.hasText(m)) return true;

        return false;
    }

    private void mark(String id, String value) {

        Optional<Master> opt = masterRepository.findById(id);
        Master master = null;
        if (!opt.isPresent()) {
            master = new Master(id, BigDecimal.ZERO);
        } else {
            master = opt.get();
        }

        master.setHoldMark(value);
        masterRepository.save(master);
        masterRepository.flush();
    }

}
