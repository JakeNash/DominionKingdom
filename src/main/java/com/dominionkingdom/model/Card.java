package com.dominionkingdom.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "DominionCards")
public class Card {
    private String box;
    private String name;
    private String cardType;
    private String cost;
    private String pickable;
    private String setup;

    @DynamoDBHashKey
    public String getBox() {
        return box;
    }

    @DynamoDBAttribute
    public String getName() {
        return name;
    }

    @DynamoDBAttribute
    public String getCardType() {
        return cardType;
    }

    @DynamoDBAttribute
    public String getCost() {
        return cost;
    }

    @DynamoDBAttribute
    public String getPickable() {
        return pickable;
    }

    @DynamoDBAttribute
    public String getSetup() {
        return setup;
    }
}
