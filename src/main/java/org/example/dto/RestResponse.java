package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class RestResponse {

    private boolean isSuccess;
    private Object response;

    public static RestResponse generateSuccessfulResponse(Object object) {
        return RestResponse.builder().isSuccess(true).response(object).build();
    }

    public static RestResponse generateFailedResponse(Object object) {
        return RestResponse.builder().isSuccess(false).response(object).build();
    }
}
