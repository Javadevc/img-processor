package com.imgprocessor.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseObject {
    private String link;
    private String id;
    private String imgType;
    private int statusCode;
    private String deleteHash;

    @Override
    public String toString() {
        if (statusCode >= 200 && statusCode < 300) {
            return "ResponseObject{" +
                    "link='" + link + '\'' +
                    ", id='" + id + '\'' +
                    ", imgType='" + imgType + '\'' +
                    ", status='" + statusCode + '\'' +
                    '}';
        } else {
            return "ResponseObject{" +
                    ", status='" + statusCode + '\'' +
                    '}';
        }
    }
}
