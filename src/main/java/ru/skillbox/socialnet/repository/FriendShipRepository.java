package ru.skillbox.socialnet.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.skillbox.socialnet.data.entity.FriendShip;
import ru.skillbox.socialnet.data.enums.FriendShipStatus;

@Repository
public interface FriendShipRepository extends CrudRepository<FriendShip, Long> {

    /**
     * @param src_person_id  - текущая персона (от имени которого запрашиваются данные)
     * @param dst_person_id  - искомая персона (его связи с текущей персоной)
     * @param shipStatus     - тип связи между персонами (если передать NULL, то запрос вернет все связи)
     * @return               - запрос вернет записи в таблице friendships между двумя персонами
     */

    @Query(value = "select * from friendships f where " +
                   "f.src_person_id = :src_person_id " +
                   "and f.dst_person_id = :dst_person_id " +
                   "and (f.status_name = :shipStatus or :shipStatus = \"\"", nativeQuery = true)
    Iterable<FriendShip> getFriendShipByIdsAndStatus(@Param("src_person_id") long src_person_id,
                                                 @Param("dst_person_id") long dst_person_id,
                                                 @Param("shipStatus") FriendShipStatus shipStatus);

}
