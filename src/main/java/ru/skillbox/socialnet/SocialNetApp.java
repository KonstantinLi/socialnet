package ru.skillbox.socialnet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class SocialNetApp {

  public static void main(String[] args) {
    SpringApplication.run(SocialNetApp.class, args);
  }

}
