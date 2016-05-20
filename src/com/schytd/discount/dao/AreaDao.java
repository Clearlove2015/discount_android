package com.schytd.discount.dao;

import java.util.List;

import com.schytd.discount.enties.AreaInfo;

public interface AreaDao {
	//存入
	public void insertPlace(AreaInfo areaInfo) throws Exception;
	//查询所有省份id
	public List<String> selectProvinceId() throws Exception;
	//查询所有市id
	public List<String> selectCityId() throws Exception;
	//查询
	public String selectPlace(String name) throws Exception;

}
