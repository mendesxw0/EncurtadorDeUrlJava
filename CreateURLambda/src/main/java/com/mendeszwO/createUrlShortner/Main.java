package com.mendeszwO.createUrlShortner;


import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectAclRequest;
import sun.misc.Unsafe;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Main implements RequestHandler<Map<String, Object>, Map<String, String>> {

    private final ObjectMapper objectMapper = new ObjectMapper();


    @Override
    public Map<String, String> handleRequest(Map<String, Object> input, Context context) {
        String body =  input.get("body").toString();


        Map<String, String> bodyMap;
        try {
            bodyMap = objectMapper.readValue(body, Map.class);
        } catch (JsonProcessingException exception) {

            throw new RuntimeException("Error parsing JSON body:"+ exception.getMessage(), exception);

        }

        String originalUrl = bodyMap.get("originalUrl");
        String expirationTime = bodyMap.get("expirationTime");
        long expirationTimeSeconds = Long.parseLong(expirationTime);

        String shortUrlCore = UUID.randomUUID().toString().substring(0,8);
        UrlData urlData = new UrlData(originalUrl, expirationTimeSeconds);


        try {
            String urlDataJson = objectMapper.writeValueAsString((urlData));
            PutObjectAclRequest request = PutObjectAclRequest.builder().bucket("url-shortener-storge-lambda").key(shortUrlCore+ ".json").build();



            s3Client.putObject(request, RequestBody.fromString(urlDataJson));
           // s3Client.putObject(request, RequestBody.fromString(urlDataJson));



        } catch (Exception exception) {

            throw  new RuntimeException("Error "+ exception.getMessage(), exception);
        }

        Map<String, String> response = new HashMap<>();
        response.put("code", shortUrlCore);



        return response;
    }
}