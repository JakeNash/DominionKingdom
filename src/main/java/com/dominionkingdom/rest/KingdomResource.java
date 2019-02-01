package com.dominionkingdom.rest;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.Table;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class KingdomResource {

    @GetMapping(path = "/box")
    public String getBoxContents(@RequestParam String box) {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.defaultClient();
        DynamoDB dynamoDB = new DynamoDB(client);
        Table cardTable = dynamoDB.getTable("DominionCards");

        ItemCollection<com.amazonaws.services.dynamodbv2.document.QueryOutcome> items = cardTable.query("Box", box);
        StringBuilder output = new StringBuilder();
        items.forEach(output::append);
        return output.toString();
    }

}