package com.example.consumidor;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class Consumer {
    //yrdyhr

    @KafkaListener(topics = "db-logs-2.DCN_USER.TESTE", groupId = "debezium-connect-group-00")
    public void listen(String message) {

        System.out.println("Mensagem recebida do Kafka: " + PayloadNormalizer.normalizePayload(message));
    }
}
