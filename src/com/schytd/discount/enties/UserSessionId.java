package com.schytd.discount.enties;

public class UserSessionId {
	private Integer id;
	private String sessionid;
	private long time;

	public UserSessionId() {
		super();
	}

	public UserSessionId(Integer id, String sessionid, long time) {
		super();
		this.id = id;
		this.sessionid = sessionid;
		this.time = time;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getSessionid() {
		return sessionid;
	}

	public void setSessionid(String sessionid) {
		this.sessionid = sessionid;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

}
