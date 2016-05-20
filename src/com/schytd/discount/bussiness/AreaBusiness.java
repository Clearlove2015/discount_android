package com.schytd.discount.bussiness;

public interface AreaBusiness {
	//从服务器获取地点信息
	public Boolean getArea(String id) throws Exception;
	//从数据库查询所有省的id
	public Boolean getProvinceId() throws Exception;
	//从数据库查询所有市的id
	public Boolean getCityId() throws Exception;

}
