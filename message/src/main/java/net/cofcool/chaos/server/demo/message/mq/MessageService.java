package net.cofcool.chaos.server.demo.message.mq;

import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service(value = "messageService")
@Slf4j
public class MessageService {

    @Resource
    private AmqpTemplate amqpTemplate;

    @Resource
    private AmqpAdmin amqpAdmin;

    public static final String EXCHANGE_KEY = "demo.topic";
    public static final String ROUTING_KEY = "demo.msg";
    public static final String QUEUE_KEY = "demo.queue";

    public void initMq() {
        Exchange exchange = ExchangeBuilder
            .directExchange(EXCHANGE_KEY)
            .build();
        amqpAdmin.declareExchange(exchange);

        Queue queue = QueueBuilder
            .durable(QUEUE_KEY)
            .build();
        amqpAdmin.declareQueue(queue);

        amqpAdmin.declareBinding(
            BindingBuilder
                .bind(queue)
                .to(exchange)
                .with(ROUTING_KEY)
                .noargs()
        );

    }


    public void sendMessage() {
        Message message = amqpTemplate.sendAndReceive(EXCHANGE_KEY, ROUTING_KEY, MessageBuilder.withBody("hello".getBytes()).build());
        log.info("received result: {}", message);
    }

    @RabbitListener(queues = QUEUE_KEY)
    public void listen(String in) {
        log.info("received: {}", in);
    }


    public void receiveMessage() {
        while (true) {
            Message message = amqpTemplate.receive(QUEUE_KEY);
            if (message == null) {
                break;
            }

            log.info("received: {}", message);
        }

        log.info("receive end");
    }
}
