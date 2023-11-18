package com.imgprocessor.repository;


import com.imgprocessor.dao.ImageInfo;
import com.imgprocessor.request.UrlObjectRequest;
import com.imgprocessor.response.ImageStatusResponse;
import com.imgprocessor.response.JobStatusObjectResponse;
import com.imgprocessor.util.IdGenerator;
import com.imgprocessor.util.JobUrlLists;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


@Repository
public class DatabaseMapService {

    public static final Map<Long, JobUrlLists> jobs = new ConcurrentHashMap<Long, JobUrlLists>();

    private static final Logger LOGGER = Logger.getLogger(DatabaseMapService.class);
    @Autowired
    private ImageRepo imageRepo;
    @Autowired
    private IdGenerator idGenerator;
    private final List<String> validExt;


    /**
     * This method tells the valid extensions
      */
    public DatabaseMapService() {
        validExt = new ArrayList<>();
        validExt.add("png");
        validExt.add("gif");
        validExt.add("tif");
        validExt.add("jpg");
        validExt.add("bmp");
        validExt.add("jpeg");
    }

    /**
     * method to check the valid extension create the job ID and update the cache
     * @param urlObjectRequest
     * @return
     */
    public JobUrlLists create(UrlObjectRequest urlObjectRequest) {
        JobUrlLists jobUrlListsObj = new JobUrlLists();
        Long id = idGenerator.getNextId();
        jobUrlListsObj.setId(id);
        jobUrlListsObj.setStatus("in-progress");
        String linkToBeChecked = urlObjectRequest.getUrls().get(0);
        String ext = FilenameUtils.getExtension(linkToBeChecked);
        if (!validExt.contains(ext.toLowerCase())) {
            LOGGER.info("removing " + linkToBeChecked + " as it does not conform to our supported " +
                    "image extensions (jpg/tif/bmp/png/gif)");
            jobUrlListsObj.setStatus("intermittent");
        } else {
            jobUrlListsObj.getPending().add(urlObjectRequest.getUrls().get(0));
        }
        jobUrlListsObj.setCreated(new Date().toString());
        ImageInfo imageInfo = new ImageInfo(id, jobUrlListsObj.getPending().size(), jobUrlListsObj.getCreated(), jobUrlListsObj.getFinished(), urlObjectRequest.getUrls().get(0), jobUrlListsObj.getStatus(), urlObjectRequest.getUserName());
        imageRepo.save(imageInfo);
        jobs.put(id, jobUrlListsObj);
        return jobUrlListsObj;
    }

    /**
     * save the details in the db for the link generated
     * @param url
     */
    public void saveImageAndUrlDetails(String url) {

        ImageInfo imageInfo = imageRepo.findByurls(url);
        JobUrlLists jobListObject = jobs.get(imageInfo.getId());
        imageInfo.setStatus(jobListObject.getStatus());
        if (!jobListObject.getPending().isEmpty()) {
            imageInfo.setPending(jobListObject.getPending().size());
        } else {
            imageInfo.setPending(0);
        }
        imageInfo.setLink(String.valueOf(jobListObject.getCompleted()));
        imageInfo.setDeleteHash(jobListObject.getDeleteHash());
        imageRepo.save(imageInfo);

    }

    /**
     * save the details for the deleted image
     * @param deleteHash
     */
    public void saveDeleteImageAndUrlDetails(String deleteHash) {

        ImageInfo imageInfo = imageRepo.findBydeleteHash(deleteHash);
        JobUrlLists jobListObject = jobs.get(imageInfo.getId());

        imageInfo.setStatus("deleted");
        jobListObject.getDeleted().add(imageInfo.getLink());

        imageInfo.setPending(jobListObject.getPending().size());
        imageInfo.setLink(String.valueOf(jobListObject.getCompleted()));
        imageInfo.setDeleteHash(jobListObject.getDeleteHash());
        imageInfo.setFinishTime(new Date().toString());
        imageRepo.save(imageInfo);

    }

    /**
     * fetch job status and other details basis the id being passed
     * @param id
     * @return
     */
    public Optional<JobStatusObjectResponse> getJobStatusById(Long id) {
        JobStatusObjectResponse jobStatusObject = new JobStatusObjectResponse();
        ImageStatusResponse imageStatusResponse = new ImageStatusResponse();
        if (jobs.containsKey(id)) {
            JobUrlLists jobListObject = jobs.get(id);
            jobStatusObject.setJobStatusId(jobListObject.getId());
            jobStatusObject.setCreated(jobListObject.getCreated());
            jobStatusObject.setStatus(jobListObject.getStatus());

            if (!jobListObject.getPending().isEmpty()) {
                imageStatusResponse.setPending(jobListObject.getPending());
            } else {
                jobStatusObject.setFinished(jobListObject.getFinished());
            }
            if (!jobListObject.getCompleted().isEmpty()) {
                imageStatusResponse.setComplete(jobListObject.getCompleted());
            }
            if (!jobListObject.getFailed().isEmpty()) {
                imageStatusResponse.setFailed(jobListObject.getFailed());
            }
            imageStatusResponse.setDeleted(jobListObject.getDeleted());
            jobStatusObject.setUploaded(imageStatusResponse);
        }
        return Optional.ofNullable(jobStatusObject);
    }

    /**
     * fetch all the uploaded links using cache map
     * @return
     */
    public List<String> getAllUploadedLinks() {
        List<String> uploadedUrlsList = new ArrayList<String>();
        for (Map.Entry<Long, JobUrlLists> entry : jobs.entrySet()) {
            uploadedUrlsList.addAll(entry.getValue().getCompleted());
        }
        return uploadedUrlsList;

    }
}
