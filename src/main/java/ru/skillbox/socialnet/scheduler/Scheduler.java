package ru.skillbox.socialnet.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import org.xml.sax.SAXException;
import ru.skillbox.socialnet.annotation.Debug;
import ru.skillbox.socialnet.annotation.Info;
import ru.skillbox.socialnet.dto.LogUploader;
import ru.skillbox.socialnet.entity.enums.LikeType;
import ru.skillbox.socialnet.entity.other.Captcha;
import ru.skillbox.socialnet.entity.postrelated.Post;
import ru.skillbox.socialnet.repository.CaptchaRepository;
import ru.skillbox.socialnet.repository.LikesRepository;
import ru.skillbox.socialnet.repository.PostsRepository;
import ru.skillbox.socialnet.service.CoursesService;
import ru.skillbox.socialnet.service.NotificationService;
import ru.skillbox.socialnet.service.PersonService;
import ru.skillbox.socialnet.service.WeatherService;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Configuration
@Async
@RequiredArgsConstructor
public class Scheduler {
    private final NotificationService notificationService;
    private final CaptchaRepository captchaRepository;
    private final LogUploader logUploader;
    private final PersonService personService;
    private final CoursesService coursesService;
    private final WeatherService weatherService;
    private final PostsRepository postsRepository;
    private final LikesRepository likesRepository;

    @Value("${logger.path}")
    private String logPath;

    @Value("${logger.expired}")
    private Duration expired;

    @Value("${schedule.remove-deleted-posts-in:P1D}")
    private Duration removeDeletedPosts;

    @Scheduled(fixedDelayString = "PT01H")
    @Transactional
    public void removeDeletedPosts() {
        List<Post> posts = postsRepository.findAllByIsDeletedAndTimeDeleteLessThan(
                true,
                LocalDateTime.now().minus(removeDeletedPosts)
        );

        posts.addAll(postsRepository.findAllByIsDeletedAndTimeDelete(
                        true,
                        null
                )
        );

        posts.stream()
                .peek(post -> post.getComments().forEach(
                        postComment -> likesRepository.findAllByTypeAndEntityId(LikeType.Comment, postComment.getId())
                                .forEach(likesRepository::delete)
                ))
                .peek(post -> likesRepository.findAllByTypeAndEntityId(LikeType.Post, post.getId())
                        .forEach(likesRepository::delete)
                )
                .forEach(postsRepository::delete);
    }

    @Scheduled(fixedDelayString = "PT02H")
    protected void deleteCaptcha() {
        LocalDateTime date = LocalDateTime.now();
        Optional<List<Captcha>> captchas = captchaRepository.findByTime(date.minusHours(2));
        captchas.ifPresent(captchaRepository::deleteAll);
    }

    @Scheduled(fixedDelayString = "PT24H")
    protected void uploadLog() {
        logUploader.uploadLog(logPath);
    }

    @Scheduled(fixedDelayString = "PT24H")
    protected void deleteOldLogs() {
        logUploader.deleteExpiredLogs(expired);
    }

    @Scheduled(cron = "0 0 0 * * *")
    protected void sendBirthdayNotification() {
        notificationService.sendBirthdayNotification();
    }

    @Scheduled(fixedDelayString = "PT24H")
    protected void deleteInactiveUsers() {
        personService.deleteInactiveUsers();
    }

    @Scheduled(cron = "${schedule.currency-download}")
    protected void downloadCourses() throws ParserConfigurationException, IOException, SAXException {
        coursesService.downloadCourses();
    }

    @Scheduled(cron = "${schedule.weather-update}")
    protected void updateWeather() {
        weatherService.updateAllCities();
    }
}
