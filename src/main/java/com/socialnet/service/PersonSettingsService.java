package com.socialnet.service;

import com.socialnet.dto.request.PersonSettingsRq;
import com.socialnet.dto.response.CommonRs;
import com.socialnet.dto.response.ComplexRs;
import com.socialnet.dto.response.PersonSettingsRs;
import com.socialnet.entity.enums.NotificationType;
import com.socialnet.repository.PersonRepository;
import com.socialnet.security.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.socialnet.entity.personrelated.Person;
import com.socialnet.entity.personrelated.PersonSettings;
import com.socialnet.exception.TokenParseException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PersonSettingsService {

    private final PersonService personService;
    private final JwtTokenUtils jwtTokenUtils;

    private final PersonRepository personRepository;

    public CommonRs<ComplexRs> editPersonSettings(String token, PersonSettingsRq personSettingsRq) {
        Long userId = jwtTokenUtils.getId(token);

        if (userId == null) {
            throw new TokenParseException("Неверный токен!");
        }

        Person person = personService.getPersonById(userId);
        PersonSettings personSettings = person.getPersonSettings();

        NotificationType type = personSettingsRq.getNotificationType();
        boolean enabled = personSettingsRq.isEnable();

        setFieldByNotificationType(personSettings, type, enabled);

        personRepository.save(person);

        CommonRs<ComplexRs> response = new CommonRs<>();
        ComplexRs complexRs = new ComplexRs();
        complexRs.setMessage(String.format("Настройка \"%s\" успешно установлена", type.getDescription()));
        response.setData(complexRs);

        return response;
    }

    public CommonRs<List<PersonSettingsRs>> getPersonSettings(String token) {
        Long userId = jwtTokenUtils.getId(token);

        if (userId == null) {
            throw new TokenParseException("Неверный токен!");
        }

        Person person = personService.getPersonById(userId);
        PersonSettings personSettings = person.getPersonSettings();

        return getListPersonSettingsResponse(personSettings);
    }

    private CommonRs<List<PersonSettingsRs>> getListPersonSettingsResponse(PersonSettings personSettings) {
        List<PersonSettingsRs> personSettingsRsList = convertToPersonSettingsRsList(personSettings);

        CommonRs<List<PersonSettingsRs>> commonRs = new CommonRs<>();
        commonRs.setData(personSettingsRsList);
        commonRs.setTotal((long) personSettingsRsList.size());
        commonRs.setTimeStamp(System.currentTimeMillis());

        return commonRs;
    }

    private List<PersonSettingsRs> convertToPersonSettingsRsList(PersonSettings personSettings) {
        List<PersonSettingsRs> personSettingsRsList = new ArrayList<>();

        for (NotificationType type : NotificationType.values()) {
            boolean enabled = getFieldByNotificationType(personSettings, type);

            PersonSettingsRs settingsRs = new PersonSettingsRs();
            settingsRs.setType(type);
            settingsRs.setDescription(type.getDescription());
            settingsRs.setEnable(enabled);

            personSettingsRsList.add(settingsRs);
        }

        return personSettingsRsList;
    }

    private boolean getFieldByNotificationType(PersonSettings personSettings, NotificationType type) {
        return switch (type) {
            case COMMENT_COMMENT -> personSettings.isCommentComment();
            case FRIEND_BIRTHDAY -> personSettings.isFriendBirthday();
            case FRIEND_REQUEST -> personSettings.isFriendRequest();
            case POST_LIKE -> personSettings.isPostLike();
            case MESSAGE -> personSettings.isMessage();
            case POST_COMMENT -> personSettings.isPostComment();
            case POST -> personSettings.isPost();
        };
    }

    private void setFieldByNotificationType(
            PersonSettings personSettings,
            NotificationType type,
            boolean enabled) {

        switch (type) {
            case COMMENT_COMMENT -> personSettings.setCommentComment(enabled);
            case FRIEND_BIRTHDAY -> personSettings.setFriendBirthday(enabled);
            case FRIEND_REQUEST -> personSettings.setFriendRequest(enabled);
            case POST_LIKE -> personSettings.setPostLike(enabled);
            case MESSAGE -> personSettings.setMessage(enabled);
            case POST_COMMENT -> personSettings.setPostComment(enabled);
            case POST -> personSettings.setPost(enabled);
        }
    }
}
