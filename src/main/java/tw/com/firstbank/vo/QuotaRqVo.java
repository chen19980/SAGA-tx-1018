package tw.com.firstbank.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

@ApiModel(description = "Quota 請求")
@Data
@NoArgsConstructor
public class QuotaRqVo implements Serializable {

    @ApiModelProperty(notes = "sagaId", required = true, example="\"c34b636a-5988-4ef2-ae3e-8b6cca0c6d2c\"")
    @JsonProperty("sagaId")
    private String sagaId;

    @ApiModelProperty(notes = "sagaSeq", required = true, example="\"01\"")
    @JsonProperty("sagaSeq")
    private String sagaSeq;

    @ApiModelProperty(notes = "status", required = true, example="\"start, complete, compensate\"")
    @JsonProperty("status")
    private String status;

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

