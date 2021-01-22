package com.wysoft.https_base.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

import net.sf.json.JSONObject;

@Component
public class MessageSender {
	@Autowired
	private KafkaTemplate kfTemplate;
	
	public ListenableFuture send(String topic,JSONObject message) {
		return kfTemplate.send(topic,message.toString());
	}
	
}
