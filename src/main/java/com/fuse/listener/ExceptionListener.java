package com.fuse.listener;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.fuse.config.RabbitmqConfig;
import com.fuse.domain.pojo.ErrorLog;
import com.fuse.exception.ObjectException;
import com.fuse.exception.PythonScriptRunException;
import com.fuse.exception.WeatherFetchException;
import com.fuse.mapper.ErrorLogMapper;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author Cra2iTeT
 * @since 2023/5/1 12:03
 */
@Component
public class ExceptionListener {

    private final ErrorLogMapper errorLogMapper;

    private final RabbitTemplate rabbitTemplate;

    public ExceptionListener(ErrorLogMapper errorLogMapper, RabbitTemplate rabbitTemplate) {
        this.errorLogMapper = errorLogMapper;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = {RabbitmqConfig.QUEUE_EXCEPTION_LISTENER_PYTHON_SCRIPT_EXCEPTION})
    public void pythonScriptProcess(String msg, Channel channel, Message message) {
        PythonScriptRunException pythonScriptRunException = JSONUtil.toBean(msg, PythonScriptRunException.class);
        try {
            resolveAndSaveToDB(channel, message, pythonScriptRunException);
        } catch (IOException e) {
            ObjectException exception = new ObjectException(RabbitmqConfig.QUEUE_EXCEPTION_LISTENER_PYTHON_SCRIPT_EXCEPTION
                    + " 消息队列消费异常", "IOException.class", e.getMessage());
            rabbitTemplate.convertAndSend(RabbitmqConfig.ROUTINGKEY_OBJECT_EXCEPTION, JSONUtil.toJsonStr(exception));
        }
    }

    @RabbitListener(queues = {RabbitmqConfig.QUEUE_EXCEPTION_LISTENER_WEATHER_FETCH_EXCEPTION})
    public void weatherFetchProcess(String msg, Channel channel, Message message) {
        WeatherFetchException weatherFetchException = JSONUtil.toBean(msg, WeatherFetchException.class);
        try {
            resolveAndSaveToDB(channel, message, weatherFetchException);
        } catch (IOException e) {
            ObjectException exception = new ObjectException(RabbitmqConfig.QUEUE_EXCEPTION_LISTENER_WEATHER_FETCH_EXCEPTION
                    + " 消息队列消费异常", "IOException.class", e.getMessage());
            rabbitTemplate.convertAndSend(RabbitmqConfig.ROUTINGKEY_OBJECT_EXCEPTION, JSONUtil.toJsonStr(exception));
        }
    }

    // TODO 消息队列消费异常记录

    private void resolveAndSaveToDB(Channel channel, Message message, ObjectException exception) throws IOException {
        ErrorLog errorLog = BeanUtil.copyProperties(exception, ErrorLog.class);
        errorLog.setLogId(errorLog.getErrorTime() + RandomUtil.randomNumbers(2));
        try {
            boolean isSave = errorLogMapper.saveErrorLog(errorLog);
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, !isSave);
        } catch (IOException e) {
            channel.basicNack(message.getMessageProperties().getDeliveryTag(),
                    false, true);
        }
    }
}
