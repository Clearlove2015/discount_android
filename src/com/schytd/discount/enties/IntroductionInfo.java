package com.schytd.discount.enties;

public class IntroductionInfo {
//	推广码
	private String introductionCode;
//	用户基本信息id
	private String userBaseId;
//	有效推广人数
	private String effectiveIntroductionCount;
//	推广总人数
	private String allIntroductionCount;
	public String getIntroductionCode() {
		return introductionCode;
	}
	public void setIntroductionCode(String introductionCode) {
		this.introductionCode = introductionCode;
	}
	public String getUserBaseId() {
		return userBaseId;
	}
	public void setUserBaseId(String userBaseId) {
		this.userBaseId = userBaseId;
	}
	public String getEffectiveIntroductionCount() {
		return effectiveIntroductionCount;
	}
	public void setEffectiveIntroductionCount(String effectiveIntroductionCount) {
		this.effectiveIntroductionCount = effectiveIntroductionCount;
	}
	public String getAllIntroductionCount() {
		return allIntroductionCount;
	}
	public void setAllIntroductionCount(String allIntroductionCount) {
		this.allIntroductionCount = allIntroductionCount;
	}
}
