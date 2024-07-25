package com.hani.ticketmerge.model;

import java.util.List;

public class ConcertRes {
    private String result;
    private List<Concert> items;
    private List<ArtistLike> artist;
    public String comment;

    public String getResult() {
        return result;
    }

    public List<Concert> getItems() {
        return items;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public void setItems(List<Concert> items) {
        this.items = items;
    }

    public List<ArtistLike> getArtist() {
        return artist;
    }

    public void setArtist(List<ArtistLike> artist) {
        this.artist = artist;
    }
}