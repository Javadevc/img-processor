package com.imgprocessor.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UrlObjectRequest {
    @NotNull
    private List<String> urls;

    @NotNull
    private String userName;
    @NotNull
    private String password;

}
