package com.schytd.discount.enties;


public class ScoreGetHistoryItem {
	private String baseInfoId;
	private String businessName;
	private String amount;
	private String score;
	private String theTime;
	public ScoreGetHistoryItem(String baseInfoId, String businessName,
			String score, String theTime,String amount) {
		this.baseInfoId = baseInfoId;
		this.businessName = businessName;
		this.score = score;
		this.theTime = theTime;
		this.amount=amount;
	}
	public ScoreGetHistoryItem(){}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getBaseInfoId() {
		return baseInfoId;
	}
	public void setBaseInfoId(String baseInfoId) {
		this.baseInfoId = baseInfoId;
	}
	public String getBusinessName() {
		return businessName;
	}
	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}
	public String getScore() {
		return score;
	}
	public void setScore(String score) {
		this.score = score;
	}
	public String getTheTime() {
		return theTime;
	}
	public void setTheTime(String theTime) {
		this.theTime = theTime;
	}
	
}
