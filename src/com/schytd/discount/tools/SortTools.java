package com.schytd.discount.tools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.schytd.discount.enties.SellerInfoItem;

public class SortTools {
	// 折扣排序
	public static List<SellerInfoItem> discountSort(final double lat,
			final double lng, List<SellerInfoItem> list) {
		Collections.sort(list, new Comparator<SellerInfoItem>() {
			@Override
			public int compare(SellerInfoItem o1, SellerInfoItem o2) {
				if (Double.parseDouble(o1.getDiscount()) > Double
						.parseDouble(o2.getDiscount())) {
					return 1;
				} else if (Double.parseDouble(o1.getDiscount()) == Double
						.parseDouble(o2.getDiscount())) {
					// 折扣相等看距离
					LatLng locationPoint = new LatLng(lat, lng);
					LatLng Seller1Point = new LatLng(Double.parseDouble(o1
							.getLat()), Double.parseDouble(o1.getLng()));
					LatLng Seller2Point = new LatLng(Double.parseDouble(o2
							.getLat()), Double.parseDouble(o2.getLng()));
					double distance1 = DistanceUtil.getDistance(locationPoint,
							Seller1Point);
					double distance2 = DistanceUtil.getDistance(locationPoint,
							Seller2Point);
					if (distance1 > distance2) {
						return 1;
					} else {
						return -1;
					}
				} else {
					return -1;
				}
			}
		});
		return list;
	}

	// 距离排序
	public static ArrayList<SellerInfoItem> distanceSort(final double lat,
			final double lng, ArrayList<SellerInfoItem> list) {
		Collections.sort(list, new Comparator<SellerInfoItem>() {
			@Override
			public int compare(SellerInfoItem sell1, SellerInfoItem sell2) {
				LatLng locationPoint = new LatLng(lat, lng);
				LatLng Seller1Point = new LatLng(Double.parseDouble(sell1
						.getLat()), Double.parseDouble(sell1.getLng()));
				LatLng Seller2Point = new LatLng(Double.parseDouble(sell2
						.getLat()), Double.parseDouble(sell2.getLng()));
				double distance1 = DistanceUtil.getDistance(locationPoint,
						Seller1Point);
				double distance2 = DistanceUtil.getDistance(locationPoint,
						Seller2Point);
				if (distance1 > distance2) {
					return 1;
				} else if (distance1 == distance2) {
					return 0;
				} else {
					return -1;
				}
			}
		});
		return list;
	}

	// 返回商家信息的距离集合
	public static ArrayList<String> getDistance(List<SellerInfoItem> items,
			double lat, double lng) {
		LatLng locationPoint = new LatLng(lat, lng);
		ArrayList<String> distances = new ArrayList<String>();
		for (SellerInfoItem sellerInfoItem : items) {
			LatLng point = new LatLng(Double.parseDouble(sellerInfoItem
					.getLat()), Double.parseDouble(sellerInfoItem.getLng()));
			distances.add(String.valueOf(DistanceUtil.getDistance(
					locationPoint, point)));
		}
		return distances;
	}

}
