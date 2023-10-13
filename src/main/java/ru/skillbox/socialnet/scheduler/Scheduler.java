package ru.skillbox.socialnet.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import ru.skillbox.socialnet.dto.LogUploader;
import ru.skillbox.socialnet.entity.other.Captcha;
import ru.skillbox.socialnet.repository.CaptchaRepository;
import ru.skillbox.socialnet.service.NotificationService;

import java.time.Duration;
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
    private final NotificationService notificationService;
    private final CaptchaRepository captchaRepository;
    private final LogUploader logUploader;

    @Value("${logger.path}")
    private String logPath;

    @Value("${logger.expired}")
    private Duration expired;

    @Scheduled(fixedDelayString = "PT02H")
    private void deleteCaptcha() {
        LocalDateTime date = LocalDateTime.now();
        Optional<List<Captcha>> captchas = captchaRepository.findByTime(date.minusHours(2));
        captchas.ifPresent(captchaRepository::deleteAll);
    }

    @Scheduled(fixedDelayString = "PT24H")
    private void uploadLog() {
        logUploader.uploadLog(logPath);
    }

    @Scheduled(fixedDelayString = "PT24H")
    private void deleteOldLogs() {
        logUploader.deleteExpiredLogs(expired);
    }

    @Scheduled(cron = "0 0 0 * * *")
    private void sendBirthdayNotification() {
        notificationService.sendBirthdayNotification();
    }
}
