/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.googlenlptesting;

import java.lang.System;

import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.Document.Type;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.Sentiment;
import com.google.cloud.language.v1.AnalyzeEntitiesRequest;
import com.google.cloud.language.v1.AnalyzeEntitiesResponse;
import com.google.cloud.language.v1.AnalyzeEntitySentimentRequest;
import com.google.cloud.language.v1.AnalyzeEntitySentimentResponse;
import com.google.cloud.language.v1.AnalyzeSentimentResponse;
import com.google.cloud.language.v1.AnalyzeSyntaxRequest;
import com.google.cloud.language.v1.AnalyzeSyntaxResponse;
import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.Document.Type;
import com.google.cloud.language.v1.EncodingType;
import com.google.cloud.language.v1.Entity;
import com.google.cloud.language.v1.EntityMention;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.Sentiment;
import com.google.cloud.language.v1.Token;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.Geometry;
import com.google.maps.model.LatLng;

import java.util.List;
import java.util.Map;

/**
 *
 * @author Alex
 * set GCLOUD_PROJECT=
 * set GOOGLE_APPLICATION_CREDENTIALS=C:\Users\Alex\Documents\NetBeansProjects\NLPTesting-50cc75ecc46e.json
 * set GOOGLE_APPLICATION_CREDENTIALS=/C/Users/Alex/Documents/NetBeansProjects/NLPTesting-50cc75ecc46e.json
 */
public class NLMSomething {
    
    public static void main(String... args) throws Exception {
        // Instantiates a client
        
        LanguageServiceClient language = LanguageServiceClient.create();
        // The text to analyze
        String broadLocation = "South Australia";
        String tweet = "Man arrested after hotel break-in at Edwardstown https://www.police.sa.gov.au/sa-police-";
        String text = broadLocation + ". " + tweet;
        String text2 = "";
        Document doc = Document.newBuilder()
                .setContent(text).setType(Type.PLAIN_TEXT).build();
        
        AnalyzeEntitiesRequest request = AnalyzeEntitiesRequest.newBuilder()
                .setDocument(doc)
                .setEncodingType(EncodingType.UTF16)
                .build();
        
        AnalyzeEntitiesResponse response = language.analyzeEntities(request);
        String location = "";
        String wikipediaUrl[];
        int wikipediaCount = 0;
        // Print the response
        for (Entity entity : response.getEntitiesList()) {
            
            if (entity.getType().toString() == "LOCATION") {
                location += entity.getName() + ", ";
            }
            
            System.out.printf("Entity: %s\n", entity.getName());
            System.out.printf("Salience: %.3f\n", entity.getSalience());
            System.out.printf("Type: %s\n", entity.getType());
            System.out.println("Metadata: \n");
            for (Map.Entry<String, String> entry : entity.getMetadataMap().entrySet()) {
                System.out.printf("%s : %s\n", entry.getKey(), entry.getValue());
                //if (entry.getKey() == "wikipedia_url") {
                //    wikipediaUrl[wikipediaCount] = entry.getValue();
                //    wikipediaCount++;
                //}
            }
            for (EntityMention mention : entity.getMentionsList()) {
                System.out.printf("Begin offset: %d\n", mention.getText().getBeginOffset());
                System.out.printf("Content: %s\n", mention.getText().getContent());
                System.out.printf("Type: %s\n\n", mention.getType());
            }
        }
        
        if (location != "") {
            // Remove trailing comma.
            location = location.substring(0, location.length() - 2);
        }
        System.out.println(location);

        geocode(location);
        // Detects the sentiment of the text
        //Sentiment sentiment = language.analyzeSentiment(doc).getDocumentSentiment();
        
        //System.out.printf("Text: %s%n", text);
        //System.out.printf("Sentiment: %s, %s%n", sentiment.getScore(), sentiment.getMagnitude());
        //System.out.println("blah");
    }
    
    public static LatLng geocode(String textLocation) {
        LatLng latLngResult;
        GeoApiContext context = new GeoApiContext.Builder()
                .apiKey("AIzaSyDBB5x4IwpNgW15HrymfbTCIO20C_cXFBQ")
                .build();
        try {
            GeocodingResult[] results =  GeocodingApi.geocode(context, textLocation).await();
            latLngResult = results[0].geometry.location;
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            System.out.println(gson.toJson(results[0].addressComponents));
            System.out.println(latLngResult);
            return latLngResult;
        } catch (Exception e) {
            System.out.println("Error");
            return null;
        }
        
        //return true;
    }
    
    
    
    String StripHash(String input) {
        // Remove hash, split camel case into separate words.
        
    }
    
}

/* Bad locations
scene
somewhere
hotel
*/