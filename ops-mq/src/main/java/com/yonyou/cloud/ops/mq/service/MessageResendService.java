package com.yonyou.cloud.ops.mq.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import com.yonyou.cloud.common.service.BaseEsService;
import com.yonyou.cloud.ops.mq.entity.MqMessage;
import com.yonyou.cloud.ops.mq.repository.MqMessageRepository;

@Service
public class MessageResendService extends BaseEsService<MqMessage>{
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private MqMessageRepository mqMessageRepository;
	
	@Autowired
	@Qualifier("restTemplate")
	private RestTemplate restTemplate;
	
	@Autowired
	@Qualifier("restTemplateWithLB")
	private RestTemplate restTemplateWithLB;

	public boolean resendMessage(String msgKey, String type, String serviceUrl) {
		try {
			MqMessage msg = mqMessageRepository.findByMsgKey(msgKey);
			String su = StringUtils.isEmpty(serviceUrl)?msg.getServiceUrl():serviceUrl;
			if(msg != null && !StringUtils.isEmpty(su)){
				String url = su + "/msg/reset/" + type + "/" + msgKey;
				RestTemplate r = containIp(serviceUrl) ? restTemplate : restTemplateWithLB;
				logger.info("resendMessage======>>>>>>get url:{}", url);
				return r.getForObject(url, Boolean.class);
			} else {
				throw new Exception("resendMessage msgKey does not exist or ServiceUrl is empty.");
			}
		} catch (Exception e) {
			return false;
		}
	}

	private boolean containIp(String str) {
		if(StringUtils.isEmpty(str)) 
			return false;
		String regex = "\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}";
		return str.split(regex).length == 1?false:true;
	}

} 
