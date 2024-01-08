package com.socialnet.service;

import com.socialnet.annotation.Info;
import com.socialnet.dto.ProfileImageManager;
import com.socialnet.dto.parameters.GetUsersSearchPs;
import com.socialnet.dto.request.UserRq;
import com.socialnet.dto.response.CommonRs;
import com.socialnet.dto.response.ComplexRs;
import com.socialnet.dto.response.CurrencyRs;
import com.socialnet.dto.response.PersonRs;
import com.socialnet.entity.dialogrelated.Dialog;
import com.socialnet.entity.dialogrelated.Message;
import com.socialnet.entity.enums.FriendShipStatus;
import com.socialnet.entity.enums.MessagePermission;
import com.socialnet.entity.locationrelated.Weather;
import com.socialnet.entity.personrelated.Person;
import com.socialnet.exception.*;
import com.socialnet.mapper.PersonMapper;
import com.socialnet.mapper.WeatherMapper;
import com.socialnet.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Info
public class PersonService {

    private final PersonRepository personRepository;
    private final PersonMapper personMapper;
    private final FriendShipRepository friendShipRepository;
    private final CurrencyRepository currencyRepository;
    private final ProfileImageManager profileImageManager;
    private final WeatherRepository weatherRepository;
    private final WeatherMapper weatherMapper;
    private final DialogRepository dialogRepository;

    @Value("${default-deleted-user-id}")
    private Long defaultDeletedPersonId;

    @Value("${aws.default-photo-url}")
    private String defaultPhotoUrl;
    private final PostsRepository postsRepository;
    private final PostCommentsRepository commentsRepository;
    private final MessageRepository messageRepository;

    public Person getPersonById(Long personId) {
        return personRepository.findById(personId).orElseThrow(
                () -> new PersonNotFoundException("Пользователь id: " + personId + " не найден"));
    }

    public CommonRs<PersonRs> getUserById(Long currentUserId, Long otherUserId) throws BadRequestException {

        Optional<Person> personOptional = personRepository.findById(otherUserId);
        Person person = checkAvailability(personOptional);

        Optional<FriendShipStatus> friendShipStatus =
                friendShipRepository.getFriendShipStatusBetweenTwoPersons(currentUserId, otherUserId);
        if (person.getPhoto() == null || person.getPhoto().isEmpty()) {
            person.setPhoto(defaultPhotoUrl);
        }
        PersonRs personRs = personMapper.personToPersonRs(person);
        CurrencyRs currencyRs = new CurrencyRs();
        currencyRs.setUsd(currencyRepository.findPriceByName("usd"));
        currencyRs.setEuro(currencyRepository.findPriceByName("eur"));
        personRs.setCurrency(currencyRs);
        Optional<Weather> optWeather = weatherRepository.findByCity(personRs.getCity());
        optWeather.ifPresent(weather -> personRs.setWeather(weatherMapper.weatherToWeatherRs(weather)));

        personRs.setFriendStatus((friendShipStatus.isEmpty()
                ? FriendShipStatus.UNKNOWN.toString() : friendShipStatus.get().toString()));
        CommonRs<PersonRs> result = new CommonRs<>();
        result.setData(personRs);

        return result;
    }

    public CommonRs<List<PersonRs>> getUsersByQuery(Long currentUserId,
                                                    GetUsersSearchPs getUsersSearchPs,
                                                    int offset,
                                                    int perPage) {
        Pageable nextPage = PageRequest.of(offset, perPage);
        CommonRs<List<PersonRs>> result = new CommonRs<>();
        Page<Person> personPage = personRepository.findUsersByQuery(currentUserId,
                getUsersSearchPs.getAgeFrom(),
                getUsersSearchPs.getAgeTo(),
                getUsersSearchPs.getCity(),
                getUsersSearchPs.getCountry(),
                getUsersSearchPs.getFirstName(),
                getUsersSearchPs.getLastName(),
                nextPage);
        result.setData(PersonMapper.INSTANCE.toRsList(personPage.getContent()));
        result.setTotal(personPage.getTotalElements());
        result.setItemPerPage(personPage.getContent().size());
        result.setPerPage(perPage);
        result.setOffset(offset);

        return result;
    }

    public CommonRs<PersonRs> updateUserInfo(Long userId, UserRq userData) {

        Optional<Person> personOptional = personRepository.findById(userId);
        Person person = checkAvailability(personOptional);

        validateNewBirthDate(userData.getBirthDate());

        updatePersonRecord(person, userData);
        Person savedPerson = personRepository.save(person);

        CommonRs<PersonRs> response = new CommonRs<>();
        PersonRs personRs = personMapper.personToPersonRs(savedPerson);
        response.setData(personRs);

        return response;
    }

    private static void validateNewBirthDate(String newBirthDateString) {
        if (newBirthDateString == null)
            return;

        if (newBirthDateString.contains("+")) {
            newBirthDateString = newBirthDateString.substring(0, newBirthDateString.indexOf("+"));
        }
        LocalDateTime newBirthDate = LocalDateTime.parse(newBirthDateString);
        if (newBirthDate.isAfter(LocalDateTime.now())) {
            throw new IllegalBirthDateDateException("Дата рождения еще не наступила!");
        }
    }

    public void updateUserPhoto(Long userId, String url) {

        Optional<Person> personOptional = personRepository.findById(userId);
        Person person = checkAvailability(personOptional);

        person.setPhoto(url);
        Person savedPerson = personRepository.save(person);

        CommonRs<PersonRs> response = new CommonRs<>();
        PersonRs personRs = personMapper.personToPersonRs(savedPerson);
        response.setData(personRs);
    }

    public CommonRs<ComplexRs> deletePersonById(Long userId) {

        Optional<Person> personOptional = personRepository.findById(userId);
        Person person = checkAvailability(personOptional);
        person.setIsDeleted(true);
        person.setDeletedTime(LocalDateTime.now());
        personRepository.save(person);

        ComplexRs successMessage = new ComplexRs();
        successMessage.setMessage("Ваша страница удалена :(");

        CommonRs<ComplexRs> result = new CommonRs<>();
        result.setData(successMessage);

        return result;
    }

    public CommonRs<ComplexRs> recoverUserInfo(Long userId) {

        Optional<Person> personOptional = personRepository.findById(userId);
        Person person = personOptional.orElseThrow(
                () -> new PersonNotFoundException(
                        String.format("Пользователь id %s не найден :(", userId))
        );
        if (Boolean.TRUE.equals(person.getIsBlocked())) {
            throw new PersonIsBlockedException(
                    String.format("Пользователь id %s заблокирован :(", userId));
        }

        person.setIsDeleted(false);
        person.setDeletedTime(null);
        personRepository.save(person);

        ComplexRs complexRs = new ComplexRs();
        complexRs.setMessage("Ваша страница восстановлена, с возвращением! :)");
        CommonRs<ComplexRs> response = new CommonRs<>();
        response.setData(complexRs);

        return response;
    }

    private Person checkAvailability(
            @SuppressWarnings("OptionalUsedAsFieldOrParameterType") Optional<Person> personOptional)
            throws PersonNotFoundException, PersonIsBlockedException {
        Person person = personOptional.orElseThrow(
                () -> new PersonNotFoundException("Пользователь с указанным id не найден"));
        if (Boolean.TRUE.equals(person.getIsBlocked())) {
            throw new PersonIsBlockedException("Пользователь заблокирован");
        } else if (Boolean.TRUE.equals(person.getIsDeleted())) {
            throw new PersonNotFoundException("Пользователь удален");
        }

        return person;
    }

    private void updatePersonRecord(Person person, UserRq userData) {

        if (userData.getAbout() != null) {
            person.setAbout(userData.getAbout());
        }
        if (userData.getCity() != null) {
            person.setCity(userData.getCity());
        }
        if (userData.getCountry() != null) {
            person.setCountry(userData.getCountry());
        }
        if (userData.getPhone() != null) {
            person.setPhone(userData.getPhone());
        }
        if (userData.getBirthDate() != null) {
            String birthDate = userData.getBirthDate();
            if (birthDate.contains("+")) {
                birthDate = birthDate.substring(0, birthDate.indexOf("+"));
            }
            person.setBirthDate(LocalDateTime.parse(birthDate));
        }
        if (userData.getFirstName() != null) {
            person.setFirstName(userData.getFirstName());
        }
        if (userData.getLastName() != null) {
            person.setLastName(userData.getLastName());
        }
        if (userData.getMessagesPermission() != null) {
            person.setMessagePermissions(
                    MessagePermission.valueOf(userData.getMessagesPermission()));
        }
        if (userData.getPhotoId() != null) {
            person.setPhoto(userData.getPhotoId());
        }
    }

    public void deleteInactiveUsers() {

        setDeletedTimeForUsersIfNotSet();

        List<Long> inactiveUsersIds = getInactiveUsersIds();
        if (inactiveUsersIds.isEmpty()) return;

        commentsRepository.deleteByAuthor_IdIn(inactiveUsersIds);

        List<Long> inactiveUsersPosts = postsRepository.findPosts_IdsByAuthors_Ids(inactiveUsersIds);
        commentsRepository.deleteByPost_IdIn(inactiveUsersPosts);

        postsRepository.deleteByIdIn(inactiveUsersPosts);

        friendShipRepository.deleteBySourcePerson_IdOrDestinationPerson_IdIn(inactiveUsersIds);

        inactiveUsersIds.forEach(personId -> {
            changePersonIdInDialogOnDeletion(personId);
            changePersonIdInMessagesOnDeletion(personId);
            personRepository.deleteById(personId);
            profileImageManager.deleteProfileImage(personId);
        });
    }

    private List<Long> getInactiveUsersIds() {
        List<Person> inactiveUsers = personRepository.
                findByIsDeletedAndDeletedTimeBefore(true, LocalDateTime.now().minusMonths(1));

        if (inactiveUsers.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> inactiveUsersIds = new ArrayList<>();

        for (Person person : inactiveUsers) {
            if (person.getId().equals(defaultDeletedPersonId)) continue;
            inactiveUsersIds.add(person.getId());
        }

        return inactiveUsersIds;
    }

    private void setDeletedTimeForUsersIfNotSet() {
        personRepository.findByIsDeletedTrueAndDeletedTimeNull()
                .forEach(person -> person.setDeletedTime(LocalDateTime.now()));
    }

    private void changePersonIdInDialogOnDeletion(Long personId) {
        Person defaultDeletedPerson = getDefaultDeletedPerson();

        removeDialogsWithNoPersons();

        List<Dialog> dialogs = dialogRepository.findByFirstPerson_IdOrSecondPerson_Id(personId, personId);
        dialogs.forEach(dialog -> {
            Long firstPersonId = dialog.getFirstPerson().getId();

            if (firstPersonId.equals(personId)) {
                dialog.setFirstPerson(defaultDeletedPerson);
            } else {
                dialog.setSecondPerson(defaultDeletedPerson);
            }

            if (dialog.getFirstPerson().getId().equals(defaultDeletedPersonId) &&
                    dialog.getSecondPerson().getId().equals(defaultDeletedPersonId)) {
                dialogRepository.delete(dialog);
            }

            dialogRepository.save(dialog);
        });
    }

    private void changePersonIdInMessagesOnDeletion(Long personId) {
        Person defaultDeletedPerson = getDefaultDeletedPerson();

        removeMessagesWithNoPersons();

        List<Message> messages = messageRepository.findByAuthor_IdOrRecipient_Id(personId, personId);

        messages.forEach(message -> {
            Long authorId = message.getAuthor().getId();

            if (authorId.equals(personId)) {
                message.setAuthor(defaultDeletedPerson);
            } else {
                message.setRecipient(defaultDeletedPerson);
            }

            if (message.getAuthor().getId().equals(defaultDeletedPersonId) &&
                    message.getRecipient().getId().equals(defaultDeletedPersonId)) {
                messageRepository.delete(message);
            }

            messageRepository.save(message);
        });
    }

    private void removeMessagesWithNoPersons() {
        messageRepository.deleteByAuthor_IdAndRecipient_Id(defaultDeletedPersonId);
    }

    private void removeDialogsWithNoPersons() {
        dialogRepository.deleteByFirstPerson_IdAndSecondPerson_Id(defaultDeletedPersonId);
    }

    private Person getDefaultDeletedPerson() {
        Optional<Person> defaultDeletedPersonOptional = personRepository.findById(defaultDeletedPersonId);
        return defaultDeletedPersonOptional.orElseThrow(
                () -> new DefaultDeletedUserNotFoundException("Ошибка удаления диалогов"));
    }
}
