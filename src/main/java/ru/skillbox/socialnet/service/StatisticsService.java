package ru.skillbox.socialnet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import ru.skillbox.socialnet.dto.response.RegionStatisticsRs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StatisticsService {
    public Long getAllUsers() {
        return 0l;
    }

    public Long getAllUsersByCountry(String country) {
        return 0l;
    }

    public Long getAllUsersByCity(String city) {
        return 0l;
    }

    public Long getAllTags() {
        return 0l;
    }

    public Long getTagsByPost(Long postId) {
        return 0l;
    }

    public Long getAllPost() {
        return 0l;
    }

    public Long getAllPostByUser(Long userId) {
        return 0l;
    }

    public Long getAllMessage() {
        return 0l;
    }

    public Long getMessageByDialog(Long dialogId) {
        return 0l;
    }

    public Map<String, Long> getMessage(Long firstUserId, Long secondUserId) {
        Map<String, Long> map = new HashMap<>();
        return map;
    }

    public Long getAllLike() {
        return 0l;
    }

    public Long getLikeEntity(@RequestParam Long entityId) {
        return 0l;
    }

    public Long getDialog() {
        return 0l;
    }

    public Long getDialogsUser(@RequestParam Long userId) {
        return 0l;
    }

    public Long getCountry() {
        return 0l;
    }

    public List<RegionStatisticsRs> getCountryUsers() {
        List<RegionStatisticsRs> list = new ArrayList<>();
        return list;
    }

    public Long getCommentsByPost(Long postId) {
        return 0l;
    }

    public Long getAllCities() {
        return 0l;
    }

    public List<RegionStatisticsRs> getCitiesUsers() {
        List<RegionStatisticsRs> list = new ArrayList<>();
        return list;
    }
}
