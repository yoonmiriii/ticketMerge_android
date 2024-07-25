package com.hani.ticketmerge.model;

import java.util.List;

public class MyLike {
    private String result;
    private List<GenreLike> genreLike;
    private List<Artist> artistLike;
    private List<Concert> concertLike;



    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public List<GenreLike> getGenreLike() {
        return genreLike;
    }

    public void setGenreLike(List<GenreLike> genreLike) {
        this.genreLike = genreLike;
    }

    public List<Artist> getArtistLike() {
        return artistLike;
    }

    public void setArtistLike(List<Artist> artistLike) {
        this.artistLike = artistLike;
    }

    public List<Concert> getConcertLike() {
        return concertLike;
    }

    public void setConcertLike(List<Concert> concertLike) {
        this.concertLike = concertLike;
    }
}