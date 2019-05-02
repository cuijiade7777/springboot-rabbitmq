package com.cj.springboot;

import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.cj.springboot.entity.Order;
import com.cj.springboot.producer.RabbitOrderSender;
import com.cj.springboot.service.OrderService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTests {

	@Test
	public void contextLoads() {
	}
	
	@Autowired
	private RabbitOrderSender orderSender;
	
	@Test
	public void test() {
		Order order = new Order();
		order.setId("201904270000000000000000001");
		order.setName("鲁班七号");
		order.setMessageId(System.currentTimeMillis()+"$"+UUID.randomUUID().toString());
		
		orderSender.sendOrder(order);
	}
	
	@Autowired
	private OrderService orderService;
	
	@Test
	public void testCreateOrder() {
		Order order = new Order();
		order.setId("201905200000002");
		order.setName("测试创建订单");
		order.setMessageId(System.currentTimeMillis()+"$"+UUID.randomUUID().toString());
		orderService.createOrder(order);
	}
}
