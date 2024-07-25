package com.hani.ticketmerge.model;

import java.io.Serializable;
import java.util.List;

public class MyInfo implements Serializable {
    private List<User> items;

    public List<User> getItems() {
        return items;
    }

    public void setItems(List<User> items) {
        this.items = items;
    }

    public int getUserId() {
        if (items != null && items.size() > 0) {
            return items.get(0).id; // 여기서는 첫 번째 사용자의 ID를 가져옵니다.
        }
        return -1; // 예외 처리: 아이템이 없거나 비어 있을 경우
    }
}
