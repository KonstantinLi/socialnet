package ru.skillbox.socialnet.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import ru.skillbox.socialnet.entity.other.Captcha;
import ru.skillbox.socialnet.repository.CaptchaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

//TODO Две закомментированные аннотации?
@Configuration
//@EnableScheduling
//@ConditionalOnProperty(name = "scheduler.enabled", matchIfMissing = true)
@Async
@RequiredArgsConstructor
public class Scheduler {
    private final CaptchaRepository captchaRepository;

    @Scheduled(fixedDelayString = "PT02H")
    private void deleteCaptcha() {
        LocalDateTime date = LocalDateTime.now();
        Optional<List<Captcha>> captchas = captchaRepository.findByTime(date.minusHours(2));
        captchas.ifPresent(captchaRepository::deleteAll);
    }
}