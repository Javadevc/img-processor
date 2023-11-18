package com.imgprocessor.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JobStatusObjectResponse {

    String created;
    String finished;
    String status;
    ImageStatusResponse uploaded;
    private Long JobStatusId;

}
