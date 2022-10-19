package tw.com.firstbank.service;

import tw.com.firstbank.vo.QuotaRsVo;
import tw.com.firstbank.vo.QuotaRqVo;

public interface QuotaService {
    QuotaRsVo doTransaction(QuotaRqVo rq);
    QuotaRsVo compensateTransaction(QuotaRqVo rq);
}
