package com.hani.ticketmerge.model;

public class CommentRequest {
    private String comment;

    public CommentRequest(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
