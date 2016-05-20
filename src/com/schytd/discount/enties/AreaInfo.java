package com.schytd.discount.enties;

public class AreaInfo {
	private String key;
	private String name;
	private String parentKey;

	public AreaInfo() {
		super();
	}

	public AreaInfo(String key, String name, String parentKey) {
		super();
		this.key = key;
		this.name = name;
		this.parentKey = parentKey;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getParentKey() {
		return parentKey;
	}

	public void setParentKey(String parentKey) {
		this.parentKey = parentKey;
	}

}
