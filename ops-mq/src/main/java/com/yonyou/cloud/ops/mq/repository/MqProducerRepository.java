package com.yonyou.cloud.ops.mq.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.yonyou.cloud.ops.mq.entity.MqProducer;

public interface MqProducerRepository extends MongoRepository<MqProducer, String> {

}
