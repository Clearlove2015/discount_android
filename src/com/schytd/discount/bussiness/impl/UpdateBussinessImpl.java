package com.schytd.discount.bussiness.impl;

import java.util.List;

import com.schytd.discount.bussiness.UpdateBussiness;
import com.schytd.discount.net.UpdateVersion;
import com.schytd.discount.net.impl.UpdateVersionImpl;

public class UpdateBussinessImpl implements UpdateBussiness {
	UpdateVersion updateVersion = new UpdateVersionImpl();

	@Override
	public List<String> update(String... params) throws Exception {
		List<String> versioninfo = updateVersion.updateversion("1.0",
				"common.getLastAppVersion", "android_app", "1");
		return versioninfo;
	}

}
