package ru.skillbox.socialnet.kafka.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.skillbox.socialnet.dto.request.PasswordRecoveryRq;
import ru.skillbox.socialnet.kafka.KafkaService;
import ru.skillbox.socialnet.kafka.dto.KafkaMessage;
import ru.skillbox.socialnet.service.AccountService;

import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaMessageListener {


    private final AccountService accountService;
    private final KafkaService kafkaService;

    @KafkaListener(
            topics = "${app.kafka.kafkaMessageTopic}",
            groupId = "${app.kafka.kafkaMessageGroupId}",
            containerFactory = "kafkaMessageConcurrentKafkaListenerContainerFactory"
    )
    public void listen(@Payload KafkaMessage message,
                       @Header(value = KafkaHeaders.RECEIVED_KEY, required = false) UUID key,
                       @Header(value = KafkaHeaders.RECEIVED_TOPIC) String topic,
                       @Header(value = KafkaHeaders.RECEIVED_PARTITION) Integer partition,
                       @Header(value = KafkaHeaders.RECEIVED_TIMESTAMP) Long timestamp) throws JsonProcessingException {
        log.info("Received message: {}", message);
        log.info("Key: {}, Partition: {}, Topic: {}, Timestamp: {}", key, partition, topic, timestamp);

        var passwordRecoveryRq = kafkaService.readMessage(message, PasswordRecoveryRq.class);
        accountService.passwordRecovery(passwordRecoveryRq);
    }

}
