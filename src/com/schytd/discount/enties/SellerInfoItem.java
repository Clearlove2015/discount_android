package com.schytd.discount.enties;

import java.io.Serializable;

public class SellerInfoItem implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id;// 商家id
	private String weight;// 权重
	private String status;// 状态
	private String businessName;// 商家名称
	private String issigned;// 是否签约
	private String address;// 地址
	private String contactPhoneNum;// 联系号码
	private String lng;// 经度
	private String lat;// 纬度
	private String businessDesc;// 商家简短则要
	private String logoPic;// 店招图片
	private String discount;// 折扣

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getWeight() {
		return weight;
	}

	public void setWeight(String weight) {
		this.weight = weight;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	public String getBusinessName() {
		return businessName;
	}

	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}

	public String getIssigned() {
		return issigned;
	}

	public void setIssigned(String issigned) {
		this.issigned = issigned;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getContactPhoneNum() {
		return contactPhoneNum;
	}

	public void setContactPhoneNum(String contactPhoneNum) {
		this.contactPhoneNum = contactPhoneNum;
	}

	public String getLng() {
		return lng;
	}

	public void setLng(String lng) {
		this.lng = lng;
	}

	public String getLat() {
		return lat;
	}

	public void setLat(String lat) {
		this.lat = lat;
	}

	public String getBusinessDesc() {
		return businessDesc;
	}

	public void setBusinessDesc(String businessDesc) {
		this.businessDesc = businessDesc;
	}

	public String getLogoPic() {
		return logoPic;
	}

	public void setLogoPic(String logoPic) {
		this.logoPic = logoPic;
	}

	public String getDiscount() {
		return discount;
	}

	public void setDiscount(String discount) {
		this.discount = discount;
	}
}
