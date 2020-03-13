package com.dominionkingdom.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Card {
    private String box;
    private String name;
    private String cardType;
    private String cost;
    private String pickable;
    private String setup;
}
