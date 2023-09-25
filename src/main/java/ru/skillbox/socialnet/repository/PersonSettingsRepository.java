package ru.skillbox.socialnet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.skillbox.socialnet.entity.PersonSettings;

@Repository
public interface PersonSettingsRepository extends JpaRepository<PersonSettings, Long> {
}
