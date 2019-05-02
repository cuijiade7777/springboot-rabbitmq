package com.cj.springboot.producer;


import java.util.Date;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ConfirmCallback;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cj.springboot.constant.Constants;
import com.cj.springboot.entity.Order;
import com.cj.springboot.mapper.BrokerMessageLogMapper;

@Component
public class RabbitOrderSender {

	@Autowired
	private RabbitTemplate rabbitTemplate;
	
	@Autowired
	private BrokerMessageLogMapper brokerMessageLogMapper;
	
	//回调函数：confirm确认
	final ConfirmCallback confirmCallback = new RabbitTemplate.ConfirmCallback() {
		
		@Override
		public void confirm(CorrelationData correlationData, boolean ack, String cause) {
			System.err.println("correlationData:"+correlationData);
			String messageId = correlationData.getId();
			if(ack) {
				//如果confirm返回成功 则进行更新
				brokerMessageLogMapper.changeBrokerMessageLogStatus(messageId, Constants.ORDER_SEND_SUCCESS, new Date());
			}else {
				//失败则进行具体的后续操作：重试或者补偿等手段
				System.err.println("异常处理。。。");
			}
		}
	};
	
	//发送消息方法调用：构建自定义对象消息
	public void sendOrder(Order order) {
		//监听回调函数
		rabbitTemplate.setConfirmCallback(confirmCallback);
		
		//消息唯一ID
		CorrelationData correlationData = new CorrelationData(order.getMessageId());
		
		rabbitTemplate.convertAndSend("order-exchange",
				"order.ABC", 
				order, 		//消息体内容
				correlationData);   //消息唯一ID
	}
	
	
}
