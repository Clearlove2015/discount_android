package com.schytd.discount.enties;

public class ConsumHistoryItem {
	// 消费id
	private String id;
	// 用户基本信息ID
	private String userBaseInfoId;
	// 商家名称
	private String bussinessName;
	// 消费金额
	private String amount;
	// 消费时间
	private String consumptionTime;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserBaseInfoId() {
		return userBaseInfoId;
	}

	public void setUserBaseInfoId(String userBaseInfoId) {
		this.userBaseInfoId = userBaseInfoId;
	}

	public String getBussinessName() {
		return bussinessName;
	}

	public void setBussinessName(String bussinessName) {
		this.bussinessName = bussinessName;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getConsumptionTime() {
		return consumptionTime;
	}

	public void setConsumptionTime(String consumptionTime) {
		this.consumptionTime = consumptionTime;
	}

	public ConsumHistoryItem() {
	}

	public ConsumHistoryItem(String id, String userBaseInfoId,
			String bussinessName, String amount, String consumptionTime) {
		this.id = id;
		this.userBaseInfoId = userBaseInfoId;
		this.bussinessName = bussinessName;
		this.amount = amount;
		this.consumptionTime = consumptionTime;
	}

}
