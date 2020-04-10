package com.dominionkingdom.rest;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.dominionkingdom.model.Card;
import com.dominionkingdom.model.PickerResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    private ObjectMapper objectMapper = new ObjectMapper();
    
    @GetMapping(path = "/boxes")
    public String getBoxContents(@RequestParam String[] boxes) {
        StringBuilder output = new StringBuilder();
        for (String box : boxes) {
            QuerySpec spec = new QuerySpec()
                    .withKeyConditionExpression("box = :v_box")
                    .withValueMap(new ValueMap()
                        .withString(":v_box", box));

            ItemCollection<QueryOutcome> items = cardTable.query(spec);
            items.forEach(output::append);
        }
        return output.toString();
    }

    @GetMapping(path = "/pick")
    public ResponseEntity<String> getPick(@RequestParam String[] boxes, @RequestParam(required = false) String[] others) {
        List<Card> allCards = new ArrayList<>();
        for (String box : boxes) {
            QuerySpec spec = new QuerySpec().withHashKey("box", box);
            ItemCollection<QueryOutcome> items = cardTable.query(spec);
            for (Item item : items) {
                if (item.get("pickable").equals("Card")) {
                    String json = item.toJSON();
                    Card card;
                    try {
                        card = objectMapper.readValue(json, Card.class);
                    } catch (Exception e) {
                        return ResponseEntity.status(HttpStatus.FAILED_DEPENDENCY).build();
                    }
                    if (null != card) {
                        allCards.add(card);
                    } else {
                        return ResponseEntity.status(HttpStatus.FAILED_DEPENDENCY).build();
                    }
                }
            }
        }
        List<Card> allCardLikes = new ArrayList<>();
        if (others != null) {
            for (String other : others) {
                QuerySpec spec = new QuerySpec().withHashKey("box", other);
                ItemCollection<QueryOutcome> items = cardTable.query(spec);
                for (Item item : items) {
                    if (item.get("pickable").equals("Event") || item.get("pickable").equals("Landmark") || item.get("pickable").equals("Project")
                        || item.get("pickable").equals("Way")) {
                        String json = item.toJSON();
                        Card cardLike;
                        try {
                            cardLike = objectMapper.readValue(json, Card.class);
                        } catch (Exception e) {
                            return ResponseEntity.status(HttpStatus.FAILED_DEPENDENCY).build();
                        }
                        if (null != cardLike) {
                            allCardLikes.add(cardLike);
                        } else {
                            return ResponseEntity.status(HttpStatus.FAILED_DEPENDENCY).build();
                        }
                    }
                }
            }
        }

        Collections.shuffle(allCards);
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            cards.add(allCards.get(i));
        }

        List<Card> cardLikes = new ArrayList<>();
        if (!allCardLikes.isEmpty()) {
            Collections.shuffle(allCardLikes);
            cardLikes = new ArrayList<>();
            Random random = new Random();
            int numCardLikes = random.nextInt(3);
            for (int i = 0; i < numCardLikes; i++) {
                cardLikes.add(allCardLikes.get(i));
            }
        }

        PickerResponse pickerResponse = new PickerResponse(cards, cardLikes);
        String output;
        try {
            output = objectMapper.writeValueAsString(pickerResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FAILED_DEPENDENCY).build();
        }

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Access-Control-Allow-Origin", "*");
        return new ResponseEntity<>(output, responseHeaders, HttpStatus.OK);
    }
}