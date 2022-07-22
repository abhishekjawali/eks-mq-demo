package com.example.demo;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeoutException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

@RestController
public class RabbitMQController {

	@Value("${mq.queue}")
	private String queueName;

	@Value("${mq.exchange}")
	private String exchange;

	@Value("${mq.routingKey}")
	private String routingKey;

	@Value("${spring.rabbitmq.host}")
	private String rabbitMqHost;

	@Value("${spring.rabbitmq.port}")
	private Integer rabbitMqPort;

	@Value("${spring.rabbitmq.username}")
	private String rabbitMqUserName;

	@Value("${spring.rabbitmq.password}")
	private String rabbitMqPassword;

	@GetMapping(value = "/test")
	public String producerTest() {
		return "tested";
	}

	@GetMapping(value = "/getMessage")
	public String getMessage() {

		try {
			Connection conn = getMQConnection();
			Channel channel = conn.createChannel();

			channel.queueDeclare(queueName, true, false, false, null);

			DefaultConsumer consumer = new DefaultConsumer(channel) {
				@Override
				public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
						byte[] body) throws IOException {

					String message = new String(body, "UTF-8");
					System.out.println("Message recevied from RabbitMQ --- " + message.toString());

				}
			};
			channel.basicConsume(queueName, true, consumer);


		} catch (Exception e) {
			e.printStackTrace();
			return "Error receiving the message";
		}
		return "Message received successfully";
	}

	@GetMapping(value = "/sendMessage")
	public String sendMessage() {

		try {

			Connection conn = getMQConnection();

			Channel channel = conn.createChannel();
			byte[] messageBodyBytes = "Hello, world! A message from EKS to Amazon MQ RabbitMQ!".getBytes();
			channel.basicPublish(exchange, routingKey,
					new AMQP.BasicProperties.Builder().contentType("text/plain").userId(rabbitMqUserName).build(),
					messageBodyBytes);

			channel.close();
			conn.close();

			return "Message sent successfully to RabbitMQ";
		} catch (Exception e) {
			
			e.printStackTrace();
			return "Error sending message";
		}

	}

	private Connection getMQConnection()
			throws NoSuchAlgorithmException, KeyManagementException, IOException, TimeoutException {
		ConnectionFactory factory = new ConnectionFactory();

		factory.setUsername(rabbitMqUserName);
		factory.setPassword(rabbitMqPassword);

		factory.setHost(rabbitMqHost);
		factory.setPort(rabbitMqPort);

		factory.useSslProtocol();

		Connection conn = factory.newConnection();
		return conn;
	}

}
