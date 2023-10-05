package ru.skillbox.socialnet.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.socialnet.dto.response.RegionStatisticsRs;
import ru.skillbox.socialnet.entity.enums.LikeType;
import ru.skillbox.socialnet.service.StatisticsService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/statistics")
@RequiredArgsConstructor
public class StatisticsController {
    private final StatisticsService statisticsService;

    @GetMapping("/user")
    public Long getAllUsers() {
        return statisticsService.getAllUsers();
    }

    @GetMapping("/user/country")
    public Long getAllUsersByCountry(@RequestParam String country) {
        return statisticsService.getAllUsersByCountry(country);
    }

    @GetMapping("/user/city")
    public Long getAllUsersByCity(@RequestParam String city) {
        return statisticsService.getAllUsersByCity(city);
    }

    @GetMapping("/tag")
    public Long getAllTags() {
        return statisticsService.getAllTags();
    }

    @GetMapping("/tag/post")
    public Long getTagsByPost(@RequestParam Long postId) {
        return statisticsService.getTagsByPost(postId);
    }

    @GetMapping("/post")
    public Long getAllPost() {
        return statisticsService.getAllPost();
    }

    @GetMapping("/post/user")
    public Long getAllPostByUser(@RequestParam Long userId) {
        return statisticsService.getAllPostByUser(userId);
    }

    @GetMapping("/message")
    public Long getAllMessage() {
        return statisticsService.getAllMessage();
    }

    @GetMapping("/message/dialog")
    public Long getMessageByDialog(@RequestParam Long dialogId) {
        return statisticsService.getMessageByDialog(dialogId);
    }

    @GetMapping("/message/all")
    public Map<String, Long> getMessage(
            @RequestParam(defaultValue = "0") Long firstUserId,
            @RequestParam(defaultValue = "0") Long secondUserId
    ) {
        return statisticsService.getMessage(firstUserId, secondUserId);
    }

    @GetMapping("/like")
    public Long getAllLike() {
        return statisticsService.getAllLike();
    }

    @GetMapping("/like/entity")
    public Long getLikeEntity(@RequestParam Long entityId, @RequestParam LikeType type) {
        return statisticsService.getLikeEntity(entityId, type);
    }

    @GetMapping("/dialog")
    public Long getDialog() {
        return statisticsService.getDialog();
    }

    @GetMapping("/dialog/user")
    public Long getDialogsUser(@RequestParam Long userId) {
        return statisticsService.getDialogsUser(userId);
    }

    @GetMapping("/country")
    public Long getCountry() {
        return statisticsService.getCountry();
    }

    @GetMapping("/country/all")
    public List<RegionStatisticsRs> getCountryUsers() {
        return statisticsService.getCountryUsers();
    }

    @GetMapping("/comment/post")
    public Long getCommentsByPost(@RequestParam Long postId) {
        return statisticsService.getCommentsByPost(postId);
    }

    @GetMapping("/city")
    public Long getAllCities() {
        return statisticsService.getAllCities();
    }

    @GetMapping("/city/all")
    public List<RegionStatisticsRs> getCitiesUsers() {
        return statisticsService.getCitiesUsers();
    }
}
