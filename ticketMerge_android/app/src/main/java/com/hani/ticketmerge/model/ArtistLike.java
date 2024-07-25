package com.hani.ticketmerge.model;

public class ArtistLike {
    private int id;
    private String name;
    private String url;
    private String gender;
    private String member;
    private String genre;
    private int isLike;


    public ArtistLike(int id, String name, String url, String gender, String member, String genre, int isLike) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.gender = gender;
        this.member = member;
        this.genre = genre;
        this.isLike = isLike;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public String getGender() {
        return gender;
    }

    public String getMember() {
        return member;
    }

    public String getGenre() {
        return genre;
    }

    public int getIsLike() {
        return isLike;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setMember(String member) {
        this.member = member;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setIsLike(int isLike) {
        this.isLike = isLike;
    }
}