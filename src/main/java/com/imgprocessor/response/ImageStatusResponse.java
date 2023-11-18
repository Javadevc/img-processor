package com.imgprocessor.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;


/**
 * store the response of images
 */
@Getter
@Setter
public class ImageStatusResponse {

    List<String> pending;
    List<String> complete;
    List<String> failed;
    List<String> deleted;
}
