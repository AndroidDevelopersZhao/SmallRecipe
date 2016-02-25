package com.cn.smallrecipe.datainfo.search;

/**
 * Created by Administrator on 2016/2/24.
 */
public class Item {
    String title = null;
    String url=null;
    String ingredients=null;
    String burden=null;

//    public Item(String title, String url, String ingredients, String burden) {
//        this.title = title;
//        this.url = url;
//        this.ingredients = ingredients;
//        this.burden = burden;
//    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public String getBurden() {
        return burden;
    }

    public void setBurden(String burden) {
        this.burden = burden;
    }
}
