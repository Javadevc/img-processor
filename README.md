# img-processor
create username password , and use imgur  api for image operations using spring boot


APIs Exposed:
1. To register a new user

POST http://localhost:8181/imgprocessor/api/users/signup

REQUEST BODY:
{
"userName":"test",
"password":"password",
"dateofBirth":"25-10-1992",
"userType" :"ADMIN",
"email":"test@test.com",
"gender": "MALE"
}

2. to upload a image using imgur
   POST http://localhost:8181/imgprocessor/v1/images/upload

REQUEST BODY:
{
"urls":[

           "https://kinsta.com/wp-content/uploads/2020/09/imag-file-types.png"
    ],
    "userName":"test",
    "password" :"password"

}

VALIDATIONS:
USER SHOULD BE REGISTERED FIRST
CORRECT CREDS SHOULD BE PASSED
ONLY 1 URL IS ACCEPTED
VALID ACCEPTED FORMATS FOR URL->    "png" , "gif" ,"tif" ,"jpg" , "bmp" , "jpeg"

3. TO VIEW DETAILS USING JOB ID
   GET http://localhost:8181/imgprocessor/v1/images/fetch/{JOBID}
4. TO VIEW IMAGE USING USERNAME
   GET http://localhost:8181/imgprocessor/v1/images/allJobUrlsbyUserName/{USERID}
5. TO DELETE A IMAGE
   POST  http://localhost:8181/imgprocessor/v1/images/delete
   REQUEST BODY:
   {
   "link":"https://i.imgur.com/LNgt71B.png",
   "userName":"test",
   "password" :"password",
   "deleteHash":"Qxukh8svzeYt7JL"
   }
6. TO FETCH ALL IMAGE AND USER DETAILS :

GET http://localhost:8181/imgprocessor/v1/images/userInfo/{USERID}
