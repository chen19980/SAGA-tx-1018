package tw.com.firstbank.service;

import tw.com.firstbank.vo.MasterRqVo;
import tw.com.firstbank.vo.MasterRsVo;

public interface MasterService {
    MasterRsVo doTransaction(MasterRqVo rq) throws IllegalStateException;
    MasterRsVo compensateTransaction(MasterRqVo rq) throws IllegalStateException;
}
