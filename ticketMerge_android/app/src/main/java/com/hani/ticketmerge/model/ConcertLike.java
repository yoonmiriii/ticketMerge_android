package com.hani.ticketmerge.model;

import java.util.List;

public class ConcertLike {
    private String result;
    private List<Concert> items;

    private List<Concert> concertLike;
    private List<Artist> artistLike;

    public ConcertLike(String result, List<Concert> items) {
        this.result = result;
        this.items = items;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public List<Concert> getItems() {
        return items;
    }

    public void setItems(List<Concert> items) {
        this.items = items;
    }

    public List<Concert> getConcertLike() {
        return concertLike;
    }

    public void setConcertLike(List<Concert> concertLike) {
        this.concertLike = concertLike;
    }

    public List<Artist> getArtistLike() {
        return artistLike;
    }

    public void setArtistLike(List<Artist> artistLike) {
        this.artistLike = artistLike;
    }
}
