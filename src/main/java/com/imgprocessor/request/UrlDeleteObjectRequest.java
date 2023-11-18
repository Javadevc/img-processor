package com.imgprocessor.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UrlDeleteObjectRequest {
    @NotNull
    private String link;

    @NotNull
    private String userName;
    @NotNull
    private String password;

    @NotNull
    private String deleteHash;

}
