package com.imgprocessor.service;


import com.imgprocessor.exception.UploadException;
import com.imgprocessor.repository.DatabaseMapService;
import com.imgprocessor.request.UrlDeleteObjectRequest;
import com.imgprocessor.request.UrlObjectRequest;
import com.imgprocessor.response.JobIdObjectResponse;
import com.imgprocessor.util.JobUrlLists;
import jakarta.annotation.PreDestroy;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * interacting service
 */
@Service
public class UrlUploadDeleteRequestService {
    public static final int NUMBER_OF_THREADS = 4;
    private static final Logger LOGGER = Logger.getLogger(UrlUploadDeleteRequestService.class);
    ExecutorService execService;
    byte[] byteData;
    String base64;
    private String imageLink;
    @Autowired
    private DatabaseMapService databaseMapService;


    public UrlUploadDeleteRequestService() {
    }

    /**
     * calls the create method for the creeating the job id and upload the image
     * @param urlObjectRequest
     * @return
     */
    public JobIdObjectResponse createJobIdForUrl(UrlObjectRequest urlObjectRequest) {

        JobUrlLists jobUrlLists = databaseMapService.create(urlObjectRequest);
        JobIdObjectResponse jobId = new JobIdObjectResponse();
        jobId.setJobId(jobUrlLists.getId());
        LOGGER.info("current jobid: " + jobId.getJobId());
        if (!jobUrlLists.getPending().isEmpty()) {
            String link = jobUrlLists.getPending().get(0);
            uploadDeleteImage(link, jobUrlLists, "upload", null);
        }

        databaseMapService.saveImageAndUrlDetails(urlObjectRequest.getUrls().get(0));

        return jobId;
    }

    /**
     * calls the delete operation for deleteing thejob id from url
     * @param urlDeleteObjectRequest
     */
    public void deleteJobIdForUrl(UrlDeleteObjectRequest urlDeleteObjectRequest) {

        uploadDeleteImage(urlDeleteObjectRequest.getLink(), new JobUrlLists(), "delete", urlDeleteObjectRequest.getDeleteHash());

    }

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

    private void uploadDeleteImage(String imageLink, JobUrlLists jobUrlLists, String operation, String deleteHash) {
        try {

            //download the image from provided URL and convert to Base64 string
            String base64String = getBase64String(imageLink);
            //upload the image to imgur using the provided base 64 string
            UploadDeleteService uploadDeleteService = new UploadDeleteService(jobUrlLists);
            uploadDeleteService.uploadDeleteImage(base64String, imageLink, operation, deleteHash);
        } catch (UploadException e) {
            //move the pending urls to failed list
            jobUrlLists.getPending().remove(imageLink);
            jobUrlLists.getFailed().add(imageLink);
            jobUrlLists.setStatus("Failed");
            LOGGER.error("Adding the link " + imageLink + " to failed list");
            LOGGER.error("**[ERROR]** encountered following error: " + e.getStatus());

        }
    }

    @PreDestroy
    public void closeExecService() {
        execService.shutdown();
        LOGGER.info("Shutting down upload executor service ...");
        //forced termination if still not closed
        try {
            if (!execService.awaitTermination(3000L, TimeUnit.MILLISECONDS)) {
                LOGGER.warn("ExecutorService didn't terminate in the specified time.");
                List<Runnable> droppedTasks = execService.shutdownNow();
                LOGGER.warn("Executor was abruptly shut down. " + droppedTasks.size() + " tasks will not be executed.");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
