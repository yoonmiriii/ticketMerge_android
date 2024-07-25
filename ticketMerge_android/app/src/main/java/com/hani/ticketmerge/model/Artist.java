package com.hani.ticketmerge.model;

import java.io.Serializable;

public class Artist implements Serializable {
    private String name;
    private Integer id;
    private String url;
    private String genre;
    private String member;
    private Integer isLike;
    private String gender;


    // 기본 생성자
    public Artist() {}

    // 매개변수가 있는 생성자
    public Artist(String name, Integer id, String url, String genre, String member, Integer isLike, String gender) {
        this.name = name;
        this.id = id;
        this.url = url;
        this.genre = genre;
        this.member = member;
        this.isLike = isLike;
        this.gender = gender;

    }

    // Getter와 Setter 메서드
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getMember() {
        return member;
    }

    public void setMember(String member) {
        this.member = member;
    }

    public Integer getLike() {
        return isLike;
    }

    public void setLike(Integer isLike) {
        this.isLike = isLike;
    }

    public Integer getIsLike() {
        return isLike;
    }

    public void setIsLike(Integer isLike) {
        this.isLike = isLike;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }


}
