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
import java.util.*;

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
        Optional<Person> person = personRepository.findPesonByemail("tbartlet4@gizmodo.com");
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
                Iterable<FriendShip> friendShips = friendShipRepository.getFriendShipByIdsAndStatus(
                        currentPerson.getId(),  destinationPersonId, FriendShipStatus.REQUEST.name());
                //текущая персона отправляет запрос на дружбу (если такой запрос уже был, то проапдейтится дата запроса)
                FriendShip friendShip = friendShips.iterator().hasNext()
                        ? friendShips.iterator().next()
                        : new FriendShip();
                friendShip.setSourcePerson(currentPerson);
                friendShip.setDestinationPerson(destinationPerson.get());
                friendShip.setSentTime(LocalDateTime.now());
                friendShip.setStatus(FriendShipStatus.REQUEST);
                friendShipRepository.save(friendShip);
                //принимающая сторона принимает запрос на дружбу (если запись в таблице уже есть, то проапдейтится поле даты)
                friendShips = friendShipRepository.getFriendShipByIdsAndStatus(
                        destinationPersonId, currentPerson.getId(), FriendShipStatus.RECEIVED_REQUEST.name());
                friendShip = friendShips.iterator().hasNext()
                        ? friendShips.iterator().next()
                        : new FriendShip();
                friendShip.setSourcePerson(destinationPerson.get());
                friendShip.setDestinationPerson(currentPerson);
                friendShip.setSentTime(LocalDateTime.now());
                friendShip.setStatus(FriendShipStatus.RECEIVED_REQUEST);
                friendShipRepository.save(friendShip);
                return generateCommonRsComplexRs();
            } else {
                return generateErrorRs(NO_DATA_FOUND, "error while create Friendship request, destination person not found");
            }
        } else {
            return generateErrorRs(NO_DATA_FOUND, "error while create Friendship request, current person not found");
        }
    }

    public ApiFatherRs deleteFriendById(int destinationPersonId, String authorization) {
        Person currentPerson = getAuthorizedUser(authorization);
        if (currentPerson != null) {
            long currentPersonId = currentPerson.getId();
            Optional<Person> destinationPerson = personRepository.findById((long) destinationPersonId);
            if (destinationPerson.isPresent()) {
                //убеждаемся, что у каждой из персон есть записи о том, что они друзья, и удаляем эти записи
                Iterable<FriendShip> friendShips = friendShipRepository.getFriendShipByIdsAndStatus(currentPersonId, destinationPerson.get().getId(), FriendShipStatus.FRIEND.name());
                Iterable<FriendShip> friendShips2 = friendShipRepository.getFriendShipByIdsAndStatus(destinationPerson.get().getId(), currentPersonId, FriendShipStatus.FRIEND.name());
                if (friendShips.iterator().hasNext() && friendShips2.iterator().hasNext()) {
                    FriendShip friendShip = friendShips.iterator().next();
                    friendShipRepository.delete(friendShip);
                    friendShip =  friendShips2.iterator().next();
                    friendShipRepository.delete(friendShip);
                    return generateCommonRsComplexRs();
                } else {
                    return generateErrorRs(NO_DATA_FOUND, "error while delete Friendship, Friendship not found");
                }
            } else {
                return generateErrorRs(NO_DATA_FOUND, "error while delete Friendship, destination person not found");
            }
        } else {
            return generateErrorRs(NO_DATA_FOUND, "error while delete Friendship, current person not found");
        }
    }

    public ApiFatherRs addFriendById(int destinationPersonId, String authorization) {
        Person currentPerson = getAuthorizedUser(authorization);
        if (currentPerson != null) {
            long currentPersonId = currentPerson.getId();
            Optional<Person> destinationPerson = personRepository.findById((long) destinationPersonId);
            if (destinationPerson.isPresent()) {
                Iterable<FriendShip> friendShips = friendShipRepository.getFriendShipByIdsAndStatus(currentPersonId, destinationPerson.get().getId(), FriendShipStatus.RECEIVED_REQUEST.name());
                if (!friendShips.iterator().hasNext()) {
                    return generateErrorRs(NO_DATA_FOUND, "error while add Friendship, dont find received request");
                }  else {
                    //удалить все запросы в друзья и т.д. !!! у обеих персон
                    friendShipRepository.delRelationsFromPersons(currentPersonId, destinationPerson.get().getId());
                    //добавить друга для первой персоны
                    FriendShip friendShip = new FriendShip();
                    friendShip.setSourcePerson(currentPerson);
                    friendShip.setDestinationPerson(destinationPerson.get());
                    friendShip.setSentTime(LocalDateTime.now());
                    friendShip.setStatus(FriendShipStatus.FRIEND);
                    friendShipRepository.save(friendShip);
                    //добавить друга для второй персоны
                    friendShip = new FriendShip();
                    friendShip.setSourcePerson(destinationPerson.get());
                    friendShip.setDestinationPerson(currentPerson);
                    friendShip.setSentTime(LocalDateTime.now());
                    friendShip.setStatus(FriendShipStatus.FRIEND);
                    friendShipRepository.save(friendShip);
                    return generateCommonRsComplexRs();
                }
            } else {
                return generateErrorRs(NO_DATA_FOUND, "error while add Friendship, destination person not found");
            }
        } else {
            return generateErrorRs(NO_DATA_FOUND, "error while add Friendship, current person not found");
        }
    }

    public ApiFatherRs declineFriendshipRequestById(int destinationPersonId, String authorization) {
        Person currentPerson = getAuthorizedUser(authorization);
        if (currentPerson != null) {
            long currentPersonId = currentPerson.getId();
            Optional<Person> destinationPerson = personRepository.findById((long) destinationPersonId);
            if (destinationPerson.isPresent()) {
                //ищем приходящий запрос на добавление в друзья у одной стороны и запрос дружбы с другой стороны
                Iterable<FriendShip> friendShips = friendShipRepository.getFriendShipByIdsAndStatus(currentPersonId, destinationPerson.get().getId(), FriendShipStatus.RECEIVED_REQUEST.name());
                Iterable<FriendShip> friendShips2 = friendShipRepository.getFriendShipByIdsAndStatus(destinationPerson.get().getId(), currentPersonId, FriendShipStatus.REQUEST.name());
                //если найдены запросы, то удалить их (DECLINE статуса не предусмотрено)
                if (friendShips.iterator().hasNext()) {
                    FriendShip friendShip = friendShips.iterator().next();
                    friendShipRepository.delete(friendShip);
                } else {
                    return generateErrorRs(NO_DATA_FOUND, "error while decline Friendship, Friendship request not found");
                }
                if (friendShips2.iterator().hasNext()) {
                    FriendShip friendShip = friendShips2.iterator().next();
                    friendShipRepository.delete(friendShip);
                }
                return generateCommonRsComplexRs();
            } else {
                return generateErrorRs(NO_DATA_FOUND, "error while decline Friendship, destination person not found");
            }
        } else {
            return generateErrorRs(NO_DATA_FOUND, "error while decline Friendship, current person not found");
        }
    }

    public ApiFatherRs blockOrUnblockUserByUser(int destinationPersonId, String authorization) {
        Person currentPerson = getAuthorizedUser(authorization);
        if (currentPerson != null) {
            long currentPersonId = currentPerson.getId();
            Optional<Person> destinationPerson = personRepository.findById((long) destinationPersonId);
            if (destinationPerson.isPresent()) {
                Iterable<FriendShip> friendShips = friendShipRepository.getFriendShipByIdsAndStatus(currentPersonId, destinationPerson.get().getId(), FriendShipStatus.BLOCKED.name());
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
            long total = personRepository.findCountPersonsByFriendship(currentPerson.getId(), FriendShipStatus.RECEIVED_REQUEST.name());
            Page<Person> personsPage = personRepository.findPersonsByFriendship(
                    currentPerson.getId(), FriendShipStatus.RECEIVED_REQUEST.name(), PageRequest.of(offset, perPage));
            CommonRsListPersonRs<PersonRs> personsList = new CommonRsListPersonRs<>();
            ArrayList<PersonRs> personsData = new ArrayList<>();
            Iterator<Person> iterator = personsPage.iterator();
            while (iterator.hasNext()) {
                Person person = iterator.next();
                PersonRs personRs = new PersonRs();
                AssignPersonRsFieldsByPerson(personRs, person, FriendShipStatus.RECEIVED_REQUEST, false);
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

    private ArrayList<Person> getRandomPersonsList(List<Long> personIds, int cnt) {
        Iterable<Person> personsIterable = personRepository.randomGenerateFriendsForPerson(personIds, cnt);
        Iterator<Person> personIterator = personsIterable.iterator();
        ArrayList<Person> persons = new ArrayList<>();
        while (personIterator.hasNext()) {
            persons.add(personIterator.next());
        }
        return persons;
    }

    private ArrayList<Person> getFriendsOfFriendsByPerson(long personId) {
        Iterable<Person> personsIterable = personRepository.getFriendsOfFriendsByPersonId(personId);
        Iterator<Person> personIterator = personsIterable.iterator();
        ArrayList<Person> persons = new ArrayList<>();
        while (personIterator.hasNext()) {
            persons.add(personIterator.next());
        }
        return persons;
    }


    private ArrayList<PersonRs> updatePersonsData(ArrayList<Person> persons, Person currentPerson) {
        ArrayList<PersonRs> personsData = new ArrayList<>();
        for (Person person : persons) {
            PersonRs personRs = new PersonRs();
            Optional<FriendShipStatus> optionalStatus = friendShipRepository.getFriendhipStatusBetweenPersons(currentPerson, person);
            FriendShipStatus status = optionalStatus.orElse(FriendShipStatus.UNKNOWN);
            AssignPersonRsFieldsByPerson(personRs, person, status, status == FriendShipStatus.BLOCKED);
            personsData.add(personRs);
        }
        return personsData;
    }

    public ApiFatherRs getRecommendationFriends(String authorization) {
        Person currentPerson = getAuthorizedUser(authorization);
        if (currentPerson != null) {
            CommonRsListPersonRs<PersonRs> personsList = new CommonRsListPersonRs<>();
            ArrayList<Long> personsIds = new ArrayList<>();
            ArrayList<PersonRs> personsData = new ArrayList<>();
// Список рекомендованных друзей решил получать так:
// посмотрим, есть ли у персоны друзья?
            long friendsCount = personRepository.findCountPersonsByFriendship(currentPerson.getId(), FriendShipStatus.FRIEND.name());
//  если нет друзей, то предлагаем рандомом из таблицы Person 10 записей.
            if (friendsCount == 0) {
                personsIds.add(currentPerson.getId());
                ArrayList<Person>  persons = getRandomPersonsList(personsIds, 10);
                personsData = updatePersonsData(persons, currentPerson);
            } else {
//    если друзья есть, то предлагать друзей друзей, и если их кол-во < 10, то дополнять рандомом до 10
                ArrayList<Person> persons = getFriendsOfFriendsByPerson(currentPerson.getId());
                int size = persons.size();
                if (size < 10) {
                    personsIds.add(currentPerson.getId());
                    for (Person person : persons) {
                        personsIds.add(person.getId());
                    }
                    ArrayList<Person> persons2 = getRandomPersonsList(personsIds, 10 - size);
                    persons.addAll(persons2);
                }
                personsData = updatePersonsData(persons, currentPerson);
            }
            personsList.setData(personsData);
            personsList.setItemPerPage(personsData.size());
            personsList.setOffset(0);
            personsList.setPerPage(personsData.size());
            personsList.setTotal((long)personsData.size());
            return personsList;
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
            Iterator<Person> iterator = personsPage.iterator();
            while (iterator.hasNext()) {
                Person person = iterator.next();
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
        personRs.setOnline(person.getOnlineStatus() == null ? false : person.getOnlineStatus().equalsIgnoreCase("TRUE"));
        personRs.setPhone(person.getPhone());
        personRs.setPhoto(person.getPhoto());
        personRs.setToken(person.getChangePasswordToken());
        personRs.setWeather(null);
        personRs.setBirth_date(person.getBirthDate() == null ? null : person.getBirthDate().toString());
        personRs.setFirst_name(person.getFirstName());
        personRs.setFriend_status(friendShipStatus.name());
        personRs.setIs_blocked(person.getIsBlocked());
        personRs.setIs_blocked_by_current_user(isBlocked);
        personRs.setLast_name(person.getLastName());
        personRs.setLast_online_time(person.getLastOnlineTime() == null ? null : person.getLastOnlineTime().toString());
        personRs.setMessages_permission(person.getMessagePermission() == null ? null : person.getMessagePermission().toString());
        personRs.setReg_date(person.getRegDate() == null ? null : person.getRegDate().toString());
        personRs.setUser_deleted(person.getIsDeleted());
    }
}
