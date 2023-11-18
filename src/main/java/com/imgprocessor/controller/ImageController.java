package com.imgprocessor.controller;


import com.imgprocessor.dao.ImageInfo;
import com.imgprocessor.dao.User;
import com.imgprocessor.repository.DatabaseMapService;
import com.imgprocessor.repository.ImageRepo;
import com.imgprocessor.repository.UserRepository;
import com.imgprocessor.request.UrlDeleteObjectRequest;
import com.imgprocessor.request.UrlObjectRequest;
import com.imgprocessor.response.JobIdObjectResponse;
import com.imgprocessor.response.JobStatusObjectResponse;
import com.imgprocessor.response.MessageResponse;
import com.imgprocessor.response.UserDetailsResponse;
import com.imgprocessor.service.UrlUploadDeleteRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.ExecutionException;


@RestController
@RequestMapping(value = "/v1/images", produces = "application/json")

public class ImageController {

    @Autowired
    private DatabaseMapService databaseMapService;

    @Autowired
    private ImageRepo imageRepo;

    @Autowired
    private UrlUploadDeleteRequestService requestService;

    private UrlObjectRequest urlObjectRequest;

    private UrlDeleteObjectRequest urlDeleteObjectRequest;
    @Autowired
    private UserRepository userRepository;

    @RequestMapping(value = "/upload", method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity<JobIdObjectResponse> uploadJob(@RequestBody UrlObjectRequest urlObjectRequest) throws ExecutionException, InterruptedException, NoSuchElementException {
        List<User> user = userRepository.findByuserName(urlObjectRequest.getUserName());
        if (!(null == user.get(0)) && !user.get(0).getPassword().equals(urlObjectRequest.getPassword())) {
            throw new NoSuchElementException("UserName and Password do not match");
        }
        if (urlObjectRequest.getUrls().size() > 1) {
            throw new RuntimeException("only 1 url is accepted");
        }
        JobIdObjectResponse responseJobId = requestService.createJobIdForUrl(urlObjectRequest);
        databaseMapService.saveImageAndUrlDetails(urlObjectRequest.getUrls().get(0));

        try {
            return new ResponseEntity<>(responseJobId, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity<MessageResponse> deleteJob(@RequestBody UrlDeleteObjectRequest urlDeleteObjectRequest) throws ExecutionException, InterruptedException, NoSuchElementException {
        List<User> user = userRepository.findByuserName(urlDeleteObjectRequest.getUserName());
        if (!(null == user.get(0)) && !user.get(0).getPassword().equals(urlDeleteObjectRequest.getPassword())) {
            throw new NoSuchElementException("UserName and Password do not match");
        }

        requestService.deleteJobIdForUrl(urlDeleteObjectRequest);
        databaseMapService.saveDeleteImageAndUrlDetails(urlDeleteObjectRequest.getDeleteHash());

        try {
            return ResponseEntity.ok(new MessageResponse("Link deleted successfully!"));
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
        }
    }


    @RequestMapping(value = "/fetch/{jobId}", method = RequestMethod.GET)
    public ResponseEntity<JobStatusObjectResponse> findByJobId(@PathVariable Long jobId) {
        Optional<JobStatusObjectResponse> jobStatusObject = databaseMapService.getJobStatusById(jobId);

        if (jobStatusObject.isPresent()) {
            return new ResponseEntity<>(jobStatusObject.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/allJobUrls")
    public ResponseEntity<UrlObjectRequest> findAllJobUrls() {
        urlObjectRequest.setUrls(databaseMapService.getAllUploadedLinks());
        return new ResponseEntity<>(urlObjectRequest, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/allJobUrlsbyUserName/{userId}")
    public ResponseEntity<List<ImageInfo>> getDetailsByJobID(@PathVariable String userId) {
        List<ImageInfo> imgInfo = imageRepo.findByuserName(userId);

        return ResponseEntity.ok().body(imgInfo);

    }

    @RequestMapping(method = RequestMethod.GET, value = "/userInfo/{userId}")
    public ResponseEntity<UserDetailsResponse> getDetailsByUserId(@PathVariable String userId) {
        List<ImageInfo> imgInfo = imageRepo.findByuserName(userId);
        List<User> user = userRepository.findByuserName(userId);
        UserDetailsResponse userDetailsResponse = new UserDetailsResponse();
        userDetailsResponse.setUser(user.get(0));
        userDetailsResponse.setImageInfo(imgInfo);
        return ResponseEntity.ok().body(userDetailsResponse);

    }
}
