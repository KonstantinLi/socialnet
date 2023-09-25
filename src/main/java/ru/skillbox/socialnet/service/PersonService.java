package ru.skillbox.socialnet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.skillbox.socialnet.dto.ComplexRs;
import ru.skillbox.socialnet.dto.PersonRs;
import ru.skillbox.socialnet.dto.UserRq;
import ru.skillbox.socialnet.dto.response.CommonRs;
import ru.skillbox.socialnet.dto.response.ErrorRs;
import ru.skillbox.socialnet.entity.Person;
import ru.skillbox.socialnet.entity.enums.MessagePermission;
import ru.skillbox.socialnet.errs.BadRequestException;
import ru.skillbox.socialnet.repository.PersonRepository;
import ru.skillbox.socialnet.util.mapper.PersonMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PersonService {

    private final PersonRepository personRepository;

    @Value("${aws.default-photo-url}")
    private String defaultPhotoUrl;

    public CommonRs<PersonRs> getUserById(Long otherUserId, Long currentUserId) throws BadRequestException {

        Optional<Person> optional = personRepository.findById(otherUserId);
        if (optional.isEmpty()) {
            throw new BadRequestException("Пользователь с указанным id не найден");
        }
        Person person = optional.get();

        if (person.getPhoto() == null || person.getPhoto().isEmpty()) {
            person.setPhoto(defaultPhotoUrl);
        }

        PersonRs personRs = PersonMapper.INSTANCE.toRs(person);
        personRs.setToken("token");
        CommonRs<PersonRs> result = new CommonRs<>();
        result.setData(personRs);
        return result;
    }

    public CommonRs<PersonRs> updateUserInfo(Long userId, UserRq userData) {

        Optional<Person> personOptional = personRepository.findById(userId);

        //noinspection OptionalGetWithoutIsPresent
        Person person = personOptional.get();

        updatePersonInfo(person, userData);
        System.out.println(userData);
        Person savedPerson = personRepository.save(person);

        CommonRs<PersonRs> response = new CommonRs<>();
        PersonRs personRs = PersonMapper.INSTANCE.toRs(savedPerson);
        response.setData(personRs);

        return response;
    }

    public CommonRs<ComplexRs> deletePersonById(Long userId) {

        Optional<Person> personOptional = personRepository.findById(userId);

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
    //TODO finish method
    private void getAvailabilityError(
            @SuppressWarnings("OptionalUsedAsFieldOrParameterType") Optional<Person> person) throws BadRequestException {
        ErrorRs errorResponse = new ErrorRs();
        if (person.isEmpty()) {
            errorResponse.setError("AccountFindException");
            errorResponse.setError_description("Cannot find user by token");
        } else if (person.get().isBlocked()) {
            errorResponse.setError("AccountBlockedException");
            errorResponse.setError_description("User account has been BLOCKED");
        } else if (person.get().isDeleted()) {
            errorResponse.setError("AccountDeletedException");
            errorResponse.setError_description("User account has been DELETED");
        }

        throw new BadRequestException(errorResponse);
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
        if (userData.getBirth_date() != null) {
            String birthDate = userData.getBirth_date();
            if (birthDate.contains("+")) {
                birthDate = birthDate.substring(0, birthDate.indexOf("+"));
            }
            person.setBirthDate(LocalDateTime.parse(birthDate));
        }
        if (userData.getFirst_name() != null) {
            person.setFirstName(userData.getFirst_name());
        }
        if (userData.getLast_name() != null) {
            person.setLastName(userData.getLast_name());
        }
        if (userData.getMessages_permission() != null) {
            person.setMessagePermission(
                    MessagePermission.valueOf(userData.getMessages_permission()));
        }
        if (userData.getPhoto_id() != null) {
            person.setPhoto(userData.getPhoto_id());
        }
    }


    //TODO remove this method when id retrieval will be implemented
    public long getRandomIdFromDB() {
        List<Long> allId = personRepository.findAllId();
        return allId.get(0);
    }
}
