package com.imgprocessor.repository;

import com.imgprocessor.dao.ImageInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepo extends JpaRepository<ImageInfo, Long> {
    ImageInfo findByjobId(Long jobId);

    ImageInfo findBydeleteHash(String deleteHash);

    ImageInfo findByurls(String urls);

    List<ImageInfo> findByuserName(String userName);

}
