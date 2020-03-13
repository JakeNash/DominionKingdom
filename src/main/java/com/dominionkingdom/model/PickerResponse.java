package com.dominionkingdom.model;

import lombok.Data;

import java.util.List;

@Data
public class PickerResponse {
    private final List<Card> cards;
    private final List<Card> cardLikes;

    public PickerResponse(List<Card> cards, List<Card> cardLikes) {
        this.cards = cards;
        this.cardLikes = cardLikes;
    }
}
