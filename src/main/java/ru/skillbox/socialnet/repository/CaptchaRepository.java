package ru.skillbox.socialnet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.skillbox.socialnet.entity.other.Captcha;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CaptchaRepository extends JpaRepository<Captcha, Long> {
    Optional<Captcha> findBySecretCode(String codeSecret);

    @Query(value = "select * from Captcha c where c.time > :timeParam", nativeQuery = true)
    Optional<List<Captcha>> findByTime(@Param("timeParam") LocalDateTime timeParam);
}
