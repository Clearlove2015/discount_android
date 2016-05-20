package com.schytd.discount.enties;

public class ConsumptionTimesAndAmount {
	private String times;
	private String total;
	public ConsumptionTimesAndAmount(){}
	public ConsumptionTimesAndAmount(String times, String total) {
		this.times = times;
		this.total = total;
	}
	public String getTimes() {
		return times;
	}
	public void setTimes(String times) {
		this.times = times;
	}
	public String getTotal() {
		return total;
	}
	public void setTotal(String total) {
		this.total = total;
	};
}
