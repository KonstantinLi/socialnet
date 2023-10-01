package ru.skillbox.socialnet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.skillbox.socialnet.dto.response.ComplexRs;
import ru.skillbox.socialnet.dto.request.UserRq;
import ru.skillbox.socialnet.dto.response.CommonRs;
import ru.skillbox.socialnet.dto.response.PersonRs;
import ru.skillbox.socialnet.entity.enums.FriendShipStatus;
import ru.skillbox.socialnet.entity.enums.MessagePermission;
import ru.skillbox.socialnet.entity.personrelated.Person;
import ru.skillbox.socialnet.exception.person.PersonIsBlockedException;
import ru.skillbox.socialnet.exception.person.PersonNotFoundException;
import ru.skillbox.socialnet.mapper.PersonMapper;
import ru.skillbox.socialnet.repository.FriendShipRepository;
import ru.skillbox.socialnet.dto.response.PersonRs;
import ru.skillbox.socialnet.dto.response.CommonRs;
import ru.skillbox.socialnet.dto.service.GetUsersSearchPs;
import ru.skillbox.socialnet.exception.BadRequestException;
import ru.skillbox.socialnet.entity.personrelated.Person;
import ru.skillbox.socialnet.repository.PersonRepository;
import ru.skillbox.socialnet.mapper.PersonMapper;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PersonService {
    private final PersonMapper personMapper;
    private final PersonRepository personRepository;
    private final FriendShipRepository friendShipRepository;
    @Value("${aws.default-photo-url}")
    private String defaultPhotoUrl;

    public CommonRs<PersonRs> getUserById(Long currentUserId, Long otherUserId) {

        Optional<Person> personOptional = personRepository.findById(otherUserId);
        checkAvailability(personOptional);
        //noinspection OptionalGetWithoutIsPresent
        Person person = personOptional.get();

        Optional<FriendShipStatus> friendShipStatus = Optional.empty();
        if (!Objects.equals(currentUserId, otherUserId)) {
            friendShipStatus =
                    friendShipRepository.getFriendShipStatusBetweenTwoPersons(currentUserId, otherUserId);
        }
        boolean isBlockedByCurrentUser = friendShipStatus.map(
                shipStatus -> shipStatus.equals(FriendShipStatus.BLOCKED)).orElse(false);

        //TODO убрать при обновлении базы
        if (person.getPhoto() == null || person.getPhoto().isEmpty()) {
            person.setPhoto(defaultPhotoUrl);
        }

        PersonRs personRs = personMapper.personToPersonRs(person);

        // TODO: is there a result of the following sentence?
        personMapper.personToPersonRs(person,
                friendShipStatus.map(Enum::name).orElse(null),
                isBlockedByCurrentUser);

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
        checkAvailability(personOptional);
        //noinspection OptionalGetWithoutIsPresent
        Person person = personOptional.get();

        updatePersonInfo(person, userData);
        Person savedPerson = personRepository.save(person);

        CommonRs<PersonRs> response = new CommonRs<>();
        PersonRs personRs = personMapper.personToPersonRs(savedPerson);
        response.setData(personRs);

        return response;
    }

    public void updateUserPhoto(Long userId, String url) {

        Optional<Person> personOptional = personRepository.findById(userId);
        checkAvailability(personOptional);
        //noinspection OptionalGetWithoutIsPresent
        Person person = personOptional.get();

        person.setPhoto(url);
        Person savedPerson = personRepository.save(person);

        CommonRs<PersonRs> response = new CommonRs<>();
        PersonRs personRs = personMapper.personToPersonRs(savedPerson);
        response.setData(personRs);
    }

    public CommonRs<ComplexRs> deletePersonById(Long userId) {

        Optional<Person> personOptional = personRepository.findById(userId);
        checkAvailability(personOptional);
        //noinspection OptionalGetWithoutIsPresent
        personRepository.delete(personOptional.get());

        ComplexRs successMessage = new ComplexRs();
        successMessage.setMessage("Ваша страница удалена :(");

        CommonRs<ComplexRs> result = new CommonRs<>();
        result.setData(successMessage);

        return result;
    }

    /**
     * Method checks if user is present and not blocked or deleted
     *
     * @param person - user to check
     *               <p>
     *               throws exceptions if user is not presented, blocked or deleted
     */
    private void checkAvailability(
            @SuppressWarnings("OptionalUsedAsFieldOrParameterType") Optional<Person> person)
            throws PersonNotFoundException, PersonIsBlockedException {
        if (person.isEmpty()) {
            throw new PersonNotFoundException("Пользователь с указанным id не найден");
        } else if (Boolean.TRUE.equals(person.get().getIsBlocked())) {
            throw new PersonIsBlockedException("Пользователь заблокирован");
        } else if (Boolean.TRUE.equals(person.get().getIsDeleted())) {
            throw new PersonNotFoundException("Пользователь удален");
        }
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
}
