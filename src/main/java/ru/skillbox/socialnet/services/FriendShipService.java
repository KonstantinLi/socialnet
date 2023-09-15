package ru.skillbox.socialnet.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.skillbox.socialnet.dto.ComplexRs;
import ru.skillbox.socialnet.dto.PersonRs;
import ru.skillbox.socialnet.dto.response.ApiFatherRs;
import ru.skillbox.socialnet.dto.response.CommonRsComplexRs;
import ru.skillbox.socialnet.dto.response.CommonRsListPersonRs;
import ru.skillbox.socialnet.dto.response.ErrorRs;
import ru.skillbox.socialnet.entity.FriendShip;
import ru.skillbox.socialnet.entity.Person;
import ru.skillbox.socialnet.entity.enums.FriendShipStatus;
import ru.skillbox.socialnet.repository.FriendShipRepository;
import ru.skillbox.socialnet.repository.PersonRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;
@Slf4j
@Service
public class FriendShipService {
    @Autowired
    FriendShipRepository friendShipRepository;
    @Autowired
    PersonRepository personRepository;
    private final String NO_DATA_FOUND = "no data found";
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(FriendShipService.class);

    public Person getAuthorizedUser(String authorization) {
        // TODO выясниить как по строке авторизации вычислить персону и написать имплементацию метода
        Optional<Person> person = personRepository.findPesonByemail("eobyz@rambler.ru");
        return person.isPresent() ? person.get() : null;
    }

    private ErrorRs generateErrorRs(String error, String error_description) {
        ErrorRs errorRs = new ErrorRs();
        errorRs.setError(error);
        errorRs.setError_description(error_description);
        return errorRs;
    }

    private CommonRsComplexRs<ComplexRs> generateCommonRsComplexRs() {
        CommonRsComplexRs<ComplexRs> response = new CommonRsComplexRs<>();
        //TODO выяснить откуда брать значение полей для класса ComplexRs и переписать создание класса
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
            Optional<Person> destinationPerson = personRepository.findById((long) destinationPersonId);
            if (destinationPerson.isPresent()) {
                //текущая персона отправляет запрос на дружбу
                FriendShip friendShip = new FriendShip();

                friendShip.setSourcePerson(currentPerson);
                friendShip.setDestinationPerson(destinationPerson.get());
                friendShip.setSentTime(LocalDateTime.now());
                friendShip.setStatus(FriendShipStatus.REQUEST);
                try {
                    friendShipRepository.save(friendShip);
                } catch (Exception ex) {
                    return generateErrorRs("error while creating Friendship request", ex.getMessage());
                }
                //принимающая сторона принимает запрос на дружбу
                friendShip.setSourcePerson(destinationPerson.get());
                friendShip.setDestinationPerson(currentPerson);
                friendShip.setSentTime(LocalDateTime.now());
                friendShip.setStatus(FriendShipStatus.RECEIVED_REQUEST);
                try {
                    friendShipRepository.save(friendShip);
                } catch (Exception ex) {
                    return generateErrorRs("error while creating Friendship request", ex.getMessage());
                }

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
                //если найден запрос на добавление в друзья, то удалить его (DECLINE статуса не предусмотрено)
                if (friendShips.iterator().hasNext()) {
                    FriendShip friendShip = friendShips.iterator().next();
                    friendShipRepository.delete(friendShip);
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

    public ApiFatherRs blockOrUnblockUserByUser(int destinationPersonId, String authorization) {
        Person currentPerson = getAuthorizedUser(authorization);
        if (currentPerson != null) {
            long currentPersonId = currentPerson.getId();
            Optional<Person> destinationPerson = personRepository.findById((long) destinationPersonId);
            if (destinationPerson.isPresent()) {
                Iterable<FriendShip> friendShips = friendShipRepository.getFriendShipByIdsAndStatus(currentPersonId, destinationPerson.get().getId(), FriendShipStatus.BLOCKED);
                //если найдена запись с заблокированной персоной, то надо эту запись удалить
                if (friendShips.iterator().hasNext()) {
                    FriendShip friendShip = friendShips.iterator().next();
                    friendShipRepository.delete(friendShip);
                } else {
                    //если запись не найдена, то надо персону заблокировать
                    FriendShip friendShip = new FriendShip();
                    friendShip.setSourcePerson(currentPerson);
                    friendShip.setDestinationPerson(destinationPerson.get());
                    friendShip.setSentTime(LocalDateTime.now());
                    friendShip.setStatus(FriendShipStatus.BLOCKED);
                    friendShipRepository.save(friendShip);
                }
                return null;
            } else {
                return generateErrorRs(NO_DATA_FOUND, "error while block/unblock Person, destination person not found");
            }
        } else {
            return generateErrorRs(NO_DATA_FOUND, "error while block/unblock Person, current person not found");
        }
    }

    public ApiFatherRs getFriendsOfCurrentUser(String authorization, int offset, int perPage) {
        Person currentPerson = getAuthorizedUser(authorization);
        if (currentPerson != null) {
            long total = personRepository.findCountPersonsByFriendship(currentPerson.getId(), FriendShipStatus.FRIEND.name());
            Page<Person> personsPage = personRepository.findPersonsByFriendship(
                    currentPerson.getId(), FriendShipStatus.FRIEND.name(), PageRequest.of(offset, perPage));
            CommonRsListPersonRs<PersonRs> personsList = new CommonRsListPersonRs<>();
            ArrayList<PersonRs> personsData = new ArrayList<>();

            Iterator<Person> iterator = personsPage.iterator();
            while (iterator.hasNext()) {
                Person person = iterator.next();
                log.info(person.getId() + " " + person.getFirstName());
                PersonRs personRs = new PersonRs();
                AssignPersonRsFieldsByPerson(personRs, person, FriendShipStatus.FRIEND, false);
                personsData.add(personRs);
            }
            personsList.setData(personsData);
            personsList.setItemPerPage(perPage);
            personsList.setOffset(offset);
            personsList.setPerPage(perPage);
            personsList.setTotal(total);
            return personsList;
        } else {
            return generateErrorRs(NO_DATA_FOUND, "error while get FriendshipList, current person not found");
        }
    }

    public ApiFatherRs getPotentialFriendsOfCurrentUser(String authorization, int offset, int perPage) {
        Person currentPerson = getAuthorizedUser(authorization);
        if (currentPerson != null) {
            long total = personRepository.findCountPersonsByFriendship(currentPerson.getId(), FriendShipStatus.REQUEST.name());
            Page<Person> personsPage = personRepository.findPersonsByFriendship(
                    currentPerson.getId(), FriendShipStatus.REQUEST.name(), PageRequest.of(offset, perPage));
            CommonRsListPersonRs<PersonRs> personsList = new CommonRsListPersonRs<>();
            ArrayList<PersonRs> personsData = new ArrayList<>();
            while (personsPage.iterator().hasNext()) {
                Person person = personsPage.iterator().next();
                PersonRs personRs = new PersonRs();
                AssignPersonRsFieldsByPerson(personRs, person, FriendShipStatus.FRIEND, false);
                personsData.add(personRs);
            }
            personsList.setData(personsData);
            personsList.setItemPerPage(perPage);
            personsList.setOffset(offset);
            personsList.setPerPage(perPage);
            personsList.setTotal(total);
            return personsList;
        } else {
            return generateErrorRs(NO_DATA_FOUND, "error while get FriendshipList, current person not found");
        }
    }

    public ApiFatherRs getRecommendationFriends(String authorization) {
        Person currentPerson = getAuthorizedUser(authorization);
        if (currentPerson != null) {
/*ToDo выяснить алгоритм получения списка рекомендованных друзей и написать имплементацию метода
    Список рекомендованных друзей решил получать так:
    если нет друзей, то рандомом из таблицы Person 10 записей.
    если друзья есть, то предлагать друзей друзей и если кол-во < 10, то дополнять рандомом до 10
*/
            return null;
        } else {
            return generateErrorRs(NO_DATA_FOUND, "error while get reccomendationFriendsList, current person not found");
        }
    }

    public ApiFatherRs getOutgoingRequestsByUser(String authorization, int offset, int perPage) {
        Person currentPerson = getAuthorizedUser(authorization);
        if (currentPerson != null) {
            long total = personRepository.findCountPersonsByFriendship(currentPerson.getId(), FriendShipStatus.REQUEST.name());
            Page<Person> personsPage = personRepository.findPersonsByFriendship(
                    currentPerson.getId(), FriendShipStatus.REQUEST.name(), PageRequest.of(offset, perPage));
            CommonRsListPersonRs<PersonRs> personsList = new CommonRsListPersonRs<>();
            ArrayList<PersonRs> personsData = new ArrayList<>();
            while (personsPage.iterator().hasNext()) {
                Person person = personsPage.iterator().next();
                PersonRs personRs = new PersonRs();
                AssignPersonRsFieldsByPerson(personRs, person, FriendShipStatus.REQUEST, false);
                personsData.add(personRs);
            }
            personsList.setData(personsData);
            personsList.setItemPerPage(perPage);
            personsList.setOffset(offset);
            personsList.setPerPage(perPage);
            personsList.setTotal(total);
            return personsList;
        } else {
            return generateErrorRs(NO_DATA_FOUND, "error while get FriendshipList, current person not found");
        }
    }

    private void AssignPersonRsFieldsByPerson(PersonRs personRs, Person person, FriendShipStatus friendShipStatus, Boolean isBlocked) {
        personRs.setAbout(person.getAbout());
        personRs.setCity(person.getCity());
        personRs.setCountry(person.getCountry());
        personRs.setCurrency(null);
        personRs.setEmail(person.getEmail());
        personRs.setId(person.getId());
        if (person.getOnlineStatus() != null) {
            personRs.setOnline(person.getOnlineStatus().equalsIgnoreCase("ONLINE"));
        } else {
            personRs.setOnline(false);
        }
        personRs.setPhone(person.getPhone());
        personRs.setPhoto(person.getPhoto());
        personRs.setToken(person.getChangePasswordToken());
        personRs.setWeather(null);
        personRs.setBirth_date(String.valueOf(person.getBirthDate()));
        personRs.setFirst_name(person.getFirstName());
        personRs.setFriend_status(friendShipStatus.name());
        personRs.setIs_blocked(person.getIsBlocked());
        personRs.setIs_blocked_by_current_user(isBlocked);
        personRs.setLast_name(person.getLastName());
        personRs.setLast_online_time(person.getLastOnlineTime().toString());
        personRs.setMessages_permission(String.valueOf(person.getMessagePermission()));
        personRs.setReg_date(String.valueOf(person.getRegDate()));
        personRs.setUser_deleted(person.getIsDeleted());
    }
}
