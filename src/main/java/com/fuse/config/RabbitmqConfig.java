package com.fuse.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Cra2iTeT
 * @since 2023/5/1 10:00
 */
@Configuration
public class RabbitmqConfig {

    public static final String EXCHANGE_DIRECT_EXCEPTION_LISTENER = "exchange_direct_exception_listener";
    public static final String QUEUE_EXCEPTION_LISTENER_PYTHON_SCRIPT_EXCEPTION = "exception_listener-python_script";
    public static final String ROUTINGKEY_PYTHON_SCRIPT_EXCEPTION = "exception.listener.python_script";
    public static final String QUEUE_EXCEPTION_LISTENER_WEATHER_FETCH_EXCEPTION = "exception_listener-weather_fetch";
    public static final String ROUTINGKEY_WEATHER_FETCH_EXCEPTION = "exception.listener.weather_fetch";

    public static final String QUEUE_EXCEPTION_LISTENER_PREDICT_EXCEPTION = "exception_listener-predict";
    public static final String ROUTINGKEY_PREDICT_EXCEPTION = "exception.listener.predict";

    public static final String QUEUE_EXCEPTION_LISTENER_OBJECT_EXCEPTION = "exception_listener-object";
    public static final String ROUTINGKEY_OBJECT_EXCEPTION = "exception.listener.object";

    @Bean(EXCHANGE_DIRECT_EXCEPTION_LISTENER)
    public Exchange EXCHANGE_FANOUT_EXCEPTION_LISTENER() {
        return ExchangeBuilder.directExchange(EXCHANGE_DIRECT_EXCEPTION_LISTENER).durable(true).build();
    }

    @Bean(QUEUE_EXCEPTION_LISTENER_PYTHON_SCRIPT_EXCEPTION)
    public Queue QUEUE_EXCEPTION_LISTENER_PYTHON_SCRIPT_EXCEPTION() {
            return new Queue(QUEUE_EXCEPTION_LISTENER_PYTHON_SCRIPT_EXCEPTION);
    }

    @Bean(QUEUE_EXCEPTION_LISTENER_WEATHER_FETCH_EXCEPTION)
    public Queue QUEUE_EXCEPTION_LISTENER_WEATHER_FETCH_EXCEPTION() {
        return new Queue(QUEUE_EXCEPTION_LISTENER_WEATHER_FETCH_EXCEPTION);
    }

    @Bean(QUEUE_EXCEPTION_LISTENER_PREDICT_EXCEPTION)
    public Queue QUEUE_EXCEPTION_LISTENER_PREDICT_EXCEPTION() {
        return new Queue(QUEUE_EXCEPTION_LISTENER_PREDICT_EXCEPTION);
    }

    @Bean(QUEUE_EXCEPTION_LISTENER_OBJECT_EXCEPTION)
    public Queue QUEUE_EXCEPTION_LISTENER_OBJECT_EXCEPTION() {
        return new Queue(QUEUE_EXCEPTION_LISTENER_OBJECT_EXCEPTION);
    }

    @Bean
    public Binding BINDING_QUEUE_EXCEPTION_LISTENER_PYTHON_SCRIPT_EXCEPTION(
            @Qualifier(QUEUE_EXCEPTION_LISTENER_PYTHON_SCRIPT_EXCEPTION) Queue queue,
            @Qualifier(EXCHANGE_DIRECT_EXCEPTION_LISTENER) Exchange exchange) {
        return BindingBuilder.bind(queue).to(exchange)
                .with(ROUTINGKEY_PYTHON_SCRIPT_EXCEPTION).noargs();
    }

    @Bean
    public Binding BINDING_QUEUE_EXCEPTION_LISTENER_PREDICT_EXCEPTION(
            @Qualifier(QUEUE_EXCEPTION_LISTENER_PREDICT_EXCEPTION) Queue queue,
            @Qualifier(EXCHANGE_DIRECT_EXCEPTION_LISTENER) Exchange exchange) {
        return BindingBuilder.bind(queue).to(exchange)
                .with(ROUTINGKEY_PREDICT_EXCEPTION).noargs();
    }

    @Bean
    public Binding BINDING_QUEUE_EXCEPTION_LISTENER_WEATHER_FETCH_EXCEPTION(
            @Qualifier(QUEUE_EXCEPTION_LISTENER_WEATHER_FETCH_EXCEPTION) Queue queue,
            @Qualifier(EXCHANGE_DIRECT_EXCEPTION_LISTENER) Exchange exchange) {
        return BindingBuilder.bind(queue).to(exchange)
                .with(ROUTINGKEY_WEATHER_FETCH_EXCEPTION).noargs();
    }

    @Bean
    public Binding BINDING_QUEUE_EXCEPTION_LISTENER_OBJECT_EXCEPTION(
            @Qualifier(QUEUE_EXCEPTION_LISTENER_OBJECT_EXCEPTION) Queue queue,
            @Qualifier(EXCHANGE_DIRECT_EXCEPTION_LISTENER) Exchange exchange) {
        return BindingBuilder.bind(queue).to(exchange)
                .with(ROUTINGKEY_OBJECT_EXCEPTION).noargs();
    }
}
