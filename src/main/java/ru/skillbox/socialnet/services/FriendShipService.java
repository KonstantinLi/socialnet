package ru.skillbox.socialnet.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.skillbox.socialnet.data.dto.ComplexRs;
import ru.skillbox.socialnet.data.dto.CurrencyRs;
import ru.skillbox.socialnet.data.dto.PersonRs;
import ru.skillbox.socialnet.data.dto.WeatherRs;
import ru.skillbox.socialnet.data.dto.response.ApiFatherRs;
import ru.skillbox.socialnet.data.dto.response.CommonRsComplexRs;
import ru.skillbox.socialnet.data.dto.response.CommonRsListPersonRs;
import ru.skillbox.socialnet.data.dto.response.ErrorRs;
import ru.skillbox.socialnet.data.entity.FriendShip;
import ru.skillbox.socialnet.data.entity.Person;
import ru.skillbox.socialnet.data.enums.FriendShipStatus;
import ru.skillbox.socialnet.repository.FriendShipRepository;
import ru.skillbox.socialnet.repository.PersonRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

@Service
public class FriendShipService {
    @Autowired
    FriendShipRepository friendShipRepository;
    @Autowired
    PersonRepository personRepository;
    private final String NO_DATA_FOUND = "no data found";

    public Person getAuthorizedUser(String authorization) {
        /* TODO как-то по строке авторизации надо вычислить персону */
        Optional<Person> person = personRepository.findByemail(authorization);
        return person.isPresent() ? person.get() : null;
    }

    private ErrorRs generateErrorRs(String error, String error_description) {
        ErrorRs errorRs = new ErrorRs();
        errorRs.setError(error);
        errorRs.setError_description(error_description);
        return new ErrorRs();
    }

    private CommonRsComplexRs<ComplexRs> generateCommonRsComplexRs() {
        CommonRsComplexRs<ComplexRs> response = new CommonRsComplexRs<>();
        ComplexRs complexRs = new ComplexRs(null, null, null, null);
        ArrayList<ComplexRs> complexRsList = new ArrayList<>();
        complexRsList.add(complexRs);
        response.setData(complexRsList);
        response.setItemPerPage(1);
        response.setPerPage(0);
        response.setOffset(0);
        response.setTotal((long) 1);
        return response;
    }

    public ApiFatherRs sendFriendshipRequest(int destinationPersonId, String authorization) {
        Person currentPerson = getAuthorizedUser(authorization);
        if (currentPerson != null) {
            long currentPersonId = currentPerson.getId();
            Optional<Person> destinationPerson = personRepository.findById((long) destinationPersonId);
            if (destinationPerson.isPresent()) {
                //текущая персона отправляет запрос на дружбу
                FriendShip friendShip = new FriendShip();
                friendShip.setSourcePerson(currentPerson);
                friendShip.setDestinationPerson(destinationPerson.get());
                friendShip.setSentTime(LocalDateTime.now());
                friendShip.setStatus(FriendShipStatus.REQUEST);
                friendShipRepository.save(friendShip);
                //принимающая сторона принимает запрос на дружбу
                friendShip.setSourcePerson(destinationPerson.get());
                friendShip.setDestinationPerson(currentPerson);
                friendShip.setSentTime(LocalDateTime.now());
                friendShip.setStatus(FriendShipStatus.RECEIVED_REQUEST);
                friendShipRepository.save(friendShip);

                return generateCommonRsComplexRs();
            } else {
                return generateErrorRs(NO_DATA_FOUND, "error while creating Friendship request, destination person not found");
            }
        } else {
            return generateErrorRs(NO_DATA_FOUND, "error while creating Friendship request, current person not found");
        }
    }

    public ApiFatherRs deleteFriendById(int destinationPersonId, String authorization) {
        Person currentPerson = getAuthorizedUser(authorization);
        if (currentPerson != null) {
            long currentPersonId = currentPerson.getId();
            Optional<Person> destinationPerson = personRepository.findById((long) destinationPersonId);
            if (destinationPerson.isPresent()) {
                Iterable<FriendShip> friendShips = friendShipRepository.getFriendShipByIdsAndStatus(currentPersonId, destinationPerson.get().getId(), FriendShipStatus.FRIEND);
                if (friendShips.iterator().hasNext()) {
                    FriendShip friendShip = friendShips.iterator().next();
                    friendShipRepository.delete(friendShip);
                    return generateCommonRsComplexRs();
                } else {
                    return generateErrorRs(NO_DATA_FOUND, "error while deleting Friendship, Friendship not found");
                }
            } else {
                return generateErrorRs(NO_DATA_FOUND, "error while deleting Friendship, destination person not found");
            }
        } else {
            return generateErrorRs(NO_DATA_FOUND, "error while deleting Friendship, current person not found");
        }
    }

    public ApiFatherRs addFriendById(int destinationPersonId, String authorization) {
        Person currentPerson = getAuthorizedUser(authorization);
        if (currentPerson != null) {
            long currentPersonId = currentPerson.getId();
            Optional<Person> destinationPerson = personRepository.findById((long) destinationPersonId);
            if (destinationPerson.isPresent()) {
                Iterable<FriendShip> friendShips = friendShipRepository.getFriendShipByIdsAndStatus(currentPersonId, destinationPerson.get().getId(), FriendShipStatus.FRIEND);
                if (friendShips.iterator().hasNext()) {
                    return generateErrorRs("record found", "error while adding Friendship, Friendship already exists");
                } else {
                    //удалить все запросы в друзья и т.д.
                    Iterable<FriendShip> allFriendShips = friendShipRepository.getFriendShipByIdsAndStatus(currentPersonId, destinationPerson.get().getId(), null);
                    while (allFriendShips.iterator().hasNext()) {
                        friendShipRepository.delete(allFriendShips.iterator().next());
                    }
                    //добавить друга
                    FriendShip friendShip = new FriendShip();
                    friendShip.setSourcePerson(currentPerson);
                    friendShip.setDestinationPerson(destinationPerson.get());
                    friendShip.setSentTime(LocalDateTime.now());
                    friendShip.setStatus(FriendShipStatus.FRIEND);
                    friendShipRepository.save(friendShip);
                    return generateCommonRsComplexRs();
                }
            } else {
                return generateErrorRs(NO_DATA_FOUND, "error while adding Friendship, destination person not found");
            }
        } else {
            return generateErrorRs(NO_DATA_FOUND, "error while adding Friendship, current person not found");
        }
    }

    public ApiFatherRs declineFriendshipRequestById(int destinationPersonId, String authorization) {
        Person currentPerson = getAuthorizedUser(authorization);
        if (currentPerson != null) {
            long currentPersonId = currentPerson.getId();
            Optional<Person> destinationPerson = personRepository.findById((long) destinationPersonId);
            if (destinationPerson.isPresent()) {
                Iterable<FriendShip> friendShips = friendShipRepository.getFriendShipByIdsAndStatus(currentPersonId, destinationPerson.get().getId(), FriendShipStatus.RECEIVED_REQUEST);
                //если найден запрос на добавление в друзья, то отклонить его
                if (friendShips.iterator().hasNext()) {
                    FriendShip friendShip = friendShips.iterator().next();
                    friendShip.setSentTime(LocalDateTime.now());
                    // TODO  нет статуса DECLINED, надо добавить
                    friendShip.setStatus(FriendShipStatus.BLOCKED);
                    friendShipRepository.save(friendShip);
                    return generateCommonRsComplexRs();
                } else {
                    return generateErrorRs(NO_DATA_FOUND, "error while declining Friendship, Friendship request not found");
                }
            } else {
                return generateErrorRs(NO_DATA_FOUND, "error while declining Friendship, destination person not found");
            }
        } else {
            return generateErrorRs(NO_DATA_FOUND, "error while declining Friendship, current person not found");
        }
    }

    public ApiFatherRs getFriendsOfCurrentUser(String authorization, int offset, int perPaage) {
        Person currentPerson = getAuthorizedUser(authorization);
        if (currentPerson != null) {
            long total = personRepository.findCountPersonsByFriendship(currentPerson.getId());
            Page<Person> personsPage = personRepository.findPersonsByFriendship(
                    currentPerson.getId(), PageRequest.of(offset, perPaage));
            CommonRsListPersonRs<PersonRs>  personsList = new CommonRsListPersonRs<>();
            ArrayList<PersonRs> personsData = new ArrayList<>();
            while (personsPage.iterator().hasNext()) {
                Person person = personsPage.iterator().next();
                PersonRs personRs = new PersonRs();
                AssignPersonRsFieldsByPerson(personRs, person);
                personsData.add(personRs);
            }
            personsList.setData(personsData);
            personsList.setItemPerPage(perPaage);
            personsList.setOffset(offset);
            personsList.setPerPage(perPaage);
            personsList.setTotal(total);
            return personsList;
        } else {
            return generateErrorRs(NO_DATA_FOUND, "error while get FriendshipList, current person not found");
        }
    }

    private void AssignPersonRsFieldsByPerson(PersonRs personRs, Person person) {
        personRs.setAbout(person.getAbout());
        personRs.setCity(person.getCity());
        personRs.setCountry(person.getCountry());
        //personRs.setCurrency();
        personRs.setEmail(person.getEmail());
        personRs.setId(person.getId());
        //personRs.setOnline();
        personRs.setPhone(person.getPhone());
        personRs.setPhoto(person.getPhoto());
        personRs.setToken(person.getChangePasswordToken());
        //personRs.setWeather();
        personRs.setBirth_date(String.valueOf(person.getBirthDate()));
        personRs.setFirst_name(person.getFirstName());
        //personRs.setFriend_status();
        personRs.setIs_blocked(person.isBlocked());
        //personRs.setIs_blocked_by_current_user();
        personRs.setLast_name(person.getLastName());
        //personRs.setLast_online_time();
        personRs.setMessages_permission(String.valueOf(person.getMessagePermission()));
        personRs.setReg_date(String.valueOf(person.getRegDate()));
        personRs.setUser_deleted(person.isDeleted());
    }
}
