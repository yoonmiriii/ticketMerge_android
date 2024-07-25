package com.hani.ticketmerge.model;

import java.util.List;

public class PostRes {
    private String result;
    private List<Post> items;
    private String accessToken;
    private List<Comments> comment; // 'comments'로 필드명 변경

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public List<Post> getItems() {
        return items;
    }

    public void setItems(List<Post> items) {
        this.items = items;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public List<Comments> getComments() { // getter 메서드 추가
        return comment;
    }

    public void setComments(List<Comments> comments) { // setter 메서드 추가
        this.comment = comments;
    }
}
