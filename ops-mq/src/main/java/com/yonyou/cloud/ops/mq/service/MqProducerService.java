package com.yonyou.cloud.ops.mq.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yonyou.cloud.common.service.BaseEsService;
import com.yonyou.cloud.ops.mq.entity.MqProducer;
import com.yonyou.cloud.ops.mq.repository.MqProducerRepository;

@Service
public class MqProducerService extends BaseEsService<MqProducer>{
	@Autowired
	private MqProducerRepository mqProducerRepository;
	
	public void save (MqProducer producer) throws Exception{
//		List<MqProducer> producers = selectList(index, "msgKey:" + producer.getMsgKey() + " AND success:true");
//		if(producers.isEmpty()){
//			insert(index, producer);
//		}else{
//			logger.error("this message status is success,msgkey:{}", producer.getMsgKey());
//		}
		mqProducerRepository.save(producer);
	}
	
}
