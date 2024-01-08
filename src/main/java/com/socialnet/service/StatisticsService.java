package com.socialnet.service;

import com.socialnet.dto.response.RegionStatisticsRs;
import com.socialnet.entity.enums.LikeType;
import com.socialnet.entity.personrelated.Person;
import com.socialnet.exception.PersonIsDeletedException;
import com.socialnet.exception.PersonNotFoundException;
import com.socialnet.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StatisticsService {
    private final PersonRepository personRepository;
    private final TagsRepository tagsRepository;
    private final Post2tagRepository post2tagRepository;
    private final PostsRepository postsRepository;
    private final PostCommentsRepository postCommentsRepository;
    private final MessagesRepository messagesRepository;
    private final LikesRepository likesRepository;
    private final DialogsRepository dialogsRepository;
    private final CountriesRepository countriesRepository;
    private final CitiesRepository citiesRepository;

    public Long getAllUsers() {
        return personRepository.countByIsDeletedFalseOrIsDeletedNull();
    }

    public Long getAllUsersByCountry(String country) {
        return personRepository.countByCountryAndIsDeleted(country, false);
    }

    public Long getAllUsersByCity(String city) {
        return personRepository.countByCityAndIsDeleted(city, false);
    }

    public Long getAllTags() {
        return tagsRepository.count();
    }

    public Long getTagsByPost(Long postId) {
        return post2tagRepository.countByPostId(postId);
    }

    public Long getAllPost() {
        return postsRepository.countByIsDeleted(false);
    }

    public Long getAllPostByUser(Long userId) {
        return postsRepository.countByAuthorIdAndIsDeleted(userId, false);
    }

    public Long getAllMessage() {
        return messagesRepository.countByIsDeleted(false);
    }

    public Long getMessageByDialog(Long dialogId) {
        return messagesRepository.countByDialogIdAndIsDeleted(dialogId, false);
    }

    public Map<String, Long> getMessage(Long firstUserId, Long secondUserId) {
        Map<String, Long> map = new HashMap<>();

        Person firstUser = personRepository.findById(firstUserId).orElseThrow(
                () -> new PersonNotFoundException(firstUserId)
        );
        if (Boolean.TRUE.equals(firstUser.getIsDeleted())) {
            throw new PersonIsDeletedException(firstUserId);
        }

        Person secondUser = personRepository.findById(secondUserId).orElseThrow(
                () -> new PersonNotFoundException(secondUserId)
        );
        if (Boolean.TRUE.equals(secondUser.getIsDeleted())) {
            throw new PersonIsDeletedException(secondUserId);
        }

        map.put(
                firstUser.getFirstName() +
                        "_" +
                        firstUser.getLastName() +
                        "->" +
                        secondUser.getFirstName() +
                        "_" +
                        secondUser.getLastName(),
                messagesRepository.countByAuthorIdAndRecipientId(firstUserId, secondUserId)
        );

        map.put(
                secondUser.getFirstName() +
                        "_" +
                        secondUser.getLastName() +
                        "->" +
                        firstUser.getFirstName() +
                        "_" +
                        firstUser.getLastName(),
                messagesRepository.countByAuthorIdAndRecipientId(secondUserId, firstUserId)
        );

        return map;
    }

    public Long getAllLike() {
        return likesRepository.count();
    }

    public Long getLikeEntity(Long entityId, LikeType type) {
        return likesRepository.countByTypeAndEntityId(type, entityId);
    }

    public Long getDialog() {
        return dialogsRepository.count();
    }

    public Long getDialogsUser(Long userId) {
        return dialogsRepository.countByFirstPersonIdOrSecondPersonId(userId, userId);
    }

    public Long getCountry() {
        return countriesRepository.count();
    }

    public Collection<RegionStatisticsRs> getCountryUsers() {
        return personRepository.countCountryStatistics();
    }

    public Long getCommentsByPost(Long postId) {
        return postCommentsRepository.countByPostIdAndIsDeleted(postId, false);
    }

    public Long getAllCities() {
        return citiesRepository.count();
    }

    public Collection<RegionStatisticsRs> getCitiesUsers() {
        return personRepository.countCityStatistics();
    }
}
