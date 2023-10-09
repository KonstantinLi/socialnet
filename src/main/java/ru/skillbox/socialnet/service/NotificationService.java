package ru.skillbox.socialnet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import ru.skillbox.socialnet.dto.response.CommonRs;
import ru.skillbox.socialnet.dto.response.NotificationRs;
import ru.skillbox.socialnet.entity.enums.NotificationType;
import ru.skillbox.socialnet.entity.other.Notification;
import ru.skillbox.socialnet.entity.personrelated.Person;
import ru.skillbox.socialnet.mapper.NotificationListMapper;
import ru.skillbox.socialnet.mapper.NotificationMapper;
import ru.skillbox.socialnet.repository.NotificationRepository;
import ru.skillbox.socialnet.repository.PersonRepository;
import ru.skillbox.socialnet.security.JwtTokenUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final FriendShipService friendShipService;

    private final NotificationRepository notificationRepository;
    private final PersonRepository personRepository;

    private final NotificationListMapper notificationListMapper;
    private final NotificationMapper notificationMapper;

    private final SimpMessagingTemplate messagingTemplate;
    private final JwtTokenUtils jwtTokenUtils;

    public void sendBirthdayNotification() {
        List<Person> birthdayPersons = personRepository.findAllByBirthDate(LocalDateTime.now());

        for (Person person : birthdayPersons) {
            String contact = String.format(
                    "%s %s отмечает сегодня День рождения",
                    person.getFirstName(),
                    person.getLastName());

            List<Person> friends = friendShipService.getAllFriends(person);

            friends.forEach(friend -> {
                Notification notification = save(contact, NotificationType.FRIEND_BIRTHDAY, friend);
                sendNotification(notification);
            });
        }
    }

    public void sendPostNotification(Person currentPerson) {
        String contact = String.format(
                "%s %s опубликовал новый пост",
                currentPerson.getFirstName(),
                currentPerson.getLastName());

        List<Person> friends = friendShipService.getAllFriends(currentPerson);

        friends.forEach(friend -> {
            Notification notification = save(contact, NotificationType.POST, friend);
            sendNotification(notification);
        });
    }

    public void sendFriendRequestNotification(Person currentPerson, Person destinationPerson) {
        String contact = String.format(
                "%s %s отправил вам заявку в друзья",
                currentPerson.getFirstName(),
                currentPerson.getLastName());

        Notification notification = save(
                contact,
                NotificationType.FRIEND_REQUEST,
                destinationPerson);

        sendNotification(notification);
    }

    public void sendPostCommentNotification(Person currentPerson, Person destinationPerson) {
        String contact = String.format(
                "%s %s прокомментировал ваш пост",
                currentPerson.getFirstName(),
                currentPerson.getLastName());

        Notification notification = save(
                contact,
                NotificationType.POST_COMMENT,
                destinationPerson);

        sendNotification(notification);
    }

    public void sendCommentCommentNotification(Person currentPerson, Person destinationPerson) {
        String contact = String.format(
                "%s %s ответил на ваш комментарий",
                currentPerson.getFirstName(),
                currentPerson.getLastName());

        Notification notification = save(
                contact,
                NotificationType.COMMENT_COMMENT,
                destinationPerson);

        sendNotification(notification);
    }

    public void sendPostLikeNotification(Person currentPerson, Person destinationPerson) {
        String contact = String.format(
                "%s %s поставил лайк вашему посту",
                currentPerson.getFirstName(),
                currentPerson.getLastName());

        Notification notification = save(
                contact,
                NotificationType.POST_LIKE,
                destinationPerson);

        sendNotification(notification);
    }

    public void sendCommentLikeNotification(Person currentPerson, Person destinationPerson) {
        String contact = String.format(
                "%s %s поставил лайк вашему комментарию",
                currentPerson.getFirstName(),
                currentPerson.getLastName());

        Notification notification = save(
                contact,
                NotificationType.COMMENT_LIKE,
                destinationPerson);

        sendNotification(notification);
    }

    private void sendNotification(Notification notification) {
        Long personId = notification.getPerson().getId();
        NotificationRs notificationRs = notificationMapper.notificationToNotificationRs(notification);

        messagingTemplate.convertAndSendToUser(
                personId.toString(),
                "/queue/notifications",
                notificationRs);
    }

    public CommonRs<List<NotificationRs>> getAllNotifications(
            String token,
            Integer itemPerPage,
            Integer offset) {

        Long personId = jwtTokenUtils.getId(token);
        List<Notification> notifications = notificationRepository.findAllByPersonId(personId);

        return getListNotificationResponse(notifications, itemPerPage, offset);
    }

    public CommonRs<List<NotificationRs>> readNotifications(String token, Long id, Boolean all) {
        List<Notification> notifications = new ArrayList<>();
        if (all) {
            Long personId = jwtTokenUtils.getId(token);
            notifications.addAll(notificationRepository.findAllByPersonId(personId));
        } else {
            notificationRepository.findById(id).ifPresent(notifications::add);
        }

        notifications.forEach(notification -> {
            notification.setRead(true);
            notificationRepository.save(notification);
        });

        return getListNotificationResponse(notifications);
    }

    private CommonRs<List<NotificationRs>> getListNotificationResponse(List<Notification> notifications) {
        return getListNotificationResponse(notifications, null, null);
    }

    private CommonRs<List<NotificationRs>> getListNotificationResponse(
            List<Notification> notifications,
            Integer itemPerPage,
            Integer offset) {

        List<NotificationRs> notificationRsList = notificationListMapper.toNotificationRsList(notifications);
        CommonRs<List<NotificationRs>> commonRs = new CommonRs<>();

        commonRs.setTotal((long) notificationRsList.size());
        commonRs.setTimestamp(System.currentTimeMillis());
        commonRs.setData(notificationRsList);
        commonRs.setItemPerPage(itemPerPage);
        commonRs.setPerPage(itemPerPage);
        commonRs.setOffset(offset);

        return commonRs;
    }

    private Notification save(String contact, NotificationType type, Person person) {
        Notification notification = new Notification();

        notification.setSentTime(LocalDateTime.now());
        notification.setEntityId(getEntityId(type));
        notification.setNotificationType(type);
        notification.setContact(contact);
        notification.setPerson(person);

        return notificationRepository.save(notification);
    }

    private long getEntityId(NotificationType type) {
        return type.ordinal();
    }
}
