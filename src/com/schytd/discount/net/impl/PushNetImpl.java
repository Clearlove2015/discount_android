package com.schytd.discount.net.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.schytd.discount.enties.ConstantData;
import com.schytd.discount.enties.PushInfo;
import com.schytd.discount.enties.PushItem;
import com.schytd.discount.net.PushNet;
import com.schytd.discount.tools.NetTools;

public class PushNetImpl implements PushNet {
	@Override
	public List<PushItem> getPushInfoOfDay() throws Exception {
		HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, 3000);
		HttpConnectionParams.setSoTimeout(httpParams, 3000);
		HttpClient client = new DefaultHttpClient(httpParams);
		HttpPost post = new HttpPost(ConstantData.URI);
		// 封装数据
		List<NameValuePair> strParams = new ArrayList<NameValuePair>();
		strParams.add(new BasicNameValuePair("v", "1.0"));
		strParams.add(new BasicNameValuePair("method",
				"common.getTodayMsgInfoList"));
		strParams.add(new BasicNameValuePair("appKey", "android_app"));
		// 生成签名
		String sign = NetTools.sign(strParams, ConstantData.SECRET);
		strParams.add(new BasicNameValuePair("sign", sign));
		post.setEntity(new UrlEncodedFormEntity(strParams, HTTP.UTF_8));
		HttpResponse response = client.execute(post);
		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode != HttpStatus.SC_OK) {
			throw new RuntimeException("服务器忙！");
		}
		String results = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);
		// 解析返回值
		JSONObject resultsJSON = new JSONObject(results);
		String code = resultsJSON.getString("code");
		if (!code.equals("0")) {
			return null;
		}
		// 解析json
		int totalCount = resultsJSON.getInt("totalCount");
		if (totalCount < 1) {
			return null;
		}
		ArrayList<PushItem> itemList = new ArrayList<PushItem>();
		JSONArray resultList = resultsJSON.getJSONArray("resultList");
		for (int i = 0; i < resultList.length(); i++) {
			JSONObject item = resultList.getJSONObject(i);
			PushItem pushItem = new PushItem();
			pushItem.setId(item.getString("id"));
			pushItem.setTitle(item.getString("title"));
			pushItem.setDate(item.getString("createTime"));
			pushItem.setImgs(item.getString("titlePicUrl"));
			pushItem.setType("1");
			itemList.add(pushItem);
		}
		return itemList;

	}

	@Override
	public PushInfo getPushDetail(String... params) throws Exception {
		HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, 3000);
		HttpConnectionParams.setSoTimeout(httpParams, 3000);
		HttpClient client = new DefaultHttpClient(httpParams);
		HttpPost post = new HttpPost(ConstantData.URI);
		// 封装数据
		List<NameValuePair> strParams = new ArrayList<NameValuePair>();
		strParams.add(new BasicNameValuePair("v", "1.0"));
		strParams.add(new BasicNameValuePair("method", "common.getMsgDetails"));
		strParams.add(new BasicNameValuePair("appKey", "android_app"));
		strParams.add(new BasicNameValuePair("id", params[0]));
		strParams.add(new BasicNameValuePair("msgType", params[1]));
		// 生成签名
		String sign = NetTools.sign(strParams, ConstantData.SECRET);
		strParams.add(new BasicNameValuePair("sign", sign));
		post.setEntity(new UrlEncodedFormEntity(strParams, HTTP.UTF_8));
		HttpResponse response = client.execute(post);
		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode != HttpStatus.SC_OK) {
			throw new RuntimeException("服务器忙！");
		}
		String results = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);
		// 解析返回值
		JSONObject resultsJSON = new JSONObject(results);
		String code = resultsJSON.getString("code");
		if (!code.equals("0")) {
			return null;
		}
		//{"code":"0","id":3590,"title":"开业大吉","titlePicUrl":"http://192.168.1.113/webfs/discountserver/ad9d6c667fc768acd5efd3d2e55ff8b0.jpg","content":"开业大吉开业大吉开业大吉开业大吉开业大吉开业大吉开业大吉开业大吉开业大吉开业大吉开业大吉开业大吉开业大吉开业大吉开业大吉开业大吉开业大吉开业大吉开业大吉开业大吉开业大吉开业大吉开业大吉开业大吉开业大吉开业大吉开业大吉开业大吉开业大吉开业大吉开业大吉开业大吉开业大吉开业大吉开业大吉开业大吉开业大吉开业大吉开业大吉开业大吉开业大吉开业大吉开业大吉开业大吉开业大吉开业大吉开业大吉开业大吉开业大吉开业大吉。开业大吉开业大吉。开业大吉。开业大吉开业大吉开业大吉。","createTime":1442569736000,"carouselImgs":"http://192.168.1.113/webfs/discountserver/69e627fd2511085e3c32507570750f27.jpg"}
		PushInfo pushInfo = new PushInfo();
		String id = resultsJSON.has("id") ? resultsJSON
				.getString("id") : null;
		pushInfo.setId(id);
		String title = resultsJSON.has("title") ? resultsJSON
				.getString("title") : "";
		pushInfo.setTitle(title);
		String titlePicUrl = resultsJSON.has("titlePicUrl") ? resultsJSON
				.getString("titlePicUrl") : null;
		pushInfo.setTitlePicUrl(titlePicUrl);
		String abstractInfo = resultsJSON.has("abstractInfo") ? resultsJSON
				.getString("abstractInfo") : "";
		pushInfo.setAbstractInfo(abstractInfo);
		String content = resultsJSON.has("content") ? resultsJSON
				.getString("content") : null;
		pushInfo.setContent(content);
		String createTime = resultsJSON.has("createTime") ? resultsJSON
				.getString("createTime") : null;
		pushInfo.setCreateTime(createTime);
		String carouselImgs = resultsJSON.has("carouselImgs") ? resultsJSON
				.getString("carouselImgs") : null;
		pushInfo.setCarouselImgs(carouselImgs);
		return pushInfo;
	}
}
