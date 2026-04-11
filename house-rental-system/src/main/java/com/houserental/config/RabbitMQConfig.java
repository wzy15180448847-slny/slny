package com.houserental.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ消息队列配置
 */
@Configuration
public class RabbitMQConfig {

    // 交换机名称
    public static final String NOTIFICATION_EXCHANGE = "notification.exchange";

    // 队列名称
    public static final String NOTIFICATION_QUEUE = "notification.queue";
    public static final String SMS_QUEUE = "sms.queue";
    public static final String EMAIL_QUEUE = "email.queue";

    // 路由键
    public static final String NOTIFICATION_ROUTING_KEY = "notification.#";
    public static final String SMS_ROUTING_KEY = "sms.#";
    public static final String EMAIL_ROUTING_KEY = "email.#";

    /**
     * 创建交换机
     */
    @Bean
    public Exchange notificationExchange() {
        return ExchangeBuilder.topicExchange(NOTIFICATION_EXCHANGE)
                .durable(true)
                .build();
    }

    /**
     * 创建站内消息队列
     */
    @Bean
    public Queue notificationQueue() {
        return QueueBuilder.durable(NOTIFICATION_QUEUE)
                .build();
    }

    /**
     * 创建短信队列
     */
    @Bean
    public Queue smsQueue() {
        return QueueBuilder.durable(SMS_QUEUE)
                .build();
    }

    /**
     * 创建邮件队列
     */
    @Bean
    public Queue emailQueue() {
        return QueueBuilder.durable(EMAIL_QUEUE)
                .build();
    }

    /**
     * 绑定站内消息队列
     */
    @Bean
    public Binding notificationBinding(Queue notificationQueue, Exchange notificationExchange) {
        return BindingBuilder.bind(notificationQueue)
                .to(notificationExchange)
                .with(NOTIFICATION_ROUTING_KEY)
                .noargs();
    }

    /**
     * 绑定短信队列
     */
    @Bean
    public Binding smsBinding(Queue smsQueue, Exchange notificationExchange) {
        return BindingBuilder.bind(smsQueue)
                .to(notificationExchange)
                .with(SMS_ROUTING_KEY)
                .noargs();
    }

    /**
     * 绑定邮件队列
     */
    @Bean
    public Binding emailBinding(Queue emailQueue, Exchange notificationExchange) {
        return BindingBuilder.bind(emailQueue)
                .to(notificationExchange)
                .with(EMAIL_ROUTING_KEY)
                .noargs();
    }
}
