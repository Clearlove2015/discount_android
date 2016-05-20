package com.schytd.discount.enties;

public class UserQrCode {
	private String num;
	private String name;
	private String clientid;

	public UserQrCode() {
		super();
	}

	public UserQrCode(String num, String name, String clientid) {
		super();
		this.num = num;
		this.name = name;
		this.clientid = clientid;
	}

	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getClientid() {
		return clientid;
	}

	public void setClientid(String clientid) {
		this.clientid = clientid;
	}

}
