package com.schytd.discount.enties;

import java.io.Serializable;

public class PushInfo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id;
	private String title;// 消息标题
	private String titlePicUrl;// 消息标题图片
	private String abstractInfo;// 摘要信息
	private String content;// 详细内容
	private String createTime;// 创建时间
	private String carouselImgs;// 轮播图片,用逗号分开的url地址

	public PushInfo(String id, String title, String titlePicUrl,
			String abstractInfo, String content, String createTime,
			String carouselImgs) {
		this.id = id;
		this.title = title;
		this.titlePicUrl = titlePicUrl;
		this.abstractInfo = abstractInfo;
		this.content = content;
		this.createTime = createTime;
		this.carouselImgs = carouselImgs;
	}
	public PushInfo(){}
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitlePicUrl() {
		return titlePicUrl;
	}

	public void setTitlePicUrl(String titlePicUrl) {
		this.titlePicUrl = titlePicUrl;
	}

	public String getAbstractInfo() {
		return abstractInfo;
	}

	public void setAbstractInfo(String abstractInfo) {
		this.abstractInfo = abstractInfo;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getCarouselImgs() {
		return carouselImgs;
	}

	public void setCarouselImgs(String carouselImgs) {
		this.carouselImgs = carouselImgs;
	}

}
