package tw.com.firstbank.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@ApiModel(description = "寫入預收款")
@Data
@NoArgsConstructor
public class Service1ReqVo implements Serializable {
    @ApiModelProperty(notes = "id", required = true, example="\"success\"")
    @JsonProperty("id")
    private String id;
}
