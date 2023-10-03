package ru.skillbox.socialnet;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hibernate.annotations.SQLDelete;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import ru.skillbox.socialnet.controller.PostsController;
import ru.skillbox.socialnet.entity.personrelated.Person;
import ru.skillbox.socialnet.repository.PersonRepository;
import ru.skillbox.socialnet.security.JwtTokenUtils;

import static org.hamcrest.Matchers.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
//@WithUserDetails("8")
@TestPropertySource("/application-test.yml")
@Sql(value = {"/post-before-data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
//@Sql(value = {}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class SocialNetAppTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PostsController postsController;
    @Autowired
    private JwtTokenUtils jwtTokenUtils;
    @Autowired
    private PersonRepository personRepository;

    @Test
    void contextLoads() throws Exception {
    }

    @Test
    public void getPostById() throws Exception {
//        assertThat(postsController).isNotNull();
        Person person = personRepository.findById(Long.valueOf(1)).get();
        String token = jwtTokenUtils.generateToken(person);

        this.mockMvc.perform(get("/api/v1/post/1").header("authorization", token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.post_text").value("text test post 1"));
    }

    @Test
    public void updateById() throws Exception {
//        assertThat(postsController).isNotNull();
        Person person = personRepository.findById(Long.valueOf(1)).get();
        String token = jwtTokenUtils.generateToken(person);

        this.mockMvc.perform(get("/api/v1/post/1").header("authorization", token).contentType(MediaType.APPLICATION_JSON)
                .content("{/post.json}"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.post_text").value("some new text"));
    }

}
