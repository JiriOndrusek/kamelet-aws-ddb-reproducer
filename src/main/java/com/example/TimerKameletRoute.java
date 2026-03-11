package com.example;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.aws2.ddb.Ddb2Constants;
import org.apache.camel.component.aws2.ddb.Ddb2Operations;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TimerKameletRoute extends RouteBuilder {

    private final String tableName = "cq-kamelet-test-" + UUID.randomUUID().toString().substring(0, 8);

    @Override
    public void configure() throws Exception {

        // Route: Timer that creates a single DynamoDB entry using aws-ddb-sink kamelet
        from("timer:ddb-insert?repeatCount=1")
            .routeId("timer-to-aws-ddb-kamelet-route")
            .log("Inserting entry into DynamoDB table: " + tableName)
            .process(exchange -> {
                // Create a Map for DynamoDB item (required for PutItem operation)
                Map<String, AttributeValue> item = new HashMap<>();
                item.put("id", AttributeValue.builder().s("1").build());
                item.put("message", AttributeValue.builder().s("Hello DynamoDBfMBYU").build());

                exchange.getIn().setHeader(Ddb2Constants.OPERATION, Ddb2Operations.PutItem);
                exchange.getIn().setHeader(Ddb2Constants.ITEM, item);
            })
            .log("Item to insert: ${body}")
            .toD("kamelet:aws-ddb-sink"
                + "?table=" + tableName
                + "&region={{aws.region}}"
                + "&accessKey={{aws.accessKey}}"
                + "&secretKey={{aws.secretKey}}"
                + "&operation=PutItem")
            .log("Successfully inserted entry into DynamoDB table: " + tableName);
    }
}
