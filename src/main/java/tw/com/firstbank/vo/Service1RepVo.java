package tw.com.firstbank.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class Service1RepVo implements Serializable {
    private String seq;
    private String sourceCurrency;
    private String sourceAmt;
}
