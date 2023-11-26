package ru.skillbox.socialnet.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skillbox.socialnet.dto.response.CommonRs;
import ru.skillbox.socialnet.dto.response.ComplexRs;
import ru.skillbox.socialnet.dto.response.PersonRs;
import ru.skillbox.socialnet.entity.enums.FriendShipStatus;
import ru.skillbox.socialnet.entity.personrelated.FriendShip;
import ru.skillbox.socialnet.entity.personrelated.Person;
import ru.skillbox.socialnet.exception.FriendShipNotFoundException;
import ru.skillbox.socialnet.exception.PersonNotFoundException;
import ru.skillbox.socialnet.mapper.PersonMapper;
import ru.skillbox.socialnet.repository.FriendShipRepository;
import ru.skillbox.socialnet.repository.PersonRepository;
import ru.skillbox.socialnet.security.JwtTokenUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FriendShipService {
    private final PersonMapper personMapper;
    private final FriendShipRepository friendShipRepository;
    private final PersonRepository personRepository;
    private final JwtTokenUtils jwtTokenUtils;

    /**
     * @param token - токен авторизации
     * @return - функция вернет пользователя (персону) по токену авторизации
     */
    public Person getAuthorizedUser(String token) throws PersonNotFoundException {

        Long id = jwtTokenUtils.getId(token);
        Optional<Person> person = personRepository.findById(id);

        if (person.isEmpty()) {
            throw new PersonNotFoundException("Пользователь не найден");
        }

        return person.get();
    }

    /**
     * @return вспомогательная функция. Возвращает форматированный объект CommonRsComplexRs<ComplexRs>
     * пока заполняется пустыми значениями
     */
    private CommonRs<ComplexRs> generateCommonRsComplexRs() {

        CommonRs<ComplexRs> response = new CommonRs<>();
        ComplexRs complexRs = new ComplexRs();
        response.setData(complexRs);

        return response;
    }

    /**
     * @param friendShip   - объект, состояние которого будет записано в базу
     * @param sourcePerson - персона 1
     * @param sentTime     - персона 2
     * @param status       - статус дружбы между персонами
     *                     вспомогательный метод
     *                     процедура делает запись в базу (таблица friendships) по входным параметрам
     */
    private void saveFriendshipChanges(FriendShip friendShip,
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
     * @param destinationPersonId - персона, которой отправляется запрос на дружбу
     * @param authorization       - токен авторизации, по нему будет вычислен текущий пользователь
     * @return - метод сервиса вернет объект на POST запрос  "/api/v1/friends/{id}"
     * @throws PersonNotFoundException - может быть сгенерировано исключение, если персона,
     *                                 с которой хотят дружить не найдена
     */
    public CommonRs<ComplexRs> sendFriendshipRequest(Long destinationPersonId, String authorization)
            throws PersonNotFoundException {

        Person currentPerson = getAuthorizedUser(authorization);
        Person destinationPerson = personRepository.findByIdImpl(destinationPersonId);
        FriendShip friendShip = friendShipRepository.getFriendShipByIdsAndStatus(
                        currentPerson.getId(), destinationPersonId, FriendShipStatus.REQUEST.name())
                .orElse(new FriendShip());

        //текущая персона отправляет запрос на дружбу
        saveFriendshipChanges(friendShip, currentPerson, destinationPerson, LocalDateTime.now(),
                FriendShipStatus.REQUEST);
        friendShip = friendShipRepository.getFriendShipByIdsAndStatus(
                        destinationPersonId, currentPerson.getId(), FriendShipStatus.RECEIVED_REQUEST.name())
                .orElse(new FriendShip());

        //принимающая сторона принимает запрос на дружбу
        saveFriendshipChanges(friendShip, destinationPerson, currentPerson, LocalDateTime.now(),
                FriendShipStatus.RECEIVED_REQUEST);

        return generateCommonRsComplexRs();
    }

    /**
     * @param destinationPersonId - персона, которую хотят удалить из друзей
     * @param authorization       - токен авторизации, по нему будет вычислен текущий пользователь
     * @return - метод сервиса вернет объект на DELETE запрос "/api/v1/friends/{id}"
     * @throws PersonNotFoundException     - может быть сгенерировано исключение, если не найдена персона, с которой
     *                                     хотят удалить дружбу, или не найдены записи о дружбе
     * @throws FriendShipNotFoundException - может быть сгенерировано исключение, если не в таблице friendships
     *                                     не найдены записали со статусом FRIEND для обеих персон
     */
    public CommonRs<ComplexRs> deleteFriendById(Long destinationPersonId, String authorization)
            throws PersonNotFoundException, FriendShipNotFoundException {

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
     * @param destinationPersonId - персона, которую хотят добавить в друзья
     * @param authorization       - токен авторизации, по нему будет вычислен текущий пользователь
     * @return - метод сервиса вернет объект на POST запрос "/api/v1/friends/request/{id}"
     * @throws PersonNotFoundException     - может быть сгенерировано исключение, если не найдена персона, которую
     *                                     хотят добавить в друзья
     * @throws FriendShipNotFoundException - может быть сгенерировано исключение, если не найден RECEIVED_REQUEST
     *                                     у принимающей стороны или REQUEST у передающей стороны
     */
    @Transactional
    public CommonRs<ComplexRs> addFriendById(Long destinationPersonId, String authorization)
            throws PersonNotFoundException, FriendShipNotFoundException {

        Person currentPerson = getAuthorizedUser(authorization);
        Person destinationPerson = personRepository.findByIdImpl(destinationPersonId);

        FriendShip friendShip = friendShipRepository.getFriendShipByIdsAndStatusImpl(
                currentPerson.getId(),
                destinationPerson.getId(),
                FriendShipStatus.RECEIVED_REQUEST);
        saveFriendshipChanges(
                friendShip,
                currentPerson,
                destinationPerson,
                LocalDateTime.now(),
                FriendShipStatus.FRIEND);
        friendShip = friendShipRepository.getFriendShipByIdsAndStatusImpl(
                destinationPerson.getId(),
                currentPerson.getId(),
                FriendShipStatus.REQUEST);
        saveFriendshipChanges(
                friendShip,
                destinationPerson,
                currentPerson,
                LocalDateTime.now(),
                FriendShipStatus.FRIEND);

        return generateCommonRsComplexRs();
    }

    /**
     * @param destinationPersonId - персона, с которой хотят отклонить дружбу
     * @param authorization       - токен авторизации, по нему будет вычислен текущий пользователь
     * @return - метод сервиса вернет объект на DELETE запрос "/api/v1/friends/request/{id}"
     * @throws PersonNotFoundException     - может быть сгенерировано исключение, если не найдена персона, с которой
     *                                     хотят отклонить дружбу
     * @throws FriendShipNotFoundException - может быть сгенерировано исключение, если не найден RECEIVED_REQUEST
     *                                     c одной стороны или REQUEST с другой стороны
     */
    @Transactional
    public CommonRs<ComplexRs> declineFriendshipRequestById(Long destinationPersonId, String authorization)
            throws PersonNotFoundException, FriendShipNotFoundException {
        Person currentPerson = getAuthorizedUser(authorization);
        Person destinationPerson = personRepository.findByIdImpl(destinationPersonId);
        FriendShip friendShip;
        try {
            friendShip = friendShipRepository.getFriendShipByIdsAndStatusImpl(
                    currentPerson.getId(), destinationPerson.getId(), FriendShipStatus.RECEIVED_REQUEST);
        } catch (FriendShipNotFoundException e) {
            friendShip = friendShipRepository.getFriendShipByIdsAndStatusImpl(
                    currentPerson.getId(), destinationPerson.getId(), FriendShipStatus.REQUEST);
        }
        friendShipRepository.delete(friendShip);
        try {
            friendShip = friendShipRepository.getFriendShipByIdsAndStatusImpl(
                    destinationPerson.getId(), currentPerson.getId(), FriendShipStatus.REQUEST);
        } catch (FriendShipNotFoundException e) {
            friendShip = friendShipRepository.getFriendShipByIdsAndStatusImpl(
                    destinationPerson.getId(), currentPerson.getId(), FriendShipStatus.RECEIVED_REQUEST);
        }
        friendShipRepository.delete(friendShip);
        return generateCommonRsComplexRs();
    }

    /**
     * @param destinationPersonId - персона, которую хотят блокировать/разблокировать
     * @param authorization       - токен авторизации, по нему будет вычислен текущий пользователь
     * @throws PersonNotFoundException - может быть сгенерировано исключение, если не найдена персона, которую
     *                                 хотят блокировать/разблокировать
     *                                 return void - мeтод ничего не возвращает
     *                                 метод вызывается, когда на контроллер поступает POST запрос
     *                                 "/api/v1/friends/block_unblock/{id}"
     */
    public void blockOrUnblockUserByUser(Long destinationPersonId, String authorization)
            throws PersonNotFoundException {

        Person currentPerson = getAuthorizedUser(authorization);
        Person destinationPerson = personRepository.findByIdImpl(destinationPersonId);
        FriendShip friendShip = friendShipRepository.getFriendShipByIdsAndStatus(
                        currentPerson.getId(),
                        destinationPerson.getId(),
                        FriendShipStatus.BLOCKED.name())
                .orElse(null);

        //если найдена запись с заблокированной персоной, то надо эту запись удалить
        if (friendShip != null) {
            friendShipRepository.delete(friendShip);
        } else {
            //если запись не найдена, то надо персону заблокировать
            friendShip = new FriendShip();
            saveFriendshipChanges(
                    friendShip,
                    currentPerson,
                    destinationPerson,
                    LocalDateTime.now(),
                    FriendShipStatus.BLOCKED);
        }
    }

    /**
     * @param authorization - токен авторизации, по нему будет вычислен текущий пользователь
     * @param offset        - "отступ" с какой страницы показывать список
     * @param perPage       - количество записей на странице
     * @return - метод возвращает объект в ответ на приходящий на контроллер GET запрос "/api/v1/friends/"
     */
    public CommonRs<List<PersonRs>> getFriendsOfCurrentUser(String authorization, int offset, int perPage)
            throws PersonNotFoundException {

        Person currentPerson = getAuthorizedUser(authorization);
        long total = personRepository.findCountPersonsByFriendship(
                currentPerson.getId(),
                FriendShipStatus.FRIEND.name());
        Page<Person> personsPage = personRepository.findPersonsByFriendship(
                currentPerson.getId(),
                FriendShipStatus.FRIEND.name(),
                PageRequest.of(offset, perPage));
        ArrayList<PersonRs> personsData = generatePersonsData(personsPage,
                FriendShipStatus.FRIEND.name(),
                false);

        return generatePersonsList(personsData, offset, perPage, total);
    }

    /**
     * @param person - пользователь, чьих друзей мы хотим получить
     * @return - метод возвращает список всех друзей пользователя
     */
    public List<Person> getAllFriends(Person person) {
        return personRepository.findPersonsByFriendship(
                        person.getId(),
                        FriendShipStatus.FRIEND.name(),
                        Pageable.unpaged())
                .getContent();
    }

    /**
     * @param personsPage      - выборка персон из запроса (в репозитории)
     * @param friendShipStatus - статус дружбы (строка)
     * @param isBlocked        - параметр блокирована ли запись
     * @return - вспомогательный метод (возвращает список персон в новом формате)
     */
    private ArrayList<PersonRs> generatePersonsData(Page<Person> personsPage,
                                                    String friendShipStatus,
                                                    Boolean isBlocked) {

        ArrayList<PersonRs> personsData = new ArrayList<>();
        personsPage.forEach(person -> {
            PersonRs personRs = personMapper.personToPersonRs(person, friendShipStatus, isBlocked);
            personsData.add(personRs);
        });

        return personsData;
    }

    /**
     * @param authorization - токен авторизации, по нему будет вычислен текущий пользователь
     * @param offset        - "отступ" с какой страницы показывать список
     * @param perPage       - количество записей на странице
     * @return - метод возвращает объект в ответ на приходящий на контроллер GET запрос "/api/v1/friends/request"
     */
    public CommonRs<List<PersonRs>> getPotentialFriendsOfCurrentUser(String authorization, int offset, int perPage)
            throws PersonNotFoundException {

        Person currentPerson = getAuthorizedUser(authorization);
        long total = personRepository.findCountPersonsByFriendship(
                currentPerson.getId(),
                FriendShipStatus.RECEIVED_REQUEST.name());
        Page<Person> personsPage = personRepository.findPersonsByFriendship(
                currentPerson.getId(),
                FriendShipStatus.RECEIVED_REQUEST.name(),
                PageRequest.of(offset, perPage));
        ArrayList<PersonRs> personsData = generatePersonsData(personsPage,
                FriendShipStatus.RECEIVED_REQUEST.name(),
                false);

        return generatePersonsList(personsData, offset, perPage, total);
    }

    /**
     * @param persons       - список персон в формате ArrayList<Person>
     * @param currentPerson - текущая персона
     * @return - вспомогательный метод (вычисляет "статус дружбы" между персонами из списка и текущей персоной и
     * возвращает объект списка в формате ArrayList<PersonRs>
     */
    private ArrayList<PersonRs> updatePersonsData(List<Person> persons, Person currentPerson) {

        ArrayList<PersonRs> personsData = new ArrayList<>();
        HashSet<Person> personSet = new HashSet();
        for (Person person : persons) {
            if (!(personSet.contains(person)) && (personSet.size() < 10)) {
                personSet.add(person);
                Optional<FriendShipStatus> optionalStatus =
                        friendShipRepository.getFriendShipStatusBetweenPersons(currentPerson, person);
                FriendShipStatus status = optionalStatus.orElse(FriendShipStatus.UNKNOWN);
                PersonRs personRs = personMapper.personToPersonRs(
                        person,
                        status.name(),
                        status == FriendShipStatus.BLOCKED);
                personsData.add(personRs);
            }
        }

        return personsData;
    }

    /**
     * @param authorization - токен авторизации, по нему будет вычислен текущий пользователь
     * @return - метод возвращает объект в ответ на приходящий на контроллер GET запрос
     * "/api/v1/friends/recommendations"
     */
    public CommonRs<List<PersonRs>> getRecommendationFriends(String authorization)
            throws PersonNotFoundException {

        Person currentPerson = getAuthorizedUser(authorization);
        List<PersonRs> personsData;
        // Список рекомендованных друзей решил получать так:
        // Если друзей нет, то генерировать рандомный список
        // Если друзья есть, то предлагать из их друзей, и если их кол-во < 10, то дополнять рандомом до 10
        List<Person> persons = personRepository.getRecomendationsByPersonId(currentPerson.getId());
        personsData = updatePersonsData(persons, currentPerson);

        return generatePersonsList(personsData,
                0,
                personsData.size(),
                personsData.size());
    }

    /**
     * @param personsData - объект (список) в формате ArrayList<PersonRs> (список персон)
     * @param offSet      - "отступ"
     * @param perPage     - "на странице"
     * @param total       - "всего"
     * @return - вспомогательный метод. Вернёт отформатированный список персон
     */
    private CommonRs<List<PersonRs>> generatePersonsList(List<PersonRs> personsData,
                                                         int offSet,
                                                         int perPage,
                                                         long total) {

        CommonRs<List<PersonRs>> personsList = new CommonRs<>();
        personsList.setData(personsData);
        personsList.setItemPerPage(perPage);
        personsList.setOffset(offSet);
        personsList.setPerPage(perPage);
        personsList.setTotal(total);

        return personsList;
    }

    /**
     * @param authorization - токен авторизации, по нему будет вычислен текущий пользователь
     * @param offset        - "отступ" с какой страницы показывать список
     * @param perPage       - количество записей на странице
     * @return - метод возвращает объект в ответ на приходящий на контроллер GET запрос
     * "/api/v1/friends/outgoing_requests"
     */
    public CommonRs<List<PersonRs>> getOutgoingRequestsByUser(String authorization, int offset, int perPage)
            throws PersonNotFoundException {

        Person currentPerson = getAuthorizedUser(authorization);
        long total =
                personRepository.findCountPersonsByFriendship(currentPerson.getId(), FriendShipStatus.REQUEST.name());
        Page<Person> personsPage = personRepository.findPersonsByFriendship(
                currentPerson.getId(),
                FriendShipStatus.REQUEST.name(),
                PageRequest.of(offset, perPage));
        ArrayList<PersonRs> personsData = generatePersonsData(
                personsPage,
                FriendShipStatus.REQUEST.name(),
                false);

        return generatePersonsList(personsData, offset, perPage, total);
    }
}
