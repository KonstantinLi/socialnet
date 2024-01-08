package com.socialnet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.socialnet.entity.personrelated.PersonSettings;

@Repository
public interface PersonSettingsRepository extends JpaRepository<PersonSettings, Long> {
}
