package com.cn.smallrecipe.datainfo.mystarinfo;

import java.io.Serializable;

public class Data_GetUserStarRecipe implements Serializable{
	private int respCode = -1;
	private Data_DataNum [] ids=null;
	public int getRespCode() {
		return respCode;
	}
	public void setRespCode(int respCode) {
		this.respCode = respCode;
	}
	public Data_DataNum[] getIds() {
		return ids;
	}
	public void setIds(Data_DataNum[] ids) {
		this.ids = ids;
	}
	
}
