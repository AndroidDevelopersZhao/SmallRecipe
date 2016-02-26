package com.cn.smallrecipe.datainfo.register;

public class RespData {
	private String sessionId = null;
	private String reLoginId = null;
	private String username = null;
	private String userid = null;
	private String userlogo = null;
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getUserlogo() {
		return userlogo;
	}
	public void setUserlogo(String userlogo) {
		this.userlogo = userlogo;
	}
	public String getReLoginId() {
		return reLoginId;
	}
	public void setReLoginId(String reLoginId) {
		this.reLoginId = reLoginId;
	}
	public RespData(String sessionId,String reLoginId,String username,String userid,String userlogo) {
		// TODO Auto-generated constructor stub
		this.sessionId=sessionId;
		this.reLoginId=reLoginId;
		this.username=username;
		this.userid=userid;
		this.userlogo=userlogo;
		
	}
	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	
}
