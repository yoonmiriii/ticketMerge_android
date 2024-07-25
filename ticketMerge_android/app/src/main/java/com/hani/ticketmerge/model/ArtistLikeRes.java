package com.hani.ticketmerge.model;
import java.util.List;

public class ArtistLikeRes {
    private String result;
    private List<ArtistLike> items;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public List<ArtistLike> getItems() {
        return items;
    }

    public void setItems(List<ArtistLike> items) {
        this.items = items;
    }
}
