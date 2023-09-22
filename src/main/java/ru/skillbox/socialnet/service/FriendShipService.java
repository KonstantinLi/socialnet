package ru.skillbox.socialnet.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.skillbox.socialnet.dto.ComplexRs;
import ru.skillbox.socialnet.dto.PersonRs;
import ru.skillbox.socialnet.dto.response.CommonRsComplexRs;
import ru.skillbox.socialnet.dto.response.CommonRsListPersonRs;
import ru.skillbox.socialnet.entity.FriendShip;
import ru.skillbox.socialnet.entity.Person;
import ru.skillbox.socialnet.entity.enums.FriendShipStatus;
import ru.skillbox.socialnet.errs.BadRequestException;
import ru.skillbox.socialnet.security.util.JwtTokenUtils;
import ru.skillbox.socialnet.util.mapper.PersonMapper;
import ru.skillbox.socialnet.repository.FriendShipRepository;
import ru.skillbox.socialnet.repository.PersonRepository;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class FriendShipService {
    private final FriendShipRepository friendShipRepository;
    private final PersonRepository personRepository;
    private final JwtTokenUtils jwtTokenUtils;

    /**
     *
     * @param token   - токен авторизации
     * @return  - функция вернет пользователя (персону) по токену авторизации
     */
    public Person getAuthorizedUser(String token) {
        Long id = jwtTokenUtils.getId(token);
        Optional<Person> person = personRepository.findById(id);
        return person.get();
    }

    /**
     *
     * @return вспомогательная функция. Возвращает форматированный объект CommonRsComplexRs<ComplexRs>
     *     пока заполняется пустыми значениями
     *
     */
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

    /**
     *
     * @param friendShip      - объект, состояние которого будет записано в базу
     * @param sourcePerson    - персона 1
     * @param sentTime        - персона 2
     * @param status          - статус дружбы между персонами
     * вспомогательный метод
     * процедура делает запись в базу (таблица friendships) по входным параметрам
     */
    private void saveFriendhipChanges(FriendShip friendShip,
                                      Person sourcePerson,
                                      Person destinationPerson,
                                      LocalDateTime sentTime,
                                      FriendShipStatus status) {
        friendShip.setSourcePerson(sourcePerson);
        friendShip.setDestinationPerson(destinationPerson);
        friendShip.setSentTime(sentTime);
        friendShip.setStatus(status);
        friendShipRepository.save(friendShip);
    }

    /**
     *
     * @param destinationPersonId - персона, которой отправляется запрос на дружбу
     * @param authorization - токен авторизации, по нему будет вычислен текущий пользователь
     * @return  - метод сервиса вернет объект на POST запрос  "/api/v1/friends/{id}"
     * @throws BadRequestException - может быть сгенерировано исключение, если персона,
     * с которой хотят дружить не найдена
     */
    public CommonRsComplexRs<ComplexRs> sendFriendshipRequest(int destinationPersonId, String authorization)
            throws BadRequestException {
        Person currentPerson = getAuthorizedUser(authorization);
        Person destinationPerson = personRepository.findById((long) destinationPersonId)
                .orElseThrow(() -> new BadRequestException("не найдена запись о пользователе."));
        FriendShip friendShip = friendShipRepository.getFriendShipByIdsAndStatus(
                        currentPerson.getId(), destinationPersonId, FriendShipStatus.REQUEST.name())
                .orElse(new FriendShip());
        //текущая персона отправляет запрос на дружбу
        saveFriendhipChanges(friendShip, currentPerson, destinationPerson, LocalDateTime.now(),
                FriendShipStatus.REQUEST);
        friendShip = friendShipRepository.getFriendShipByIdsAndStatus(
                        destinationPersonId, currentPerson.getId(), FriendShipStatus.RECEIVED_REQUEST.name())
                .orElse(new FriendShip());
        //принимающая сторона принимает запрос на дружбу
        saveFriendhipChanges(friendShip, destinationPerson, currentPerson, LocalDateTime.now(),
                FriendShipStatus.RECEIVED_REQUEST);
        return generateCommonRsComplexRs();
    }

    /**
     *
     * @param destinationPersonId - персона, которую хотят удалить из друзей
     * @param authorization - токен авторизации, по нему будет вычислен текущий пользователь
     * @return  - метод сервиса вернет объект на DELETE запрос "/api/v1/friends/{id}"
     * @throws BadRequestException - может быть сгенерировано исключение, если не найдена персона, с которой
     * хотят удалить дружбу, или не найдены записи о дружбе
     */
    public CommonRsComplexRs<ComplexRs> deleteFriendById(int destinationPersonId, String authorization)
            throws BadRequestException {
        Person currentPerson = getAuthorizedUser(authorization);
        Person destinationPerson = personRepository.findById((long) destinationPersonId)
                .orElseThrow(() -> new BadRequestException("не найдена запись о пользователе."));
        //убеждаемся, что у каждой из персон есть записи о том, что они друзья, и удаляем эти записи
        FriendShip friendShip = friendShipRepository.getFriendShipByIdsAndStatus(
                currentPerson.getId(), destinationPerson.getId(), FriendShipStatus.FRIEND.name())
                .orElseThrow(() -> new BadRequestException("не найдена запись о дружбе"));
        FriendShip friendShip2 = friendShipRepository.getFriendShipByIdsAndStatus(
                destinationPerson.getId(), currentPerson.getId(), FriendShipStatus.FRIEND.name())
                .orElseThrow(() ->  new BadRequestException("не найдена запись о дружбе"));
        friendShipRepository.delete(friendShip);
        friendShipRepository.delete(friendShip2);
        return generateCommonRsComplexRs();
    }

    /**
     *
     * @param destinationPersonId - персона, которую хотят добавить в друзья
     * @param authorization - токен авторизации, по нему будет вычислен текущий пользователь
     * @return  - метод сервиса вернет объект на POST запрос "/api/v1/friends/request/{id}"
     * @throws BadRequestException- может быть сгенерировано исключение, если не найдена персона, которую
     *      * хотят добавить в друзья
     */
    public CommonRsComplexRs<ComplexRs> addFriendById(int destinationPersonId, String authorization)
            throws BadRequestException {
        Person currentPerson = getAuthorizedUser(authorization);
        Person destinationPerson = personRepository.findById((long) destinationPersonId)
                .orElseThrow(() -> new BadRequestException("запись о пользователе не найдена"));
        FriendShip friendShip = friendShipRepository.getFriendShipByIdsAndStatus(
                currentPerson.getId(), destinationPerson.getId(), FriendShipStatus.RECEIVED_REQUEST.name())
                .orElseThrow(() -> new BadRequestException("не найдена запись запросе на добавление в друзья"));
        saveFriendhipChanges(friendShip, currentPerson, destinationPerson, LocalDateTime.now(), FriendShipStatus.FRIEND);
        friendShip = friendShipRepository.getFriendShipByIdsAndStatus(
                currentPerson.getId(), destinationPerson.getId(), FriendShipStatus.REQUEST.name())
                .orElseThrow(()-> new BadRequestException("не найдена запись запроса на дружбу"));
        saveFriendhipChanges(friendShip,destinationPerson,currentPerson,LocalDateTime.now(),FriendShipStatus.FRIEND);
        return generateCommonRsComplexRs();
    }

    /**
     *
     * @param destinationPersonId - персона, с которой хотят отклонить дружбу
     * @param authorization - токен авторизации, по нему будет вычислен текущий пользователь
     * @return  - метод сервиса вернет объект на DELETE запрос "/api/v1/friends/request/{id}"
     * @throws BadRequestException- может быть сгенерировано исключение, если не найдена персона, c которой
     * хотят отклонить дружбу или записи о запросах на дружбу
     */
    public CommonRsComplexRs<ComplexRs> declineFriendshipRequestById(int destinationPersonId, String authorization)
            throws BadRequestException {
        Person currentPerson = getAuthorizedUser(authorization);
        Person destinationPerson = personRepository.findById((long) destinationPersonId)
                .orElseThrow(() -> new BadRequestException("не найдена запись о пользователе."));
        //ищем приходящий запрос на добавление в друзья у одной стороны и запрос дружбы с другой стороны
        FriendShip friendShip = friendShipRepository.getFriendShipByIdsAndStatus(
                currentPerson.getId(), destinationPerson.getId(), FriendShipStatus.RECEIVED_REQUEST.name())
                .orElseThrow(()-> new BadRequestException("не найдена запись о запросе на добавление в друзья"));
        //если найдены запросы, то удалить их (DECLINE статуса не предусмотрено)
        friendShipRepository.delete(friendShip);
        FriendShip friendShip2 = friendShipRepository.getFriendShipByIdsAndStatus(
                destinationPerson.getId(), currentPerson.getId(), FriendShipStatus.REQUEST.name())
                .orElseThrow(()-> new BadRequestException("не найдена запись о запросе на дружбу"));
        //если найдены запросы, то удалить их (DECLINE статуса не предусмотрено)
        friendShipRepository.delete(friendShip);
        return generateCommonRsComplexRs();
    }

    /**
     *
     * @param destinationPersonId - персона,  которую хотят блокировать/разблокировать
     * @param authorization - токен авторизации, по нему будет вычислен текущий пользователь
     * @throws BadRequestException- может быть сгенерировано исключение, если не найдена персона, которую
     * хотят блокировать/разблокировать
     * return void - мeтод ничего не возвращает
     * метод вызывается, когда на контроллер поступает POST запрос  "/api/v1/friends/block_unblock/{id}"
     */
    public void blockOrUnblockUserByUser(int destinationPersonId, String authorization)
            throws BadRequestException {
        Person currentPerson = getAuthorizedUser(authorization);
        Person destinationPerson = personRepository.findById((long) destinationPersonId)
                .orElseThrow(() -> new BadRequestException("не найдена запись о пользователе."));
        FriendShip friendShip = friendShipRepository.getFriendShipByIdsAndStatus(
                currentPerson.getId(), destinationPerson.getId(), FriendShipStatus.BLOCKED.name())
                .orElse(null);
        //если найдена запись с заблокированной персоной, то надо эту запись удалить
        if (friendShip != null) {
            friendShipRepository.delete(friendShip);
        } else {
        //если запись не найдена, то надо персону заблокировать
            friendShip = new FriendShip();
            saveFriendhipChanges(friendShip, currentPerson, destinationPerson, LocalDateTime.now(),
                    FriendShipStatus.BLOCKED);
        }
    }

    /**
     *
     * @param authorization - токен авторизации, по нему будет вычислен текущий пользователь
     * @param offset - "отступ".. с какой страницы показывать список
     * @param perPage - количество записей на странице
     * @return - метод возвращает объект в ответ на приходящий на контроллер GET запрос "/api/v1/friends/"
     */
    public CommonRsListPersonRs<PersonRs> getFriendsOfCurrentUser(String authorization, int offset, int perPage) {
        Person currentPerson = getAuthorizedUser(authorization);
        long total = personRepository.findCountPersonsByFriendship(currentPerson.getId(), FriendShipStatus.FRIEND.name());
        Page<Person> personsPage = personRepository.findPersonsByFriendship(
                    currentPerson.getId(), FriendShipStatus.FRIEND.name(), PageRequest.of(offset, perPage));
        CommonRsListPersonRs<PersonRs> personsList = new CommonRsListPersonRs<>();
        ArrayList<PersonRs> personsData = new ArrayList<>();
        Iterator<Person> iterator = personsPage.iterator();
        while (iterator.hasNext()) {
            Person person = iterator.next();
            PersonRs personRs = PersonMapper.INSTANCE.personToPersonRs(person, FriendShipStatus.FRIEND.name(),
                    false);
            personsData.add(personRs);
        }
        personsList.setData(personsData);
        personsList.setItemPerPage(perPage);
        personsList.setOffset(offset);
        personsList.setPerPage(perPage);
        personsList.setTotal(total);
        return personsList;
    }

    /**
     *
     * @param authorization - токен авторизации, по нему будет вычислен текущий пользователь
     * @param offset - "отступ".. с какой страницы показывать список
     * @param perPage - количество записей на странице
     * @return - метод возвращает объект в ответ на приходящий на контроллер GET запрос "/api/v1/friends/request"
     */
    public CommonRsListPersonRs<PersonRs> getPotentialFriendsOfCurrentUser(String authorization, int offset, int perPage) {
        Person currentPerson = getAuthorizedUser(authorization);
        long total = personRepository.findCountPersonsByFriendship(currentPerson.getId(),
                FriendShipStatus.RECEIVED_REQUEST.name());
        Page<Person> personsPage = personRepository.findPersonsByFriendship(
                    currentPerson.getId(), FriendShipStatus.RECEIVED_REQUEST.name(), PageRequest.of(offset, perPage));
        CommonRsListPersonRs<PersonRs> personsList = new CommonRsListPersonRs<>();
        ArrayList<PersonRs> personsData = new ArrayList<>();
        Iterator<Person> iterator = personsPage.iterator();
        while (iterator.hasNext()) {
            Person person = iterator.next();
            PersonRs personRs = PersonMapper.INSTANCE.personToPersonRs(person, FriendShipStatus.RECEIVED_REQUEST.name(),
                    false);
            personsData.add(personRs);
        }
        personsList.setData(personsData);
        personsList.setItemPerPage(perPage);
        personsList.setOffset(offset);
        personsList.setPerPage(perPage);
        personsList.setTotal(total);
        return personsList;
    }

    /**
     *
     * @param personIds - лист из ID персон, которых НЕ надо включать в список
     * @param cnt  - количество персон в списке (поддерживается до 100 персон)
     * @return - вспомогательный метод (вернет рандомный список персон)
     */
    private ArrayList<Person> getRandomPersonsList(List<Long> personIds, int cnt) {
        Iterable<Person> personsIterable = personRepository.randomGenerateFriendsForPerson(personIds, cnt);
        Iterator<Person> personIterator = personsIterable.iterator();
        ArrayList<Person> persons = new ArrayList<>();
        while (personIterator.hasNext()) {
            persons.add(personIterator.next());
        }
        return persons;
    }

    /**
     *
     * @param personId - ID персоны
     * @return - вспомогательный метод (вернет список персон, которые в друзьях у друзей персоны)
     */
    private ArrayList<Person> getFriendsOfFriendsByPerson(long personId) {
        Iterable<Person> personsIterable = personRepository.getFriendsOfFriendsByPersonId(personId);
        Iterator<Person> personIterator = personsIterable.iterator();
        ArrayList<Person> persons = new ArrayList<>();
        while (personIterator.hasNext()) {
            persons.add(personIterator.next());
        }
        return persons;
    }

    /**
     *
     * @param persons - список персон в формате ArrayList<Person>
     * @param currentPerson - текущая персона
     * @return - вспомогательный метод (вычисляет "статус дружбы" между персонами из списка и текущей персоной и
     * возвращает объект списка в формате ArrayList<PersonRs>
     */
    private ArrayList<PersonRs> updatePersonsData(ArrayList<Person> persons, Person currentPerson) {
        ArrayList<PersonRs> personsData = new ArrayList<>();
        for (Person person : persons) {
            Optional<FriendShipStatus> optionalStatus = friendShipRepository.getFriendhipStatusBetweenPersons(
                    currentPerson, person);
            FriendShipStatus status = optionalStatus.orElse(FriendShipStatus.UNKNOWN);
            PersonRs personRs = PersonMapper.INSTANCE.personToPersonRs(person, status.name(),
                    status == FriendShipStatus.BLOCKED);
            personsData.add(personRs);
        }
        return personsData;
    }

    /**
     *
     * @param authorization - токен авторизации, по нему будет вычислен текущий пользователь
     * @return - метод возвращает объект в ответ на приходящий на контроллер GET запрос "/api/v1/friends/recommendations"
     */
    public CommonRsListPersonRs<PersonRs> getRecommendationFriends(String authorization)  {
        Person currentPerson = getAuthorizedUser(authorization);
        ArrayList<Long> personsIds = new ArrayList<>();
        ArrayList<PersonRs> personsData;
// Список рекомендованных друзей решил получать так:
// посмотрим, есть ли у персоны друзья?
        long friendsCount = personRepository.findCountPersonsByFriendship(currentPerson.getId(),
                FriendShipStatus.FRIEND.name());
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
        CommonRsListPersonRs<PersonRs> personsList = getpersonsList(personsData, 0,
                personsData.size(), personsData.size());
        return personsList;
    }

    /**
     *
     * @param personsData - объект (список) в формате ArrayList<PersonRs> (список персон)
     * @param offSet      - "отступ"
     * @param perPage     - "на странице"
     * @param total       - "всего"
     * @return   - вспомогательный метод. Вернёт отформатированный список персон
     */
    private CommonRsListPersonRs<PersonRs>  getpersonsList(ArrayList<PersonRs> personsData,
                                                           int offSet, int perPage, long total) {
        CommonRsListPersonRs<PersonRs> personsList = new CommonRsListPersonRs<>();
        personsList.setData(personsData);
        personsList.setItemPerPage(perPage);
        personsList.setOffset(0);
        personsList.setPerPage(perPage);
        personsList.setTotal(total);
        return personsList;
    }

    /**
     *
     * @param authorization - токен авторизации, по нему будет вычислен текущий пользователь
     * @param offset - "отступ".. с какой страницы показывать список
     * @param perPage - количество записей на странице
     * @return - метод возвращает объект в ответ на приходящий на контроллер GET запрос "/api/v1/friends/outgoing_requests"
     */
    public CommonRsListPersonRs<PersonRs> getOutgoingRequestsByUser(String authorization, int offset, int perPage) {
        Person currentPerson = getAuthorizedUser(authorization);
        long total = personRepository.findCountPersonsByFriendship(currentPerson.getId(), FriendShipStatus.REQUEST.name());
        Page<Person> personsPage = personRepository.findPersonsByFriendship(
                    currentPerson.getId(), FriendShipStatus.REQUEST.name(), PageRequest.of(offset, perPage));
        ArrayList<PersonRs> personsData = new ArrayList<>();
        Iterator<Person> iterator = personsPage.iterator();
        while (iterator.hasNext()) {
            Person person = iterator.next();
            PersonRs personRs = PersonMapper.INSTANCE.personToPersonRs(person, FriendShipStatus.REQUEST.name(),
                    false);
            personsData.add(personRs);
        }
        CommonRsListPersonRs<PersonRs> personsList = getpersonsList(personsData, offset, perPage, total);
        return personsList;
    }
}
