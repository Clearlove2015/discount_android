package com.schytd.discount.enties;

public class CommodityHistory {
	private String name;
	private String price;
	private String commodity_picture;

	public CommodityHistory() {
		super();
	}

	public CommodityHistory(String name, String price, String commodity_picture) {
		super();
		this.name = name;
		this.price = price;
		this.commodity_picture = commodity_picture;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getCommodity_picture() {
		return commodity_picture;
	}

	public void setCommodity_picture(String commodity_picture) {
		this.commodity_picture = commodity_picture;
	}

}
