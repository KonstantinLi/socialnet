package ru.skillbox.socialnet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.skillbox.socialnet.entity.FriendShip;
import ru.skillbox.socialnet.entity.Person;
import ru.skillbox.socialnet.entity.enums.FriendShipStatus;

import java.util.Optional;

@Repository
public interface FriendShipRepository extends JpaRepository<FriendShip, Long> {

    /**
     * @param src_person_id  - текущая персона (от имени которого запрашиваются данные)
     * @param dst_person_id  - искомая персона (его связи с текущей персоной)
     * @param shipStatus     - тип связи между персонами (если передать NULL, то запрос вернет все связи)
     * @return               - запрос вернет записи в таблице friendships между двумя персонами
     */

    @Query(value = "select * from friendships f where " +
                   "f.src_person_id = :src_person_id " +
                   "and f.dst_person_id = :dst_person_id " +
                   "and (f.status_name = :shipStatus or :shipStatus = '')", nativeQuery = true)
    Iterable<FriendShip> getFriendShipByIdsAndStatus(@Param("src_person_id") long src_person_id,
                                                 @Param("dst_person_id") long dst_person_id,
                                                 @Param("shipStatus") String shipStatus);

    /**
     *  удаляем все связи в таблице friendships между персонами, переданными в параметрах
     * @param src_person_id
     * @param dst_person_id
     */
    @Transactional
    @Modifying
    @Query(value =  "delete from friendships f " +
            " where (f.src_person_id = :src_person_id and f.dst_person_id = :dst_person_id) " +
            "   or  (f.src_person_id = :dst_person_id and f.dst_person_id = :src_person_id)", nativeQuery = true)
    void delRelationsFromPersons(@Param("src_person_id") long src_person_id,
                                 @Param("dst_person_id") long dst_person_id);

    /**
     *
     * @param sourcePerson       - персона src
     * @param destinationPerson  - персона dst
     * @return  - запрос вернет значение статуса в таблице friendships между персонами, переданными в параметрах
     */
    @Query(value = "select f.status from FriendShip f " +
            " where f.sourcePerson = :sourcePerson and f.destinationPerson = :destinationPerson ")
    Optional<FriendShipStatus> getFriendhipStatusBetweenPersons(@Param("sourcePerson") Person sourcePerson,
                                                                @Param("destinationPerson") Person destinationPerson);
}
