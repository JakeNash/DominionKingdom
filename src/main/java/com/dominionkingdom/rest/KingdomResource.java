package com.dominionkingdom.rest;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.management.Query;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@RestController
public class KingdomResource {

    private String CARD_TABLE = "DominionCards";
    private AmazonDynamoDB client = AmazonDynamoDBClientBuilder.defaultClient();
    private DynamoDB dynamoDB = new DynamoDB(client);
    private Table cardTable = dynamoDB.getTable(CARD_TABLE);
    
    @GetMapping(path = "/boxes")
    public String getBoxContents(@RequestParam String[] boxes) {
        StringBuilder output = new StringBuilder();
        for (String box : boxes) {
            QuerySpec spec = new QuerySpec()
                    .withKeyConditionExpression("Box = :v_box")
                    .withValueMap(new ValueMap()
                        .withString(":v_box", box));

            ItemCollection<QueryOutcome> items = cardTable.query(spec);
            items.forEach(output::append);
        }
        return output.toString();
    }

    @GetMapping(path = "/pick")
    public String getPick(@RequestParam String[] boxes, @RequestParam(required = false) String[] others) {
        List<String> allCards = new ArrayList<>();
        for (String box : boxes) {
            QuerySpec spec = new QuerySpec().withHashKey("Box", box);
            ItemCollection<QueryOutcome> items = cardTable.query(spec);
            for (Item item : items) {
                if (item.get("Pickable").equals("Card")) {
                    allCards.add((String) item.get("Name"));
                }
            }
        }
        List<String> allCardLikes = new ArrayList<>();
        if (others != null) {
            for (String other : others) {
                QuerySpec spec = new QuerySpec().withHashKey("Box", other);
                ItemCollection<QueryOutcome> items = cardTable.query(spec);
                for (Item item : items) {
                    if (item.get("Pickable").equals("Event") || item.get("Pickable").equals("Landmark") || item.get("Pickable").equals("Project")) {
                        allCardLikes.add((String) item.get("Name"));
                    }
                }
            }
        }

        StringBuilder output = new StringBuilder();
        output.append("{ \"Cards\": [");

        Collections.shuffle(allCards);
        List<String> cards = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            cards.add("\"" + allCards.get(i) + "\"");
        }
        output.append(StringUtils.collectionToCommaDelimitedString(cards));

        if (!allCardLikes.isEmpty()) {
            Collections.shuffle(allCardLikes);
            List<String> cardLikes = new ArrayList<>();
            Random random = new Random();
            int numCardLikes = random.nextInt(3);
            for (int i = 0; i < numCardLikes; i++) {
                cardLikes.add("\"" + allCardLikes.get(i) + "\"");
            }
            if (!cardLikes.isEmpty()) {
                output.append("], \"Card-Likes\": [");
                output.append(StringUtils.collectionToCommaDelimitedString(cardLikes));
                output.append("]}");
            } else {
                output.append("]}");
            }
        } else {
            output.append("]}");
        }

        return output.toString();
    }
}