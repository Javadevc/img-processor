package com.imgprocessor.response;

import com.imgprocessor.dao.ImageInfo;
import com.imgprocessor.dao.User;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserDetailsResponse {

    List<ImageInfo> imageInfo;
    User user;
}
