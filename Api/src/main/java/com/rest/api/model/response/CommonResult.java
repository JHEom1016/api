package com.rest.api.model.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommonResult {

    @ApiModelProperty(value = "응답 성공 여부: true / false")
    private boolean success;
    @ApiModelProperty(value = "응답 코드: 0 보다 크거나 같으면 정상, 0보다 적으면 오류")
    private int code;
    @ApiModelProperty(value = "응답 메시지")
    private String msg;
}
