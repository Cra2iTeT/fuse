package com.fuse.listener;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.fuse.common.SystemCode;
import com.fuse.config.RabbitmqConfig;
import com.fuse.domain.pojo.ErrorLog;
import com.fuse.domain.vo.R;
import com.fuse.exception.ObjectException;
import com.fuse.exception.PythonScriptRunException;
import com.fuse.exception.WeatherException;
import com.fuse.exception.WeatherFetchException;
import com.fuse.mapper.ErrorLogMapper;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.fuse.common.SystemCode.PYTHON_SCRIPT_ERROR;
import static com.fuse.common.SystemCode.WEATHER_ERROR;
import static com.fuse.config.RabbitmqConfig.*;

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

    @RabbitListener(queues = {QUEUE_EXCEPTION_LISTENER_PYTHON_SCRIPT_EXCEPTION})
    public void pythonScriptProcess(String msg, Channel channel, Message message) {
        PythonScriptRunException pythonScriptRunException = JSONUtil
                .toBean(msg, PythonScriptRunException.class);
        try {
            WebSocketService.sendMessageToAll(new R(PYTHON_SCRIPT_ERROR.getCode(),
                    PYTHON_SCRIPT_ERROR.getMsg(), null));
            resolveAndSaveToDB(channel, message, pythonScriptRunException);
        } catch (IOException e) {
            ObjectException exception = new ObjectException(QUEUE_EXCEPTION_LISTENER_PYTHON_SCRIPT_EXCEPTION
                    + " 消息队列消费异常", IOException.class.getName(), e.getMessage());
            rabbitTemplate.convertAndSend(ROUTINGKEY_OBJECT_EXCEPTION, JSONUtil.toJsonStr(exception));
        }
    }

    @RabbitListener(queues = {RabbitmqConfig.QUEUE_EXCEPTION_LISTENER_WEATHER_FETCH_EXCEPTION})
    public void weatherFetchProcess(String msg, Channel channel, Message message) {
        WeatherFetchException weatherFetchException = JSONUtil.toBean(msg, WeatherFetchException.class);
        try {
            WebSocketService.sendMessageToAll(new R(WEATHER_ERROR.getCode(), WEATHER_ERROR.getMsg(), null));
            resolveAndSaveToDB(channel, message, weatherFetchException);
        } catch (IOException e) {
            ObjectException exception = new ObjectException(QUEUE_EXCEPTION_LISTENER_WEATHER_FETCH_EXCEPTION
                    + " 消息队列消费异常", IOException.class.getName(), e.getMessage());
            rabbitTemplate.convertAndSend(ROUTINGKEY_OBJECT_EXCEPTION, JSONUtil.toJsonStr(exception));
        }
    }

    @RabbitListener(queues = {QUEUE_EXCEPTION_LISTENER_WEATHER_EXCEPTION})
    public void weatherProcess(String msg, Channel channel, Message message) {
        WeatherException weatherException = JSONUtil.toBean(msg, WeatherException.class);
        try {
            WebSocketService.sendMessageToAll(new R(WEATHER_ERROR.getCode(), WEATHER_ERROR.getMsg(), null));
            resolveAndSaveToDB(channel, message, weatherException);
        } catch (IOException e) {
            ObjectException exception = new ObjectException(QUEUE_EXCEPTION_LISTENER_WEATHER_EXCEPTION
                    + " 消息队列消费异常", IOException.class.getName(), e.getMessage());
            rabbitTemplate.convertAndSend(ROUTINGKEY_OBJECT_EXCEPTION, JSONUtil.toJsonStr(exception));
        }
    }

    @RabbitListener(queues = {RabbitmqConfig.QUEUE_EXCEPTION_LISTENER_OBJECT_EXCEPTION})
    public void ObjectProcess(String msg, Channel channel, Message message) {
        ObjectException exception = JSONUtil.toBean(msg, ObjectException.class);
        try {
            WebSocketService.sendMessageToAll(new R(SystemCode.MQ_ERROR.getCode(),
                    SystemCode.MQ_ERROR.getMsg(), null));
            resolveAndSaveToDB(channel, message, exception);
        } catch (IOException e) {
            ObjectException newException = new ObjectException(QUEUE_EXCEPTION_LISTENER_OBJECT_EXCEPTION
                    + " 消息队列消费异常", IOException.class.getName(), e.getMessage());
            rabbitTemplate.convertAndSend(ROUTINGKEY_OBJECT_EXCEPTION, JSONUtil.toJsonStr(newException));
        }
    }

    private void resolveAndSaveToDB(Channel channel, Message message, ObjectException exception) throws IOException {
        ErrorLog errorLog = BeanUtil.copyProperties(exception, ErrorLog.class);
        errorLog.setLogId(errorLog.getErrorTime() + RandomUtil.randomNumbers(2));
        try {
            int isSave = errorLogMapper.save(errorLog);
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, isSave == 1);
        } catch (IOException e) {
            channel.basicNack(message.getMessageProperties().getDeliveryTag(),
                    false, true);
        }
    }
}
