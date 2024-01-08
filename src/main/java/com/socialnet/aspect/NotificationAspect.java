package com.socialnet.aspect;

import com.socialnet.dto.request.CommentRq;
import com.socialnet.dto.request.LikeRq;
import com.socialnet.entity.enums.LikeType;
import com.socialnet.repository.PersonRepository;
import com.socialnet.repository.PostCommentsRepository;
import com.socialnet.repository.PostsRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import com.socialnet.entity.personrelated.Person;
import com.socialnet.security.JwtTokenUtils;
import com.socialnet.service.NotificationService;

@Component
@Aspect
@RequiredArgsConstructor
public class NotificationAspect {

    private final NotificationService notificationService;

    private final PostCommentsRepository commentsRepository;
    private final PersonRepository personRepository;
    private final PostsRepository postsRepository;

    private final JwtTokenUtils jwtTokenUtils;

    @Around("execution(* com.socialnet.controller.FriendsController.sendFriendshipRequest(String, Long))")
    public Object sendFriendshipRequestAroundAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();
        Object[] args = joinPoint.getArgs();

        Long sourceId = jwtTokenUtils.getId((String) args[0]);
        Long targetId = (Long) args[1];

        Person currentPerson = personRepository.findByIdImpl(sourceId);
        Person destinationPerson = personRepository.findByIdImpl(targetId);

        notificationService.sendFriendRequestNotification(currentPerson, destinationPerson);

        return result;
    }

    @Around("execution(* com.socialnet.controller.PostsController.createPost(String, ..))")
    public Object postAroundAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();

        Long personId = jwtTokenUtils.getId((String) joinPoint.getArgs()[0]);
        Person person = personRepository.findByIdImpl(personId);

        notificationService.sendPostNotification(person);

        return result;
    }

    @Around("execution(* com.socialnet.controller.PostCommentsController.createComment(" +
            "String, Long, com.socialnet.dto.request.CommentRq))")
    public Object commentAroundAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();
        Object[] args = joinPoint.getArgs();

        Long personId = jwtTokenUtils.getId((String) args[0]);
        Person currentPerson = personRepository.findByIdImpl(personId);
        Person destinationPerson;

        CommentRq commentRq = (CommentRq) args[2];
        if (commentRq.getParentId() == null) {
            Long postId = (Long) args[1];
            destinationPerson = postsRepository.findById(postId).orElseThrow().getAuthor();
            notificationService.sendPostCommentNotification(currentPerson, destinationPerson);
        } else {
            Long parentCommentId = commentRq.getParentId();
            destinationPerson = commentsRepository.findById(parentCommentId).orElseThrow().getAuthor();
            notificationService.sendCommentCommentNotification(currentPerson, destinationPerson);
        }

        return result;
    }

    @Around("execution(* com.socialnet.controller.LikesController.putLike(" +
            "String, com.socialnet.dto.request.LikeRq))")
    public Object likeAroundAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();
        Object[] args = joinPoint.getArgs();

        Long personId = jwtTokenUtils.getId((String) args[0]);
        Person currentPerson = personRepository.findByIdImpl(personId);
        Person destinationPerson;

        LikeRq likeRq = (LikeRq) args[1];
        if (likeRq.getType() == LikeType.Post) {
            destinationPerson = postsRepository.findById(likeRq.getItemId())
                    .orElseThrow()
                    .getAuthor();
            notificationService.sendPostLikeNotification(currentPerson, destinationPerson);
        } else {
            destinationPerson = commentsRepository.findById(likeRq.getItemId())
                    .orElseThrow()
                    .getAuthor();
            notificationService.sendCommentLikeNotification(currentPerson, destinationPerson);
        }

        return result;
    }
}
