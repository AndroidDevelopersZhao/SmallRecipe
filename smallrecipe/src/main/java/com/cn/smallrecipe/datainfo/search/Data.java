package com.cn.smallrecipe.datainfo.search;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Administrator on 2016/2/19.
 */
public class Data implements Serializable {
    private String id = null;
    private String title = null;
    private String tags = null;
    private String imtro = null;
    private String ingredients = null;
    private String burden = null;
    private ArrayList<String> albums = null;
    private ArrayList<Steps> steps = null;

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

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getImtro() {
        return imtro;
    }

    public void setImtro(String imtro) {
        this.imtro = imtro;
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

    public ArrayList<String> getAlbums() {
        return albums;
    }

    public void setAlbums(ArrayList<String> albums) {
        this.albums = albums;
    }

    public ArrayList<Steps> getSteps() {
        return steps;
    }

    public void setSteps(ArrayList<Steps> steps) {
        this.steps = steps;
    }
}
