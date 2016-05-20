package com.schytd.discount.enties;

import java.io.Serializable;

public class SellerDetail implements Serializable {
	private static final long serialVersionUID = 1L;
	private String id;// 详细信息ID
	private String carouselImgs;// 轮播图 链接，以都好隔开
	private String baseInfoId;// 商家基本信息id
	private String detailsDesc;// 详细描述
	private String feature;// 商家特色
	private String environment;// 商家特色
	private String tips; // 消息提示
	private String contactPhoneNum;// 联系号码
	private String discount;// 折扣

	public String getDiscount() {
		return discount;
	}

	public void setDiscount(String discount) {
		this.discount = discount;
	}

	public String getContactPhoneNum() {
		return contactPhoneNum;
	}

	public void setContactPhoneNum(String contactPhoneNum) {
		this.contactPhoneNum = contactPhoneNum;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	private String address; // 地址

	public SellerDetail(String id, String carouselImgs, String baseInfoId,
			String detailsDesc, String feature, String environment,
			String tips, String contactPhoneNum, String address, String discount) {
		this.id = id;
		this.carouselImgs = carouselImgs;
		this.baseInfoId = baseInfoId;
		this.detailsDesc = detailsDesc;
		this.feature = feature;
		this.environment = environment;
		this.tips = tips;
		this.contactPhoneNum = contactPhoneNum;
		this.address = address;
		this.discount = discount;
	}

	public SellerDetail() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCarouselImgs() {
		return carouselImgs;
	}

	public void setCarouselImgs(String carouselImgs) {
		this.carouselImgs = carouselImgs;
	}

	public String getBaseInfoId() {
		return baseInfoId;
	}

	public void setBaseInfoId(String baseInfoId) {
		this.baseInfoId = baseInfoId;
	}

	public String getDetailsDesc() {
		return detailsDesc;
	}

	public void setDetailsDesc(String detailsDesc) {
		this.detailsDesc = detailsDesc;
	}

	public String getFeature() {
		return feature;
	}

	public void setFeature(String feature) {
		this.feature = feature;
	}

	public String getEnvironment() {
		return environment;
	}

	public void setEnvironment(String environment) {
		this.environment = environment;
	}

	public String getTips() {
		return tips;
	}

	public void setTips(String tips) {
		this.tips = tips;
	}

}
