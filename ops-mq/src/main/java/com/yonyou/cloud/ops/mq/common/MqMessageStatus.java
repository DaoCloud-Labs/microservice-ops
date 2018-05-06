package com.yonyou.cloud.ops.mq.common;

public enum MqMessageStatus {
	
	PRODUCED("已发送"), PRODUCE_FAIL("发送失败"), CONSUMED("已消费"), CONSUME_FAIL("消费失败");
	
	private String name;

	private MqMessageStatus(String name){
		this.name = name;
	}

	public String getName() {
		return name;
	}

}
