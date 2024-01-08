package com.socialnet.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.socialnet.annotation.BadRequestResponseDescription;
import com.socialnet.annotation.Info;
import com.socialnet.annotation.OkResponseSwaggerDescription;
import com.socialnet.dto.response.RegionStatisticsRs;
import com.socialnet.entity.enums.LikeType;
import com.socialnet.service.StatisticsService;

import java.util.Collection;
import java.util.Map;

@Tag(name = "StatisticsController", description = "Get statistics by social network")
@RestController
@RequestMapping("/api/v1/statistics")
@RequiredArgsConstructor
@Info
public class StatisticsController {
    private final StatisticsService statisticsService;

    @OkResponseSwaggerDescription(summary = "get the number of all users")
    @GetMapping("/user")
    public Long getAllUsers() {
        return statisticsService.getAllUsers();
    }

    @OkResponseSwaggerDescription(summary = "get the number of all users by country name")
    @GetMapping("/user/country")
    public Long getAllUsersByCountry(@RequestParam String country) {
        return statisticsService.getAllUsersByCountry(country);
    }

    @OkResponseSwaggerDescription(summary = "get the number of all users by city name")
    @GetMapping("/user/city")
    public Long getAllUsersByCity(@RequestParam String city) {
        return statisticsService.getAllUsersByCity(city);
    }

    @OkResponseSwaggerDescription(summary = "get the number of all tags")
    @GetMapping("/tag")
    public Long getAllTags() {
        return statisticsService.getAllTags();
    }

    @OkResponseSwaggerDescription(summary = "get the number of tags by post id")
    @GetMapping("/tag/post")
    public Long getTagsByPost(@RequestParam Long postId) {
        return statisticsService.getTagsByPost(postId);
    }

    @OkResponseSwaggerDescription(summary = "get the number of all posts")
    @GetMapping("/post")
    public Long getAllPost() {
        return statisticsService.getAllPost();
    }

    @OkResponseSwaggerDescription(summary = "get the number of post by user id")
    @GetMapping("/post/user")
    public Long getAllPostByUser(@RequestParam Long userId) {
        return statisticsService.getAllPostByUser(userId);
    }

    @OkResponseSwaggerDescription(summary = "get the number of all messages")
    @GetMapping("/message")
    public Long getAllMessage() {
        return statisticsService.getAllMessage();
    }

    @OkResponseSwaggerDescription(summary = "get the number of all messages by dialog id")
    @GetMapping("/message/dialog")
    public Long getMessageByDialog(@RequestParam Long dialogId) {
        return statisticsService.getMessageByDialog(dialogId);
    }

    @OkResponseSwaggerDescription(summary = "get the number of messages by id's of two persons."
            + " This method return map where key is description who author, and who recipient."
            + " And value is number of message")
    @BadRequestResponseDescription
    @GetMapping(value = "/message/all", produces = "application/json")
    public Map<String, Long> getMessage(
            @RequestParam(defaultValue = "0") Long firstUserId,
            @RequestParam(defaultValue = "0") Long secondUserId
    ) {
        return statisticsService.getMessage(firstUserId, secondUserId);
    }

    @OkResponseSwaggerDescription(summary = "get the number of all likes")
    @GetMapping("/like")
    public Long getAllLike() {
        return statisticsService.getAllLike();
    }

    @OkResponseSwaggerDescription(summary = "get the number of likes by post or comment id")
    @GetMapping("/like/entity")
    public Long getLikeEntity(@RequestParam Long entityId, @RequestParam LikeType type) {
        return statisticsService.getLikeEntity(entityId, type);
    }

    @OkResponseSwaggerDescription(summary = "get the number of all dialogs")
    @GetMapping("/dialog")
    public Long getDialog() {
        return statisticsService.getDialog();
    }

    @OkResponseSwaggerDescription(summary = "get the number of dialogs by user id")
    @GetMapping("/dialog/user")
    public Long getDialogsUser(@RequestParam Long userId) {
        return statisticsService.getDialogsUser(userId);
    }

    @OkResponseSwaggerDescription(summary = "get the number of all countries")
    @GetMapping("/country")
    public Long getCountry() {
        return statisticsService.getCountry();
    }

    @OkResponseSwaggerDescription(summary = "get countries with number of all users")
    @GetMapping(value = "/country/all", produces = "application/json")
    public Collection<RegionStatisticsRs> getCountryUsers() {
        return statisticsService.getCountryUsers();
    }

    @OkResponseSwaggerDescription(summary = "get the number of comments by post id")
    @GetMapping("/comment/post")
    public Long getCommentsByPost(@RequestParam Long postId) {
        return statisticsService.getCommentsByPost(postId);
    }

    @OkResponseSwaggerDescription(summary = "get the number of all cities")
    @GetMapping("/city")
    public Long getAllCities() {
        return statisticsService.getAllCities();
    }

    @OkResponseSwaggerDescription(summary = "get cities with number of users")
    @GetMapping(value = "/city/all", produces = "application/json")
    public Collection<RegionStatisticsRs> getCitiesUsers() {
        return statisticsService.getCitiesUsers();
    }
}
