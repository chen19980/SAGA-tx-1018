package tw.com.firstbank.service;

import tw.com.firstbank.vo.DetailRqVo;
import tw.com.firstbank.vo.DetailRsVo;
import tw.com.firstbank.vo.HostRqVo;
import tw.com.firstbank.vo.HostRsVo;

public interface HostService {
    HostRsVo doTransaction(HostRqVo rq);
    HostRsVo compensateTransaction(HostRqVo rq);
}
