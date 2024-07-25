package com.hani.ticketmerge.model;
import com.google.gson.annotations.SerializedName;


import java.io.Serializable;
import java.util.List;

public class ConcertDetailRes implements Serializable {


    private String result;


    private Concert concert;
//    @SerializedName("concert")
//    private List<Concert> concertList;

    @SerializedName("artist")
    private List<Artist> artistList;

    // Getter 메서드
    public String getResult() {
        return result;
    }

    public Concert getConcert() {
        return concert;
    }
//    public List<Concert> getConcertList() {
//        return concertList;
//    }
    public List<Artist> getArtistList() {
        return artistList;
    }
}
