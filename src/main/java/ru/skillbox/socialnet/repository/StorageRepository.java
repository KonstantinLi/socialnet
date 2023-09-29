package ru.skillbox.socialnet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.skillbox.socialnet.entity.other.Storage;

@Repository
public interface StorageRepository extends JpaRepository<Storage, Long> {
}
