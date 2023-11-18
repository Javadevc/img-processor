package com.imgprocessor.dao;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

@Entity(name = "IMAGE_INFORMATION")
/*@Table(name = "USER_DETAILS",
		uniqueConstraints = {
				@UniqueConstraint(columnNames = "userName"),
				@UniqueConstraint(columnNames = "email")
		})*/
@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    @Column(name = "ID")
    private Long id;

    @NotNull
    @Column(name = "USER_NAME")
    private String userName;

    @NotNull
    @Column(name = "Job_id")
    private Long jobId;

    @NotNull
    @Column(name = "URLS_PASSED")
    private String urls;


    @NotNull
    @Column(name = "PENDING")
    private int pending;

    @Temporal(value = TemporalType.TIMESTAMP)
    @Column(name = "CREATED_TIME")
    private String creationTime;

    @Temporal(value = TemporalType.TIMESTAMP)
    @Column(name = "FINISHED_TIME")
    private String finishTime;
    @Column(name = "STATUS")
    private String status;

    @Column(name = "Link")
    private String link;

    @Column(name = "DeleteHash")
    private String deleteHash;

    public ImageInfo(Long id, int pending, String creationTime, String finishTime, String urls, String status, String userName) {
        this.jobId = id;
        this.pending = pending;
        this.creationTime = creationTime;
        this.finishTime = finishTime;
        this.urls = urls;
        this.status = status;
        this.userName = userName;

    }

}
