package ru.skillbox.socialnet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.skillbox.socialnet.entity.enums.FriendShipStatus;
import ru.skillbox.socialnet.entity.personrelated.FriendShip;
import ru.skillbox.socialnet.entity.personrelated.Person;
import ru.skillbox.socialnet.exception.FriendShipNotFoundException;

import java.util.Optional;

@Repository
public interface FriendShipRepository extends JpaRepository<FriendShip, Long> {

    /**
     * @param srcPersonId - текущая персона (от имени которого запрашиваются данные)
     * @param dstPersonId - искомая персона (его связи с текущей персоной)
     * @param shipStatus    - тип связи между персонами (если передать NULL, то запрос вернет все связи)
     * @return - запрос вернет записи в таблице friendships между двумя персонами
     */
    @Query(value = "select * from friendships f where " +
            "f.src_person_id = :src_person_id " +
            "and f.dst_person_id = :dst_person_id " +
            "and (f.status_name = :shipStatus or :shipStatus = '')", nativeQuery = true)
    Optional<FriendShip> getFriendShipByIdsAndStatus(@Param("src_person_id") long srcPersonId,
                                                     @Param("dst_person_id") long dstPersonId,
                                                     @Param("shipStatus") String shipStatus);

    /**
     * @param srcPersonId - ID 1-й персоны
     * @param dstPersonId - ID 2-й персоны
     * @param status      - статус дружбы
     * @return - дефолтный метод-обертка. Вернет объект класса FriendShip или сгенерирует исключение
     * @throws FriendShipNotFoundException - может быть сгенерировано исключение, если запись в таблице friendships
     *                                     по входным параметрам не найдена
     */
    default FriendShip getFriendShipByIdsAndStatusImpl(long srcPersonId, long dstPersonId, FriendShipStatus status)
            throws FriendShipNotFoundException {
        return getFriendShipByIdsAndStatus(srcPersonId, dstPersonId, status.name())
                .orElseThrow(() -> new FriendShipNotFoundException(status));
    }

    /**
     * @param sourcePerson      - персона src
     * @param destinationPerson - персона dst
     * @return - запрос вернет значение статуса в таблице friendships между персонами, переданными в параметрах
     */
    @Query(value = "select f.status from FriendShip f " +
            " where f.sourcePerson = :sourcePerson and f.destinationPerson = :destinationPerson ")
    Optional<FriendShipStatus> getFriendShipStatusBetweenPersons(@Param("sourcePerson") Person sourcePerson,
                                                                 @Param("destinationPerson") Person destinationPerson);

    @Query(value = "select f.status from FriendShip f " +
            " where f.sourcePerson.id = :sourcePersonId and f.destinationPerson.id = :destinationPersonId ")
    Optional<FriendShipStatus> getFriendShipStatusBetweenTwoPersons(
            @Param("sourcePersonId") Long sourcePersonI,
            @Param("destinationPersonId") Long destinationPersonI);

    Optional<FriendShip> findBySrcPersonIdAndDstPersonId(Long srcPersonId, Long dstPersonId);
}
