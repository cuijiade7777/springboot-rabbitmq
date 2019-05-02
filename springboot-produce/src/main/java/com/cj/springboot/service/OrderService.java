package com.cj.springboot.service;

import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.cj.springboot.constant.Constants;
import com.cj.springboot.entity.BrokerMessageLog;
import com.cj.springboot.entity.Order;
import com.cj.springboot.mapper.BrokerMessageLogMapper;
import com.cj.springboot.mapper.OrderMapper;
import com.cj.springboot.producer.RabbitOrderSender;

@Service
public class OrderService {
	
	@Autowired
	private OrderMapper orderMapper;
	
	@Autowired
	private BrokerMessageLogMapper brokerMessageLogMapper;
	
	@Autowired
	private RabbitOrderSender rabbitOrderSender;
	
	
	//创建订单
	public void createOrder(Order order) {
		//使用当前时间当做订单创建时间
		Date orderTime = new Date();
		//插入业务数据
		orderMapper.insert(order);
		//插入消息记录表数据
		BrokerMessageLog brokerMessageLog = new BrokerMessageLog();
		//消息唯一ID
		brokerMessageLog.setMessageId(order.getMessageId());
		//保存消息整体
		brokerMessageLog.setMessage(JSONObject.toJSONString(order));
		//设置消息状态为0 表示发送中
		brokerMessageLog.setStatus("0");
		//设置消息未确认超时时间窗口为一分钟
		brokerMessageLog.setNextRetry(DateUtils.addMinutes(orderTime, Constants.ORDER_TIMEOUT));
		brokerMessageLog.setCreateTime(new Date());
		brokerMessageLog.setUpdateTime(new Date());
		brokerMessageLogMapper.insert(brokerMessageLog);
		
		//发送消息
		rabbitOrderSender.sendOrder(order);

	}
}
