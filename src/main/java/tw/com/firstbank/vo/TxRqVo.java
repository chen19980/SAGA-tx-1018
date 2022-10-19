package tw.com.firstbank.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@ApiModel(description = "交易請求")
@Data
@NoArgsConstructor
public class TxRqVo implements Serializable {

    @ApiModelProperty(notes = "account", required = true, example="\"10-121-12345\"")
    @JsonProperty("account")
    private String account;

    @ApiModelProperty(notes = "amt", required = true, example="100")
    @JsonProperty("amt")
    private BigDecimal amt;

    @ApiModelProperty(notes = "txGuid", required = true, example="\"04e12d89-0b74-4b41-83bb-6cea5deddeb5\"")
    @JsonProperty("txGuid")
    private String txGuid;

}
