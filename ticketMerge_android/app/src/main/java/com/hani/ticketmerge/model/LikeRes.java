package com.hani.ticketmerge.model;

import com.hani.ticketmerge.model.ArtistLike;
import com.hani.ticketmerge.model.GenreLike;

import java.util.List;

public class LikeRes {
    private String result;
    private List<ArtistLike> artistLike;
    private List<GenreLike> genreLike; // 추가된 장르 좋아요 리스트

    // Getters and Setters
    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public List<ArtistLike> getArtistLike() {
        return artistLike;
    }

    public void setArtistLike(List<ArtistLike> artistLike) {
        this.artistLike = artistLike;
    }

    public List<GenreLike> getGenreLike() {
        return genreLike;
    }

    public void setGenreLike(List<GenreLike> genreLike) {
        this.genreLike = genreLike;
    }
}
