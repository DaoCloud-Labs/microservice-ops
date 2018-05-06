package com.yonyou.cloud.ops.mq.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.yonyou.cloud.ops.mq.entity.MqConsumeDetailInfo;

public interface MqConsumeDetailInfoRepository extends MongoRepository<MqConsumeDetailInfo, String> {

    public List<MqConsumeDetailInfo> findByMsgKey(String msgKey);

}
