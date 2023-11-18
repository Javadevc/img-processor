package com.imgprocessor.dao;

import com.imgprocessor.util.Gender;
import com.imgprocessor.util.UserType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.util.Date;

@Entity(name = "USER_DETAILS")
/*@Table(name = "USER_DETAILS",
		uniqueConstraints = {
				@UniqueConstraint(columnNames = "userName"),
				@UniqueConstraint(columnNames = "email")
		})*/
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    @Column(name = "ID")
    private Long id;

    @Column(name = "USER_NAME")
    private String userName;

    @Column(name = "PASSWORD")
    private String password;

    @org.springframework.data.annotation.Transient
    //@JsonIgnore
    @Column(name = "EMAIL")
    private String email;

    @Temporal(value = TemporalType.TIMESTAMP)
    @Column(name = "CREATED_TIME")
    private Date creationTime;


    @Temporal(value = TemporalType.DATE)
    @Column(name = "DOB")
    private String dateofBirth;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "USER_TYPE")
    private UserType userType;
/*
	@Transient
	private String dateOfBirthString;*/

    @Enumerated(value = EnumType.STRING)
    @Column(name = "GENDER")
    private Gender gender;

    public User(String userName, String email, String password, UserType userType, String dateofBirth, Date date, Gender gender) {
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.dateofBirth = dateofBirth;
        this.userType = userType;
        this.creationTime = date;
        this.gender = gender;

    }

    public User() {
    }
}
