package com.schytd.discount.enties;

import java.io.Serializable;

public class SellerIndex implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String sid;
	private String bid;
	public SellerIndex(){}
	public SellerIndex(String sid, String bid) {
		super();
		this.sid = sid;
		this.bid = bid;
	}
	public String getSid() {
		return sid;
	}
	public void setSid(String sid) {
		this.sid = sid;
	}
	public String getBid() {
		return bid;
	}
	public void setBid(String bid) {
		this.bid = bid;
	}
	

}
