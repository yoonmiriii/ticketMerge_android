package com.hani.ticketmerge.model;

import java.io.Serializable;

public class Concert implements Serializable {

    private String title;
    private Integer id;

    private String thumbnailUrl;
    private String contentUrl;
    private String place;
    private String Location;
    private String startDate;
    private String endDate;
    private String castingList;
    private String url;
    private Integer viewCnt;
    private Integer isLike;
    private Integer likeCnt;





    public Concert(){

    }

    public Concert(String title, String thumbnailUrl, String contentUrl, String place, String startDate, String endDate, String castingList, String url) {
        this.title = title;
        this.thumbnailUrl = thumbnailUrl;
        this.contentUrl = contentUrl;
        this.place = place;
        this.startDate = startDate;
        this.endDate = endDate;
        this.castingList = castingList;
        this.url = url;
    }

    public Integer getIsLike() {
        return isLike;
    }


    public void setIsLike(Integer isLike) {
        this.isLike = isLike;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getContentUrl() {
        return contentUrl;
    }

    public void setContentUrl(String contentUrl) {
        this.contentUrl = contentUrl;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getCastingList() {
        return castingList;
    }

    public void setCastingList(String castingList) {
        this.castingList = castingList;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getViewCnt() {
        return viewCnt;
    }

    public void setViewCnt(Integer viewCnt) {
        this.viewCnt = viewCnt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getLike() {
        return isLike;
    }

    public void setLike(Integer like) {
        isLike = like;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public Integer getLikeCnt() {
        return likeCnt;
    }

    public void setLikeCnt(Integer likeCnt) {
        this.likeCnt = likeCnt;
    }
}
