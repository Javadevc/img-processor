package com.imgprocessor.util;


import com.imgprocessor.exception.UploadException;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;
import java.util.logging.Logger;

/*
 * Downloads the image from provided link
 * and converts it to a Base64 string that is later
 * used for uploading to imgur
 * */

public class URLToBase64 {
    private static final Logger LOGGER = Logger.getLogger(String.valueOf(URLToBase64.class));

    byte[] byteData;
    String base64;

    public String getBase64String(String link) throws UploadException {
        try (InputStream in = new URL(link).openStream()) {
            byteData = IOUtils.toByteArray(in);
            base64 = Base64.getEncoder().encodeToString(byteData);
            LOGGER.info(link + " has been downloaded now. Base64 length: " + base64.length());
        } catch (MalformedURLException e) {
            throw new UploadException(e, 555);
        } catch (IOException e) {
            throw new UploadException(e);
        }

        return base64;
    }
}
