package ru.skillbox.socialnet;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runner.notification.Failure;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SuppressWarnings("ALL")
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
class SocialNetAppTests {

        public static void main(String[] args) {
            Result result = JUnitCore.runClasses(PostControllerTests.class);
            for (Failure failure : result.getFailures()) {
                System.out.println("fail ho gaya"+failure.toString());
            }
            System.out.println("passed:"+result.wasSuccessful());
        }
    }

