package com.imgprocessor.service;


import com.imgprocessor.exception.UploadException;
import com.imgprocessor.response.CustomResponseHandler;
import com.imgprocessor.response.ResponseObject;
import com.imgprocessor.util.JobUrlLists;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/*
 * Connects to imgur api to upload the provided Base64 string.
 * Based on the response the respective lists- uploaded, failed,
 * completed are updated.
 * */

public class UploadDeleteService {
    public static final String CLIENT_ID = "d5708748ccd2cfd";
    public static final String IMGUR_URL = "https://api.imgur.com/3/image";
    private static final Logger LOGGER = Logger.getLogger(UploadDeleteService.class);
    JobUrlLists jobUrlLists;

    public UploadDeleteService(JobUrlLists jobUrlLists) {
        this.jobUrlLists = jobUrlLists;
    }


    public JobUrlLists getJobUrlLists() {
        return jobUrlLists;
    }

    /**
     * connects to the imgur and upload delete the data
     * @param base64String
     * @param imageLink
     * @param operation
     * @param deleletHash
     * @throws UploadException
     */
    public void uploadDeleteImage(String base64String, String imageLink, String operation, String deleletHash) throws UploadException {

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPostRequest;
        if (operation == "upload") {
            httpPostRequest = new HttpPost(IMGUR_URL);
        } else {
            httpPostRequest = new HttpPost(IMGUR_URL + "/" + deleletHash);
        }
        httpPostRequest.setHeader("Authorization", "Client-ID " + CLIENT_ID);

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("image", base64String));
        CustomResponseHandler customResponseHandler = new CustomResponseHandler();
        int status = -1;
        try {
            httpPostRequest.setEntity(new UrlEncodedFormEntity(params));
            ResponseObject responseBody = null;
            if (operation == "upload") {
                responseBody = (ResponseObject) httpClient.execute(httpPostRequest, customResponseHandler);

                //LOGGER.info("----------------------------------------");
                LOGGER.info(responseBody);
                this.jobUrlLists.setDeleteHash(responseBody.getDeleteHash());
                status = responseBody.getStatusCode();
            } else {
                status = httpClient.execute(httpPostRequest).getStatusLine().getStatusCode();
            }

            if (operation == "delete") {
                if (status >= 200 && status < 300) {
                    this.jobUrlLists.getDeleted().add(imageLink);
                    this.jobUrlLists.getCompleted().remove(imageLink);
                    this.jobUrlLists.setStatus("Deleted");
                    this.jobUrlLists.setFinished(new Date().toString());
                }
            } else if (operation == "upload") {
                if (status >= 200 && status < 300) {
                    this.jobUrlLists.getPending().remove(imageLink);
                    this.jobUrlLists.getCompleted().add(responseBody.getLink());
                    if (this.jobUrlLists.getPending().isEmpty()) {
                        this.jobUrlLists.setFinished(new Date().toString());
                        this.jobUrlLists.setStatus("processed");
                    }
                    LOGGER.info("Adding the imgur link for " + imageLink + " to completed list.");
                } else {
                    this.jobUrlLists.getPending().remove(imageLink);
                    this.jobUrlLists.getFailed().add(imageLink);
                    this.jobUrlLists.setStatus("Failed");
                    LOGGER.info("Adding the link " + imageLink + " to failed list");
                }
            }


            httpClient.close();
        } catch (UnsupportedEncodingException e) {
            throw new UploadException(e, status);
        } catch (ClientProtocolException e) {
            throw new UploadException(e, status);
        } catch (IOException e) {
            throw new UploadException(e, status);
        }

    }


}
