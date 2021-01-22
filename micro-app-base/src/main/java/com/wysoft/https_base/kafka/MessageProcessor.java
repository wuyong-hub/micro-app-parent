package com.wysoft.https_base.kafka;

import java.util.Optional;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.wysoft.https_base.service.HttpService;
import com.wysoft.https_base.util.JSONUtil;

import net.sf.json.JSONObject;

@Component
public class MessageProcessor {
	@Autowired
	private Logger log;
	
	@Autowired
	private HttpService httpService;
	
	@KafkaListener(topics = "test", groupId = "test-group")
	public void doMessage(ConsumerRecord<?, ?> consumerRecord) {
		Optional<?> kafkaMessage = Optional.ofNullable(consumerRecord.value());
		if (kafkaMessage.isPresent()) {
			Object message = kafkaMessage.get();
			log.info("接收消息:" + message);
			
			//消息处理
			
			JSONObject json = JSONObject.fromObject(message);
			JSONObject result = httpService.service(json, "");
			
			if(JSONUtil.getInt(result, "code") != JSONUtil.SERVICE_OK) {
				log.info(JSONUtil.getString(result, "msg"));
			}
		}
	}
}
