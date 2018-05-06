package com.yonyou.cloud.ops.mq.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.yonyou.cloud.ops.mq.entity.MqConsumeDetailInfo;
import com.yonyou.cloud.ops.mq.entity.MqConsumer;

public interface MqConsumerRepository extends MongoRepository<MqConsumer, String> {

	List<MqConsumeDetailInfo> findByMsgKey(String msgKey);
	
}