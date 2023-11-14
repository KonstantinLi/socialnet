package ru.skillbox.socialnet.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.skillbox.socialnet.kafka.dto.KafkaMessage;

@Service
@RequiredArgsConstructor
public class KafkaService {

    @Value("${app.kafka.kafkaMessageTopic}")
    private String topicName;

    private final KafkaTemplate<String, KafkaMessage> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void sendMessage(Object data) throws JsonProcessingException {
        var message = KafkaMessage.builder().message(objectMapper.writeValueAsString(data)).build();
        kafkaTemplate.send(topicName, message);
    }

    public <T> T readMessage(KafkaMessage message, Class<T> valueType) throws JsonProcessingException {
        return objectMapper.readValue(message.getMessage(), valueType);
    }

}
