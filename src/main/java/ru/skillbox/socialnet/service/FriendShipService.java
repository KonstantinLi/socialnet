package ru.skillbox.socialnet.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
import ru.skillbox.socialnet.exception.FriendShipNotFoundExeption;
import ru.skillbox.socialnet.exception.PersonNotFoundExeption;
import ru.skillbox.socialnet.repository.FriendShipRepository;
import ru.skillbox.socialnet.repository.PersonRepository;
import ru.skillbox.socialnet.security.util.JwtTokenUtils;
import ru.skillbox.socialnet.util.mapper.PersonMapper;

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
       // response.setData(complexRsList);
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
     * @throws PersonNotFoundExeption - может быть сгенерировано исключение, если персона,
     * с которой хотят дружить не найдена
     */
    public CommonRsComplexRs<ComplexRs> sendFriendshipRequest(Long destinationPersonId, String authorization)
            throws PersonNotFoundExeption {
        Person currentPerson = getAuthorizedUser(authorization);
        Person destinationPerson = personRepository.findByIdImpl( destinationPersonId);
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
     * @throws PersonNotFoundExeption - может быть сгенерировано исключение, если не найдена персона, с которой
     * хотят удалить дружбу, или не найдены записи о дружбе
     * @throws FriendShipNotFoundExeption - может быть сгенерировано исключение, если не в таблице friendships
     * не найдены запиаи со статусом FRIEND для обеих персон
     */
    public CommonRsComplexRs<ComplexRs> deleteFriendById(Long destinationPersonId, String authorization)
            throws PersonNotFoundExeption, FriendShipNotFoundExeption {
        Person currentPerson = getAuthorizedUser(authorization);
        Person destinationPerson = personRepository.findByIdImpl(destinationPersonId);
        //убеждаемся, что у каждой из персон есть записи о том, что они друзья, и удаляем эти записи
        FriendShip friendShip = friendShipRepository.getFriendShipByIdsAndStatusImpl(
                currentPerson.getId(), destinationPerson.getId(), FriendShipStatus.FRIEND);
        FriendShip friendShip2 = friendShipRepository.getFriendShipByIdsAndStatusImpl(
                destinationPerson.getId(), currentPerson.getId(), FriendShipStatus.FRIEND);
        friendShipRepository.delete(friendShip);
        friendShipRepository.delete(friendShip2);
        return generateCommonRsComplexRs();
    }

    /**
     *
     * @param destinationPersonId  - персона, которую хотят добавить в друзья
     * @param authorization  - токен авторизации, по нему будет вычислен текущий пользователь
     * @return  - метод сервиса вернет объект на POST запрос "/api/v1/friends/request/{id}"
     * @throws PersonNotFoundExeption - может быть сгенерировано исключение, если не найдена персона, которую
     * хотят добавить в друзья
     * @throws FriendShipNotFoundExeption - может быть сгенерировано исключение, если не найден RECEIVED_REQUEST
     * у принимающей стороны или REQUEST у передающей стороны
     */
    public CommonRsComplexRs<ComplexRs> addFriendById(Long destinationPersonId, String authorization)
            throws PersonNotFoundExeption, FriendShipNotFoundExeption {
        Person currentPerson = getAuthorizedUser(authorization);
        Person destinationPerson = personRepository.findByIdImpl(destinationPersonId);
        FriendShip friendShip = friendShipRepository.getFriendShipByIdsAndStatusImpl(
                currentPerson.getId(), destinationPerson.getId(), FriendShipStatus.RECEIVED_REQUEST);
        saveFriendhipChanges(friendShip, currentPerson, destinationPerson, LocalDateTime.now(), FriendShipStatus.FRIEND);
        friendShip = friendShipRepository.getFriendShipByIdsAndStatusImpl(
                currentPerson.getId(), destinationPerson.getId(), FriendShipStatus.REQUEST);
        saveFriendhipChanges(friendShip,destinationPerson,currentPerson,LocalDateTime.now(),FriendShipStatus.FRIEND);
        return generateCommonRsComplexRs();
    }

    /**
     *
     * @param destinationPersonId - персона, с которой хотят отклонить дружбу
     * @param authorization - токен авторизации, по нему будет вычислен текущий пользователь
     * @return  - метод сервиса вернет объект на DELETE запрос "/api/v1/friends/request/{id}"
     * @throws PersonNotFoundExeption- может быть сгенерировано исключение, если не найдена персона, c которой
     * хотят отклонить дружбу
     * @throws FriendShipNotFoundExeption - может быть сгенерировано исключение, если не найден RECEIVED_REQUEST
     * c одной стороны или REQUEST с другой стороны
     */
    public CommonRsComplexRs<ComplexRs> declineFriendshipRequestById(Long destinationPersonId, String authorization)
            throws PersonNotFoundExeption, FriendShipNotFoundExeption {
        Person currentPerson = getAuthorizedUser(authorization);
        Person destinationPerson = personRepository.findByIdImpl(destinationPersonId);
        //ищем приходящий запрос на добавление в друзья у одной стороны и запрос дружбы с другой стороны
        FriendShip friendShip = friendShipRepository.getFriendShipByIdsAndStatusImpl(
                currentPerson.getId(), destinationPerson.getId(), FriendShipStatus.RECEIVED_REQUEST);
        //если найдены запросы, то удалить их (DECLINE статуса не предусмотрено)
        friendShipRepository.delete(friendShip);
        FriendShip friendShip2 = friendShipRepository.getFriendShipByIdsAndStatusImpl(
                destinationPerson.getId(), currentPerson.getId(), FriendShipStatus.REQUEST);
        //если найдены запросы, то удалить их (DECLINE статуса не предусмотрено)
        friendShipRepository.delete(friendShip);
        return generateCommonRsComplexRs();
    }

    /**
     *
     * @param destinationPersonId - персона, которую хотят блокировать/разблокировать
     * @param authorization - токен авторизации, по нему будет вычислен текущий пользователь
     * @throws PersonNotFoundExeption - может быть сгенерировано исключение, если не найдена персона, которую
     * хотят блокировать/разблокировать
     * return void - мeтод ничего не возвращает
     * метод вызывается, когда на контроллер поступает POST запрос  "/api/v1/friends/block_unblock/{id}"
     */
    public void blockOrUnblockUserByUser(Long destinationPersonId, String authorization)
            throws PersonNotFoundExeption {
        Person currentPerson = getAuthorizedUser(authorization);
        Person destinationPerson = personRepository.findByIdImpl(destinationPersonId);
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
        ArrayList<PersonRs> personsData = generatePersonsData(personsPage, FriendShipStatus.FRIEND.name(), false);
        CommonRsListPersonRs<PersonRs> personsList = generatePersonsList(personsData, offset, perPage, total);
        return personsList;
    }

    /**
     *
     * @param personsPage  - выборка персон из запроса (в репозитории)
     * @param friendShipStatus - статус дружбы (строка)
     * @param isBlocked  - параметр блокрована ли запись
     * @return - вспомогательный метод (возвращает список персон в новом формате)
     */
    private ArrayList<PersonRs> generatePersonsData(Page<Person> personsPage, String friendShipStatus, Boolean isBlocked) {
        ArrayList<PersonRs> personsData = new ArrayList<>();
        personsPage.forEach(person -> {
            PersonRs personRs = PersonMapper.INSTANCE.personToPersonRs(person, friendShipStatus, isBlocked);
            personsData.add(personRs);
        });
        return personsData;
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
        ArrayList<PersonRs> personsData = generatePersonsData(personsPage, FriendShipStatus.RECEIVED_REQUEST.name(), false);
        CommonRsListPersonRs<PersonRs> personsList = generatePersonsList(personsData, offset, perPage, total);
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
        ArrayList<Person> persons = new ArrayList<>();
        personsIterable.forEach(person -> persons.add(person));
        return persons;
    }

    /**
     *
     * @param personId - ID персоны
     * @return - вспомогательный метод (вернет список персон, которые в друзьях у друзей персоны)
     */
    private ArrayList<Person> getFriendsOfFriendsByPerson(long personId) {
        Iterable<Person> personsIterable = personRepository.getFriendsOfFriendsByPersonId(personId);
        ArrayList<Person> persons = new ArrayList<>();
        personsIterable.forEach(person -> persons.add(person));
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
        CommonRsListPersonRs<PersonRs> personsList = generatePersonsList(personsData, 0,
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
    private CommonRsListPersonRs<PersonRs>  generatePersonsList(ArrayList<PersonRs> personsData,
                                                           int offSet, int perPage, long total) {
        CommonRsListPersonRs<PersonRs> personsList = new CommonRsListPersonRs<>();
        personsList.setData(personsData);
        personsList.setItemPerPage(perPage);
        personsList.setOffset(offSet);
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
        ArrayList<PersonRs> personsData = generatePersonsData(personsPage, FriendShipStatus.REQUEST.name(), false) ;
        CommonRsListPersonRs<PersonRs> personsList = generatePersonsList(personsData, offset, perPage, total);
        return personsList;
    }
}
