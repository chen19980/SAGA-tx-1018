package tw.com.firstbank.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class Service2RepVo implements Serializable {
    private String seq;
    private String sourceCurrency;
    private String sourceAmt;
}
