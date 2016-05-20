package com.schytd.discount.net;

import java.util.List;

import com.schytd.discount.enties.AreaInfo;

public interface AreaNet {
	//从服务器获取地点信息
	public List<AreaInfo> getArea(String...params) throws Exception;

}
