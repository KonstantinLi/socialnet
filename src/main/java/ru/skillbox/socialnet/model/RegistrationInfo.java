package ru.skillbox.socialnet.model;

import lombok.Data;

@Data
public class RegistrationInfo {
     private String code;
     private String codeSecret;
     private String email;
     private String firstName;
     private String lastName;
     private String passwd1;
     private String passwd2;
}
