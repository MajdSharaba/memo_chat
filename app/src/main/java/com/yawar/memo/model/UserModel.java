package com.yawar.memo.model;

  public class UserModel{
   String userId;
   String userName;
   String lastName;
   String email;
   String phone;
   String secretNumber;
   String image;
   String status;


      public UserModel(String userId, String userName, String lastName, String email, String phone,String secretNumber,String image,String status) {
          this.userId = userId;
          this.userName = userName;
          this.lastName = lastName;
          this.email = email;
          this.phone = phone;
          this.secretNumber = secretNumber;
          this.image = image;
          this.status = status;
      }

      public String getUserId() {
          return userId;
      }

      public void setUserId(String userId) {
          this.userId = userId;
      }

      public String getUserName() {
          return userName;
      }

      public void setUserName(String userName) {
          this.userName = userName;
      }

      public String getLastName() {
          return lastName;
      }

      public void setLastName(String lastName) {
          this.lastName = lastName;
      }

      public String getEmail() {
          return email;
      }

      public void setEmail(String email) {
          this.email = email;
      }

      public String getPhone() {
          return phone;
      }

      public void setPhone(String phone) {
          this.phone = phone;
      }

      public String getSecretNumber() {
          return secretNumber;
      }

      public void setSecretNumber(String secretNumber) {
          this.secretNumber = secretNumber;
      }

      public String getImage() {
          return image;
      }

      public void setImage(String image) {
          this.image = image;
      }

      public String getStatus() {
          return status;
      }

      public void setStatus(String status) {
          this.status = status;
      }
  }
