package com.dominionkingdom.rest;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class KingdomResource {

    private String CARD_TABLE = "DominionCards";
    private AmazonDynamoDB client = AmazonDynamoDBClientBuilder.defaultClient();
    private DynamoDB dynamoDB = new DynamoDB(client);
    private Table cardTable = dynamoDB.getTable(CARD_TABLE);
    
    @GetMapping(path = "/boxes")
    public String getMultipleBoxContents(@RequestParam String[] box) {
        StringBuilder output = new StringBuilder();
        for (String curr : box) {
            QuerySpec spec = new QuerySpec()
                    .withKeyConditionExpression("Box = :v_box")
                    .withValueMap(new ValueMap()
                        .withString(":v_box", curr));

            ItemCollection<QueryOutcome> items = cardTable.query(spec);
            items.forEach(output::append);
        }
        return output.toString();
    }

}