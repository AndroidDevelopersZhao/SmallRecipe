package com.cn.smallrecipe.datainfo.sendsayinfo;

import java.util.Date;

public class Data_Say_Result {
	public Data_Say_Result(){
		
	}
	private String city=null;
	private String say_id=null;//说说ID
	private String sendsay_usernumber = null;//发表说说的用户账号
	private String say_time = null;//发表说说的时间
	private String say_text = null;//说说文字内容
	private String say_image_url = null;//说说中包含的图片的url
	private String say_com_id = null;//说说对应的收藏的菜谱ID
	private String say_com_name = null;//说说对应的收藏的菜谱名称
	private String say_like_num = null;//点赞数量
	private String say_comment = null;//对该说说评论的所有用户账号、评论内容、评论时间 存取格式为账号ˇ内容ˇ时间┃
	private String user_name=null;
	private String user_img=null;
	
	

	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public Data_Say_Result(String sayId, String sendsayUsernumber,
			String sayTime, String sayText, String sayImageUrl,
			String sayComId, String sayComName, String sayLikeNum,
			String sayComment, String userName, String userImg,String city) {
		super();
		say_id = sayId;
		sendsay_usernumber = sendsayUsernumber;
		say_time = sayTime;
		say_text = sayText;
		say_image_url = sayImageUrl;
		say_com_id = sayComId;
		say_com_name = sayComName;
		say_like_num = sayLikeNum;
		say_comment = sayComment;
		user_name = userName;
		user_img = userImg;
		this.city=city;
	}
	public String getUser_name() {
		return user_name;
	}
	public void setUser_name(String userName) {
		user_name = userName;
	}
	public String getUser_img() {
		return user_img;
	}
	public void setUser_img(String userImg) {
		user_img = userImg;
	}
	public String getSay_id() {
		return say_id;
	}
	public void setSay_id(String sayId) {
		say_id = sayId;
	}
	public String getSendsay_usernumber() {
		return sendsay_usernumber;
	}
	public void setSendsay_usernumber(String sendsayUsernumber) {
		sendsay_usernumber = sendsayUsernumber;
	}
	public String getSay_time() {
		return say_time;
	}
	public void setSay_time(String sayTime) {
		say_time = sayTime;
	}
	public String getSay_text() {
		return say_text;
	}
	public void setSay_text(String sayText) {
		say_text = sayText;
	}
	public String getSay_image_url() {
		return say_image_url;
	}
	public void setSay_image_url(String sayImageUrl) {
		say_image_url = sayImageUrl;
	}
	public String getSay_com_id() {
		return say_com_id;
	}
	public void setSay_com_id(String sayComId) {
		say_com_id = sayComId;
	}
	public String getSay_com_name() {
		return say_com_name;
	}
	public void setSay_com_name(String sayComName) {
		say_com_name = sayComName;
	}
	public String getSay_like_num() {
		return say_like_num;
	}
	public void setSay_like_num(String sayLikeNum) {
		say_like_num = sayLikeNum;
	}
	public String getSay_comment() {
		return say_comment;
	}
	public void setSay_comment(String sayComment) {
		say_comment = sayComment;
	}
	
	
}
