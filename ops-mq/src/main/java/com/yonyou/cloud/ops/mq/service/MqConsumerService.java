package com.yonyou.cloud.ops.mq.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import com.yonyou.cloud.common.service.BaseEsService;
import com.yonyou.cloud.ops.mq.entity.MqConsumer;
import com.yonyou.cloud.ops.mq.repository.MqConsumerRepository;

@Service
public class MqConsumerService extends BaseEsService<MqConsumer>{
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private MqConsumerRepository mqConsumerRepository;
	
	public void save(MqConsumer mqConsumer) throws Exception{
		
		if(Boolean.valueOf(mqConsumer.getSuccess())){
			mqConsumer.setSuccessTime(mqConsumer.getOccurTime());
		}else{
			mqConsumer.setFailTimes(mqConsumer.getFailTimes() + 1);
		}
		
		MqConsumer msgExample = new MqConsumer();
		msgExample.setMsgKey(mqConsumer.getMsgKey());
		msgExample.setConsumerId(mqConsumer.getConsumerId());
        Example<MqConsumer> example = Example.of(msgExample);
        MqConsumer consumer = mqConsumerRepository.findOne(example);
        
//		MqConsumer consumer = selectOne(MqOpsConstant.INDEX, "msgKey:" + mqConsumer.getMsgKey() + " AND " + "consumerId:" + mqConsumer.getConsumerId());
		if(consumer == null){
			mqConsumerRepository.save(mqConsumer);
//			insert(MqOpsConstant.INDEX, mqConsumer);
		} else if(Boolean.valueOf(consumer.getSuccess())){
			logger.error("this message has been consumed==========>>>>>>msgkey:{}  consmerId{}", consumer.getMsgKey(), consumer.getConsumerId());
		}  else {
//			update(MqOpsConstant.INDEX, mqConsumer, consumer.getId());
		}
	}
}
