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

import android.util.Log;

import com.schytd.discount.enties.ConstantData;
import com.schytd.discount.enties.IndexImage;
import com.schytd.discount.enties.SellerDetail;
import com.schytd.discount.enties.SellerInfo;
import com.schytd.discount.enties.SellerInfoItem;
import com.schytd.discount.net.SellerNet;
import com.schytd.discount.tools.NetTools;
import com.schytd.discount.tools.StrTools;

public class SellerNetImpl implements SellerNet {
	@Override
	public SellerInfo getSellerInfoFromServer(String... params)
			throws Exception {
		HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, 4000);
		HttpConnectionParams.setSoTimeout(httpParams, 6000);
		HttpClient client = new DefaultHttpClient(httpParams);
		HttpPost post = new HttpPost(ConstantData.URI);
		if (ConstantData.ISNOW && !ConstantData.ISALL) {
			params[8] = null;
		} else {
			params[0] = "0.0";
			params[1] = "0.0";
		}
		if (ConstantData.ISALL) {
			params[0] = "0.0";
			params[1] = "0.0";
		}
		// 封装数据
		List<NameValuePair> strParams = new ArrayList<NameValuePair>();
		strParams.add(new BasicNameValuePair("v", "1.0"));
		strParams.add(new BasicNameValuePair("method",
				"bus.getBusinessList4Page"));
		strParams.add(new BasicNameValuePair("appKey", "android_app"));
		strParams.add(new BasicNameValuePair("lat", params[0]));
		strParams.add(new BasicNameValuePair("lng", params[1]));
		strParams.add(new BasicNameValuePair("raidus", params[2]));
		strParams.add(new BasicNameValuePair("businessName", params[3]));
		strParams.add(new BasicNameValuePair("pageSize", params[4]));
		strParams.add(new BasicNameValuePair("currentPage", params[5]));
		strParams.add(new BasicNameValuePair("propertyTypeId", params[6]));// 行业分类
		strParams.add(new BasicNameValuePair("industryId", params[7]));// 商家大分类
		if (!StrTools.isNull(params[8])) {
			strParams.add(new BasicNameValuePair("district", params[8]));
		}
		if (!StrTools.isNull(params[9])) {
			strParams.add(new BasicNameValuePair("sessionId", params[9]));
		}
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
		Log.d("+++++", code);
		if (!code.equals("0")) {
			return null;
		}
		// 解析json
		SellerInfo sellerInfo = new SellerInfo();
		int totalCount = resultsJSON.getInt("totalCount");
		if (totalCount < 1) {
			return null;
		}
		ArrayList<SellerInfoItem> itemList = new ArrayList<SellerInfoItem>();
		if (!resultsJSON.has("resultList")) {
			return null;
		}
		JSONArray resultList = resultsJSON.getJSONArray("resultList");
		for (int i = 0; i < resultList.length(); i++) {
			JSONObject item = resultList.getJSONObject(i);
			SellerInfoItem infoItem = new SellerInfoItem();
			infoItem.setId(item.getString("id"));
			infoItem.setWeight(item.getString("weight"));
			infoItem.setStatus(item.getString("status"));
			infoItem.setBusinessName(item.getString("businessName"));
			infoItem.setIssigned(item.getString("issigned"));
			infoItem.setAddress(item.getString("address"));
			infoItem.setContactPhoneNum(item.getString("contactPhoneNum"));
			infoItem.setLng(item.getString("lng"));
			infoItem.setLat(item.getString("lat"));
			infoItem.setBusinessDesc(item.getString("businessDesc"));
			infoItem.setLogoPic(item.getString("logoPic"));// 店招图片
			infoItem.setDiscount(item.getString("discount"));
			itemList.add(infoItem);
		}
		sellerInfo.setTotalCount(totalCount);
		sellerInfo.setResultList(itemList);
		return sellerInfo;
	}

	@Override
	public SellerDetail getSellerDetailInfoFromServer(String... params)
			throws Exception {
		HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, 3000);
		HttpConnectionParams.setSoTimeout(httpParams, 3000);
		HttpClient client = new DefaultHttpClient(httpParams);
		HttpPost post = new HttpPost(ConstantData.URI);
		// 封装数据
		List<NameValuePair> strParams = new ArrayList<NameValuePair>();
		strParams.add(new BasicNameValuePair("v", "1.0"));
		strParams
				.add(new BasicNameValuePair("method", "bus.getBusinessDetails"));
		strParams.add(new BasicNameValuePair("appKey", "android_app"));
		strParams.add(new BasicNameValuePair("baseInfoId", params[0]));
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
		SellerDetail sellerDetail = new SellerDetail();
		String id = resultsJSON.getString("id");
		if (StrTools.isNull(id)) {
			sellerDetail.setId(id);
		}
		String carouselImgs = resultsJSON.has("carouselImgs") ? resultsJSON
				.getString("carouselImgs") : null;
		sellerDetail.setCarouselImgs(carouselImgs);
		String baseInfoId = resultsJSON.has("baseInfoId") ? resultsJSON
				.getString("baseInfoId") : null;
		sellerDetail.setBaseInfoId(baseInfoId);
		String detailsDesc = resultsJSON.has("detailsDesc") ? resultsJSON
				.getString("detailsDesc") : null;
		sellerDetail.setDetailsDesc(detailsDesc);
		String feature = resultsJSON.has("feature") ? resultsJSON
				.getString("feature") : null;
		sellerDetail.setFeature(feature);
		String environment = resultsJSON.has("environment") ? resultsJSON
				.getString("environment") : null;
		sellerDetail.setEnvironment(environment);
		String tips = resultsJSON.has("tips") ? resultsJSON.getString("tips")
				: null;
		sellerDetail.setTips(tips);
		String address = resultsJSON.has("address") ? resultsJSON
				.getString("address") : null;
		sellerDetail.setAddress(address);
		String contactPhoneNum = resultsJSON.has("contactPhoneNum") ? resultsJSON
				.getString("contactPhoneNum") : null;
		sellerDetail.setContactPhoneNum(contactPhoneNum);
		return sellerDetail;
	}

	// 向服务器获得首页轮播图片的路径
	@Override
	public ArrayList<IndexImage> getIndexImageFromServer() throws Exception {
		HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, 3000);
		HttpConnectionParams.setSoTimeout(httpParams, 3000);
		HttpClient client = new DefaultHttpClient(httpParams);
		HttpPost post = new HttpPost(ConstantData.URI);
		// 封装数据
		List<NameValuePair> strParams = new ArrayList<NameValuePair>();
		strParams.add(new BasicNameValuePair("v", "1.0"));
		strParams.add(new BasicNameValuePair("method",
				"common.getManPageMsgInfoList"));
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
		Log.d("轮播图片", code + "---" + resultsJSON);
		ArrayList<IndexImage> resultList = new ArrayList<IndexImage>();
		if (resultsJSON.has("resultList")) {
			JSONArray resultArray = resultsJSON.getJSONArray("resultList");
			for (int i = 0; i < resultArray.length(); i++) {
				IndexImage indexImage = new IndexImage();
				JSONObject item = resultArray.getJSONObject(i);
				indexImage.setId(item.getString("id"));
				indexImage.setUrl(item.getString("titlepicurl"));
				resultList.add(indexImage);
			}
		}
		return resultList;
	}

	// 优惠资讯
	@Override
	public SellerInfo getDiscountInfoFormServer(String... params)
			throws Exception {
		HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, 3000);
		HttpConnectionParams.setSoTimeout(httpParams, 3000);
		HttpClient client = new DefaultHttpClient(httpParams);
		HttpPost post = new HttpPost(ConstantData.URI);
		// 封装数据
		List<NameValuePair> strParams = new ArrayList<NameValuePair>();
		strParams.add(new BasicNameValuePair("v", "1.0"));
		strParams.add(new BasicNameValuePair("method",
				"common.geNearbydiscountlist4Page"));
		strParams.add(new BasicNameValuePair("appKey", "android_app"));
		strParams.add(new BasicNameValuePair("lat", params[0]));
		strParams.add(new BasicNameValuePair("lng", params[1]));
		strParams.add(new BasicNameValuePair("raidus", params[2]));
		strParams.add(new BasicNameValuePair("pageSize", params[3]));
		strParams.add(new BasicNameValuePair("currentPage", params[4]));
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
		SellerInfo sellerInfo = new SellerInfo();
		int totalCount = resultsJSON.getInt("totalCount");
		if (totalCount < 1) {
			return null;
		}
		ArrayList<SellerInfoItem> itemList = new ArrayList<SellerInfoItem>();
		JSONArray resultList = resultsJSON.getJSONArray("resultList");
		for (int i = 0; i < resultList.length(); i++) {
			JSONObject item = resultList.getJSONObject(i);
			SellerInfoItem infoItem = new SellerInfoItem();
			infoItem.setId(item.getString("id"));
			infoItem.setWeight(item.getString("weight"));
			infoItem.setStatus(item.getString("status"));
			infoItem.setBusinessName(item.getString("businessName"));
			infoItem.setIssigned(item.getString("issigned"));
			infoItem.setAddress(item.getString("address"));
			infoItem.setContactPhoneNum(item.getString("contactPhoneNum"));
			infoItem.setLng(item.getString("lng"));
			infoItem.setLat(item.getString("lat"));
			infoItem.setBusinessDesc(item.getString("businessDesc"));
			infoItem.setLogoPic(item.getString("logoPic"));// 店招图片
			infoItem.setDiscount(item.getString("discount"));
			itemList.add(infoItem);
		}
		sellerInfo.setTotalCount(totalCount);
		sellerInfo.setResultList(itemList);
		return sellerInfo;
	}

}
