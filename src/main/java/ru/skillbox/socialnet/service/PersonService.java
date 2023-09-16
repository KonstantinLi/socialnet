package ru.skillbox.socialnet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.skillbox.socialnet.data.dto.ComplexRs;
import ru.skillbox.socialnet.data.dto.PersonRs;
import ru.skillbox.socialnet.data.dto.UserRq;
import ru.skillbox.socialnet.data.dto.response.ApiResponse;
import ru.skillbox.socialnet.data.dto.response.CorrectResponse;
import ru.skillbox.socialnet.data.dto.response.ErrorResponse;
import ru.skillbox.socialnet.data.entity.Person;
import ru.skillbox.socialnet.data.enums.MessagePermission;
import ru.skillbox.socialnet.repository.PersonRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PersonService {

    private final PersonRepository personRepository;

    public ApiResponse getPersonRsById(Long userId) {
        Optional<Person> person = personRepository.findById(userId);

        ErrorResponse availabilityError = getAvailabilityError(person);
        if (availabilityError.getError() != null) {
            return availabilityError;
        }

        //noinspection OptionalGetWithoutIsPresent
        PersonRs personRs = new PersonRs(person.get());
        CorrectResponse<PersonRs> correctResponse = new CorrectResponse<>();
        correctResponse.setData(List.of(personRs));

        return correctResponse;
    }

    public ApiResponse updateUserInfo(Long userId, UserRq userData) {

        Optional<Person> personOptional = personRepository.findById(userId);

        ErrorResponse availabilityError = getAvailabilityError(personOptional);
        if (availabilityError.getError() != null) {
            return availabilityError;
        }

        //noinspection OptionalGetWithoutIsPresent
        Person person = personOptional.get();

        updatePersonInfo(person, userData);
        System.out.println(userData);
        Person savedPerson = personRepository.save(person);

        CorrectResponse<PersonRs> response = new CorrectResponse<>();
        PersonRs personRs = new PersonRs(savedPerson);
        response.setData(List.of(personRs));

        return response;
    }

    public ApiResponse deletePersonById(Long userId) {

        Optional<Person> personOptional = personRepository.findById(userId);

        ErrorResponse availabilityError = getAvailabilityError(personOptional);
        if (availabilityError.getError() != null) {
            return availabilityError;
        }

        //noinspection OptionalGetWithoutIsPresent
        personRepository.delete(personOptional.get());

        ComplexRs successMessage = new ComplexRs();
        successMessage.setMessage("Ваша страница удалена :(");

        CorrectResponse<Object> response = new CorrectResponse<>();
        response.setData(List.of(successMessage));

        return response;
    }

    /**
     * Method checks if user is present and not blocked or deleted
     *
     * @param person - user to check
     * @return ErrorResponse with error if user is not present or blocked or deleted,
     * empty ErrorResponse if everything is ok
     */
    private static ErrorResponse getAvailabilityError(
            @SuppressWarnings("OptionalUsedAsFieldOrParameterType") Optional<Person> person) {
        ErrorResponse errorResponse = new ErrorResponse();

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

        return errorResponse;
    }

    //TODO remove this method when id retrieval will be implemented
    public void updatePersonInfo(Person person, UserRq userData) {
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
            person.setBirthDate(LocalDateTime.parse(userData.getBirth_date()));
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

    public long getRandomIdFromDB() {
        List<Long> allId = personRepository.findAllId();
        return allId.get(0);
    }
}
