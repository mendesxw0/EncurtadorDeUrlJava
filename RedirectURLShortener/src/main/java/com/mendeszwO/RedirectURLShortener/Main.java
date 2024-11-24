package com.mendeszwO.RedirectURLShortener;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectAclRequest;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main implements RequestHandler<Map<String, Object>, Map<String, Object>> {

    private final S3Client s3Client = S3Client.builder().build();
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public Map<String, Object> handleRequest(Map<String, Object> input, Context context) {

        String pathParameters = (String) input.get("rawPath");  // path cru
        String shortUrlCore = pathParameters.replace("/", "");


        if(shortUrlCore == null || shortUrlCore.isEmpty()) {
            throw  new IllegalArgumentException("Invalid input.");
        }

        GetObjectAclRequest getObjectAclRequest = GetObjectAclRequest.builder().bucket("url-shortener-storage").key(shortUrlCore + ".json").build();


        InputStream s3ObjectStrem;

        try {
            s3ObjectStrem = s3Client.getObject(getObjectAclRequest);

        } catch (Exception e) {
            throw new RuntimeException("error");
        }

      UrlData urlData;

        try {
            urlData = objectMapper.readValue(s3ObjectStrem, UrlData.class);
        } catch (Exception e) {
            throw new RuntimeException("error Url Data. "+ e.getMessage(), e);
        }

        long currentTimeSeconds = System.currentTimeMillis() / 1000;
        if (currentTimeSeconds < urlData.getEXpirationTime) {
            Map<String, Object>  response = new HashMap<>();

            response.put("statuscode", 302);
            Map<String, String> headers = new HashMap<>();
            response.put("headers", headers);


            return response;
        }
        Map<String, Object> response = new HashMap<>();
        response.put("StatusCore", 410);

        response.put("body", "this URL has expired");
        return response;
    }
}