package tw.com.firstbank.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@ApiModel(description = "Master 結果")
@Data
@NoArgsConstructor
public class MasterRsVo implements Serializable {

    @ApiModelProperty(notes = "sagaId", required = true, example="\"c34b636a-5988-4ef2-ae3e-8b6cca0c6d2c\"")
    @JsonProperty("sagaId")
    private String sagaId;

    @ApiModelProperty(notes = "sagaSeq", required = true, example="\"01\"")
    @JsonProperty("sagaSeq")
    private String sagaSeq;

    @ApiModelProperty(notes = "account", required = true, example="\"10-121-12345\"")
    @JsonProperty("account")
    private String account;

    @ApiModelProperty(notes = "balance", required = true, example="100")
    @JsonProperty("balance")
    private BigDecimal balance;
}
