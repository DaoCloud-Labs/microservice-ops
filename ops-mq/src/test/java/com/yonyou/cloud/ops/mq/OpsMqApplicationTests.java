package com.yonyou.cloud.ops.mq;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.xiaoleilu.hutool.json.JSONUtil;
import com.yonyou.cloud.common.beans.PageResultResponse;
import com.yonyou.cloud.ops.mq.common.MqMessageStatus;
import com.yonyou.cloud.ops.mq.common.MqOpsConstant;
import com.yonyou.cloud.ops.mq.dao.MqDataApi;
import com.yonyou.cloud.ops.mq.entity.MqConsumeDetailInfo;
import com.yonyou.cloud.ops.mq.entity.MqConsumer;
import com.yonyou.cloud.ops.mq.entity.MqData;
import com.yonyou.cloud.ops.mq.entity.MqMessage;
import com.yonyou.cloud.ops.mq.entity.MqProducer;
import com.yonyou.cloud.ops.mq.service.MqConsumeDetailInfoService;
import com.yonyou.cloud.ops.mq.service.MqConsumerService;
import com.yonyou.cloud.ops.mq.service.MqMessageService;
import com.yonyou.cloud.ops.mq.service.MqProducerService;

@RunWith(SpringRunner.class)
@SpringBootTest
@Ignore
public class OpsMqApplicationTests {
	@Autowired
	private TransportClient client;

	@Test
	public void contextLoads() {
//		MultiGetRequestBuilder builder = client.prepareMultiGet();
//		//builder.add("demo_mq", "track","");
//		builder.add("demo_mq", "track","*");
//		client.prepareMultiGet().
		SearchResponse searchResponse  = client.prepareSearch("demo_mq")
		.setTypes("track")
		.setQuery(QueryBuilders.matchAllQuery())
		.setSearchType(SearchType.QUERY_THEN_FETCH)  
        .setFrom(10).setSize(10)//分页  
        .addSort("@timestamp", SortOrder.DESC)//排序  
        .get();  
		
		SearchHits hits = searchResponse.getHits();  
        long total = hits.getTotalHits();  
        System.out.println(total);  
        SearchHit[] searchHits = hits.getHits();  
        for(SearchHit s : searchHits)  
        {  
        	
        	String properties = s.getSource().get("properties").toString();
        	System.out.println(properties);
        	System.out.println(fromStr(properties).get("column1"));
        }  
		
		
//		MultiGetResponse responses = builder.get();
//		System.out.println(responses.getResponses());
//		responses.forEach(response -> System.out.println(response.getResponse().getSource().get("properties")));
	}
	
	public Map<String,String> fromStr(String str){
		Map<String, String> strMap = new HashMap<String, String>();  
		String[] strs = str.split(",");
		for(String string : strs){
			String[] ss = string.trim().split("=");
			strMap.put(ss[0], ss[1]);
		}
		
		return strMap;
		
	}
	
	
	@Autowired
	MqDataApi mqDataApi;

	
	@Test
	public void test2(){
		PageResultResponse<MqData> page = mqDataApi.pageQueryMqData(1, 20, "demo_mq", "track", "properties.column1:9ef524e9*", "@timestamp", "desc");
		System.out.println(page.getData());
		
	}
	
	@Autowired
	MqProducerService mqProducerService;
	
	
	@Test
	public void test3() throws JsonProcessingException{
//		System.out.println("data===="+mqProducerService.selectOne("mq", "msgKey:cf2b37f5-7cf4-4391-8aeb-5eac8a17da84"));
//		System.out.println("data===="+mqProducerService.selectList("mq", "msgKey:cf2b37f5-7cf4-4391-8aeb-5eac8a17da84"));
		
		MqProducer pro = new MqProducer();
		pro.setData("666666");
		pro.setHost("10");
		pro.setMsg("msg");
		pro.setSuccess("false");
		pro.setMsgKey("bbbb");
		
//		mqProducerService.insert("mq", pro);
		
//		mqProducerService.update("mq",pro, "AV_JMnIaZG64QQzzpAPt");
		
//		System.out.println("data===="+mqProducerService.selectOne("mq", "_type:"));
		System.out.println("dataList===="+mqProducerService.selectList("mq", "occurTime:1510882840569").size());
		
	}
	
	@Autowired
	private MqMessageService mqMessageService;
	@Autowired
	private MqConsumerService mqConsumerService;
	@Autowired
	MqConsumeDetailInfoService mqConsumeDetailInfoService;
	@Test
	public void mqMessageServiceTest(){
		MqMessage m = mqMessageService.selectOne(MqOpsConstant.INDEX, "msgKey:49c5ea89-9df4-4d17-bbad-676ebd4419a9");
		m.setSuccess("true");
		try {
			mqMessageService.update(MqOpsConstant.INDEX, m, m.getId());
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(JSONUtil.toJsonPrettyStr(m));
	}
	
	@Test
	public void insertTest(){
		for(int i =0;i<100;i++){
			String uuid = UUID.randomUUID().toString();
			
			MqMessage mqMessage = new MqMessage();
			mqMessage.setMsgKey(uuid);
			mqMessage.setOccurTime(System.currentTimeMillis());
			mqMessage.setProduceSuccessTime(System.currentTimeMillis());
			mqMessage.setConsumeSuccessTime(System.currentTimeMillis());
			mqMessage.setData("{\"alco\": \"cn.sh.ly\"}");
			mqMessage.setHost("10.32.38.3");
			mqMessage.setExchangeName("exchange_dol_l");
			mqMessage.setMsg("ok");
			mqMessage.setRoutingKey("keyyyyyyy");
			mqMessage.setSender("发送方clod");
			mqMessage.setSuccess("true");
			mqMessage.setStatus(MqMessageStatus.PRODUCED.toString());
			MqConsumer mqConsumer = new MqConsumer();
			mqConsumer.setMsgKey(uuid);
			mqConsumer.setHost("host");
			mqConsumer.setData("{\"alco\": \"cn.sh.ly\"}");
			mqConsumer.setMsg("msg");
			mqConsumer.setQueueName("queuename");
			mqConsumer.setOccurTime(System.currentTimeMillis());
			MqConsumeDetailInfo mqConsumeDetailInfo = new MqConsumeDetailInfo();
			mqConsumeDetailInfo.setMsgKey(uuid);
			mqConsumeDetailInfo.setAppName("aapname" + i);
			mqConsumeDetailInfo.setData("sdfasdfsgsdfasdfs");
			mqConsumeDetailInfo.setHost("host");
			mqConsumeDetailInfo.setMsg("msg");
			mqConsumeDetailInfo.setOccurTime(System.currentTimeMillis());
			mqConsumeDetailInfo.setQueueName("queuename");
			mqConsumeDetailInfo.setSuccess("ture");
			try {
//				mqConsumerService.insert(MqOpsConstant.INDEX, mqConsumer);
				mqMessageService.insert(MqOpsConstant.INDEX, mqMessage);
//				mqConsumeDetailInfo.setConsumerId(String.valueOf(i));
//				mqConsumeDetailInfoService.insert(MqOpsConstant.INDEX, mqConsumeDetailInfo);
//				mqConsumeDetailInfo.setConsumerId(String.valueOf(i*10));
//				mqConsumeDetailInfoService.insert(MqOpsConstant.INDEX, mqConsumeDetailInfo);
//				mqConsumeDetailInfo.setConsumerId(String.valueOf(i*100));
//				mqConsumeDetailInfoService.insert(MqOpsConstant.INDEX, mqConsumeDetailInfo);
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
