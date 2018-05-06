package com.yonyou.cloud.ops.mq.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.yonyou.cloud.ops.mq.entity.MqMessage;

public interface MqMessageRepository extends MongoRepository<MqMessage, String> {
	
	public MqMessage findByMsgKey(String msgKey);

}
