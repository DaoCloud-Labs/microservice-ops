package com.yonyou.cloud.ops.mq.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.yonyou.cloud.common.beans.PageResultResponse;
import com.yonyou.cloud.common.service.BaseEsService;
import com.yonyou.cloud.common.service.utils.PageQuery;
import com.yonyou.cloud.ops.mq.common.MqMessageStatus;
import com.yonyou.cloud.ops.mq.common.MqMessageType;
import com.yonyou.cloud.ops.mq.entity.MqConsumeDetailInfo;
import com.yonyou.cloud.ops.mq.entity.MqMessage;
import com.yonyou.cloud.ops.mq.repository.MqConsumeDetailInfoRepository;
import com.yonyou.cloud.ops.mq.repository.MqMessageRepository;

@Service
public class MqMessageService extends BaseEsService<MqMessage>{
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	MqConsumeDetailInfoService mqConsumeDetailInfoService;
	
	@Autowired
	private MqMessageRepository mqMessageRepository;
	
	@Autowired
	private MqConsumeDetailInfoRepository mqConsumeDetailInfoRepository;
	
	@Autowired
	private MongoTemplate mongoTemplate;

	public void save (MqMessage mqMessage, MqMessageType mqMessageType) throws Exception{
		
		MqMessage oldMessage = mqMessageRepository.findByMsgKey(mqMessage.getMsgKey());
		
//		MqMessage oldMessage = selectOne(MqOpsConstant.INDEX, "msgKey:" + "\"" +mqMessage.getMsgKey() + "\"");
		
		if(oldMessage == null){
			updateMsgStatus(mqMessage, mqMessage, mqMessageType);
			mqMessageRepository.insert(mqMessage);
		} else {
			updateMsgStatus(oldMessage, mqMessage, mqMessageType);
	        Query query=new Query(Criteria.where("msgKey").is(oldMessage.getMsgKey()));
	        Update update= new Update().set("produceSuccessTime", oldMessage.getProduceSuccessTime())
	        						   .set("produceStatus", oldMessage.getProduceStatus())
	        						   .set("produceFailTimes", oldMessage.getProduceFailTimes())
	        						   .set("consumeSuccessTime", oldMessage.getConsumeSuccessTime())
	        						   .set("consumeStatus", oldMessage.getConsumeStatus())
	        						   .set("consumeFailTimes", oldMessage.getConsumeFailTimes());
			mongoTemplate.updateMulti(query, update, MqMessage.class);
//			update(MqOpsConstant.INDEX, oldMessage, oldMessage.getId());
		}
	}

	private void updateMsgStatus(MqMessage oldMessage, MqMessage newMessage, MqMessageType mqMessageType) {
		switch (mqMessageType) {
		case PRODUCER:
			if(Boolean.valueOf(newMessage.getSuccess())){
				oldMessage.setProduceSuccessTime(newMessage.getOccurTime());
				oldMessage.setProduceStatus(MqMessageStatus.PRODUCED.name());
			}else{
				oldMessage.setProduceFailTimes(oldMessage.getProduceFailTimes() + 1);
				oldMessage.setProduceStatus(MqMessageStatus.PRODUCE_FAIL.name());
			}
			break;
			
		case CONSUMER:
			
			boolean allSucces = true;
			
			try {
				List<MqConsumeDetailInfo> mqConsumeDetailInfo = mqConsumeDetailInfoRepository.findByMsgKey(newMessage.getMsgKey());
//				List<MqConsumeDetailInfo> mqConsumeDetailInfo = mqConsumeDetailInfoService.selectList(MqOpsConstant.INDEX, "msgKey:" + "\"" +newMessage.getMsgKey() + "\"");
				//分组
				Map<String, List<MqConsumeDetailInfo>> m = mqConsumeDetailInfo.stream().filter(t -> t.getConsumerId() != null)
						.collect(Collectors.groupingBy(MqConsumeDetailInfo::getConsumerId));
				//聚合
				for(List<MqConsumeDetailInfo> l : m.values()){
					allSucces = allSucces & l.parallelStream().filter(t -> Boolean.valueOf(t.getSuccess())).count() > 0 ? true : false;
				}
			} catch (Exception e) {
				logger.error("mqConsumeDetailInfo converge fail.");
			}
			
			if(Boolean.valueOf(newMessage.getSuccess())){
				oldMessage.setConsumeSuccessTime(newMessage.getOccurTime());
			}else{
				oldMessage.setConsumeFailTimes(oldMessage.getConsumeFailTimes() + 1);
			}
			oldMessage.setConsumeStatus(allSucces?MqMessageStatus.CONSUMED.name():MqMessageStatus.CONSUME_FAIL.name());
			break;
			
		default:
			break;
		}
	}

	public PageResultResponse<MqMessage> queryByCondition(PageQuery request) {
        Query query = new Query();
        
        if(request.get("orderBy") != null && request.get("orderType") != null) {
        	query.with(new Sort(new Order(Direction.valueOf(request.get("orderType").toString().toUpperCase()),request.get("orderBy").toString())));
        }
        
        if(request.get("occurStartTime") != null && request.get("occurEndTime") != null){
    		query.addCriteria(Criteria.where("occurTime").gte(request.get("occurStartTime")).lte(request.get("occurEndTime")));
        } else if (request.get("occurStartTime") != null) {
    		query.addCriteria(Criteria.where("occurTime").gte(request.get("occurStartTime")));  
        } else if (request.get("occurEndTime") != null) {
        	query.addCriteria(Criteria.where("occurTime").lte(request.get("occurEndTime")));  
        }
        
        request.remove("orderBy");
        request.remove("orderType");
        request.remove("occurStartTime");
        request.remove("occurEndTime");
        
        for (Map.Entry<String, Object> entry : request.entrySet()) {
        	if(!StringUtils.isEmpty(entry.getValue())) {
        		query.addCriteria(Criteria.where(entry.getKey()).regex(".*?" + entry.getValue().toString()+ ".*"));  
        	}
        }
        
        query.skip((request.getPage() -1) * request.getLimit());
        query.limit(request.getLimit());
        List<MqMessage> l = mongoTemplate.find(query, MqMessage.class);
        long count = mongoTemplate.count(query, MqMessage.class);
		return new PageResultResponse<MqMessage>(count, l);
	}
} 
