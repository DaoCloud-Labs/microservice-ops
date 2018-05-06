package com.yonyou.cloud.ops.mq.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yonyou.cloud.common.service.BaseEsService;
import com.yonyou.cloud.ops.mq.entity.MqConsumeDetailInfo;
import com.yonyou.cloud.ops.mq.entity.MqConsumer;
import com.yonyou.cloud.ops.mq.repository.MqConsumeDetailInfoRepository;

@Service
public class MqConsumeDetailInfoService extends BaseEsService<MqConsumeDetailInfo>{
	
	@Autowired
	private MqConsumeDetailInfoRepository mqConsumeDetailInfoRepository;
	
//	private Logger logger = LoggerFactory.getLogger(this.getClass());

	public void save(MqConsumer mqConsumer) throws Exception{
		MqConsumeDetailInfo mqConsumeDetailInfo = new MqConsumeDetailInfo();
		BeanUtils.copyProperties(mqConsumer, mqConsumeDetailInfo);
//		List<MqConsumeDetailInfo> consumeDetails = selectList(MqOpsConstant.INDEX, "msgKey:" + mqConsumeDetailInfo.getMsgKey() + " AND success:true");
//		if(consumeDetails.isEmpty()){
//			insert(MqOpsConstant.INDEX, mqConsumeDetailInfo);
//		}else{
//			logger.error("this message status is success,msgkey:{}", mqConsumeDetailInfo.getMsgKey());
//		}
		mqConsumeDetailInfoRepository.save(mqConsumeDetailInfo);
//		insert(MqOpsConstant.INDEX, mqConsumeDetailInfo);
	}
}
