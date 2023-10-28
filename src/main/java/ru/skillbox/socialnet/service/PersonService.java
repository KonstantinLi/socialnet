package ru.skillbox.socialnet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.skillbox.socialnet.annotation.Debug;
import ru.skillbox.socialnet.dto.ProfileImageManager;
import ru.skillbox.socialnet.dto.parameters.GetUsersSearchPs;
import ru.skillbox.socialnet.dto.request.UserRq;
import ru.skillbox.socialnet.dto.response.CommonRs;
import ru.skillbox.socialnet.dto.response.ComplexRs;
import ru.skillbox.socialnet.dto.response.PersonRs;
import ru.skillbox.socialnet.entity.enums.FriendShipStatus;
import ru.skillbox.socialnet.entity.enums.MessagePermission;
import ru.skillbox.socialnet.entity.personrelated.Person;
import ru.skillbox.socialnet.exception.BadRequestException;
import ru.skillbox.socialnet.exception.PersonIsBlockedException;
import ru.skillbox.socialnet.exception.PersonNotFoundException;
import ru.skillbox.socialnet.mapper.PersonMapper;
import ru.skillbox.socialnet.repository.FriendShipRepository;
import ru.skillbox.socialnet.repository.PersonRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Debug
public class PersonService {

    private final PersonRepository personRepository;
    private final PersonMapper personMapper;
    private final FriendShipRepository friendShipRepository;
    private final ProfileImageManager profileImageManager;

    @Value("${aws.default-photo-url}")
    private String defaultPhotoUrl;

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

        updatePersonInfo(person, userData);
        Person savedPerson = personRepository.save(person);

        CommonRs<PersonRs> response = new CommonRs<>();
        PersonRs personRs = personMapper.personToPersonRs(savedPerson);
        response.setData(personRs);

        return response;
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

    /**
     * Method checks if user is present and not blocked or deleted
     *
     * @param personOptional - user to check
     *                       <p>
     *                       throws exceptions if user is not presented, blocked or deleted
     */
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

    private void updatePersonInfo(Person person, UserRq userData) {

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
        Optional<List<Person>> inactiveUsers = personRepository.findAllInactiveUsersByDeletedTime(
                LocalDateTime.now().minusMonths(1));

        inactiveUsers.ifPresent(persons -> persons.forEach(person -> {
            personRepository.delete(person);
            profileImageManager.deleteProfileImage(person.getId());
        }));
    }
}
