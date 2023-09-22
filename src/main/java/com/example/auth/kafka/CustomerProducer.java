package com.example.auth.kafka;

import com.example.auth.client.request.OwnerRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerProducer {
    private final KafkaTemplate<String, OwnerRequest> template;

    public void send(OwnerRequest request){
        template.send(TopicConfig.customerTopic, request);
    }
}
