package com.hashedin.csvapi.config;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.util.TableUtils;
import com.hashedin.csvapi.models.Movie;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.io.*;
import java.util.Scanner;

@Configuration
@PropertySource("classpath:application-local.properties")
public class DynamoDbConfiguration {

    @Value("${aws.dynamodb.endpoint}")
    private String dynamoDbEndpoint;
    @Value("${aws.dynamodb.accessKey}")
    private String awsAccessKey;
    @Value("${aws.dynamodb.secretKey}")
    private String awsSecretKey;

    @Bean
    public DynamoDBMapper dynamoDBMapper() throws FileNotFoundException {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withEndpointConfiguration(
                        new AwsClientBuilder.EndpointConfiguration(dynamoDbEndpoint, "us-east-1"))
                .withCredentials(amazonDynamoDBCredentials()).build();
        DynamoDBMapper dynamoDBMapper = new DynamoDBMapper(client, DynamoDBMapperConfig.DEFAULT);
        init(dynamoDBMapper, client);

        return dynamoDBMapper;
    }

    @Bean
    public AWSCredentialsProvider amazonDynamoDBCredentials() {
        return new AWSStaticCredentialsProvider(new BasicAWSCredentials(awsAccessKey, awsSecretKey));
    }


    public void init(DynamoDBMapper dynamoDBMapper, AmazonDynamoDB client) {
        CreateTableRequest tableRequest = dynamoDBMapper.generateCreateTableRequest(Movie.class);
        tableRequest.setProvisionedThroughput(new ProvisionedThroughput(1L, 1L));
//        tableRequest.getGlobalSecondaryIndexes().get(0).setProvisionedThroughput(new ProvisionedThroughput(1L, 1l));

//        System.out.println(tableRequest.getGlobalSecondaryIndexes());
//        tableRequest.se
        if (TableUtils.createTableIfNotExists(client, tableRequest)) {
            System.out.println("Table created");
        }
    }}

//    @Bean
//    public DynamoDBMapper dynamoDBMapper(){
//        return new DynamoDBMapper(amazonDynamoDb());
//    }
//
//    private AmazonDynamoDB amazonDynamoDb() {
//        return AmazonDynamoDBClientBuilder.standard()
//                .withEndpointConfiguration(
//                        new AwsClientBuilder.EndpointConfiguration(dynamoDbEndpoint, "us-east-1"))
//                .withCredentials(amazonDynamoDBCredentials()).build();
//    }
//
//    private AWSCredentialsProvider amazonDynamoDBCredentials() {
//        return new AWSStaticCredentialsProvider(new BasicAWSCredentials(awsAccessKey, awsSecretKey));
//    }
//    public void init(DynamoDBMapper dynamoDBMapper, AmazonDynamoDB client) {
//
//        CreateTableRequest tableRequest = dynamoDBMapper.generateCreateTableRequest(Customer.class);
//        tableRequest.setProvisionedThroughput(new ProvisionedThroughput(1L, 1L));
//
//        if (TableUtils.createTableIfNotExists(client, tableRequest)) {
//            System.out.println("Table created");
//        }
//
//    }
