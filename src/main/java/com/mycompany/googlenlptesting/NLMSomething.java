/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.googlenlptesting;

import java.lang.System;

import com.google.cloud.language.v1.AnalyzeEntitiesRequest;
import com.google.cloud.language.v1.AnalyzeEntitiesResponse;
import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.Document.Type;
import com.google.cloud.language.v1.EncodingType;
import com.google.cloud.language.v1.Entity;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import java.util.ArrayList;
import java.util.Arrays;

import java.util.List;

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
        String tweet = "I'm going for a walk to San Francisco, then I'll climb a mountain in Adelaide, then go to London";
        List<String> locationEntities = nlpSometext(tweet);
        for (String singleLocationEntity : locationEntities) {
            System.out.println(singleLocationEntity);
        }
        System.out.println("----------");
        List<String> locationEntitiesCombos = comb(2, locationEntities);
        for (String singleLocationEntitySet : locationEntitiesCombos) {
            System.out.println(singleLocationEntitySet);
        }
    }
    
    public static List<String> nlpSometext(String input) {
        LanguageServiceClient language;
        try {
            language = LanguageServiceClient.create();
        } catch (java.io.IOException e) {
            System.out.println("Error: " + e.getMessage());
            return null;
        }
        
        Document doc = Document.newBuilder()
                .setContent(input).setType(Type.PLAIN_TEXT).build();
        
        AnalyzeEntitiesRequest request = AnalyzeEntitiesRequest.newBuilder()
                .setDocument(doc)
                .setEncodingType(EncodingType.UTF16)
                .build();
        
        AnalyzeEntitiesResponse response = language.analyzeEntities(request);
        List<String> locations = new ArrayList<String>();
        //String wikipediaUrl[];
        //int wikipediaCount = 0;
        // Print the response
        for (Entity entity : response.getEntitiesList()) {
            
            if (entity.getType().toString() == "LOCATION") {
                //location += entity.getName() + ", ";
                locations.add(entity.getName());
                
            }
            
            //System.out.printf("Entity: %s\n", entity.getName());
            //System.out.printf("Salience: %.3f\n", entity.getSalience());
            //System.out.printf("Type: %s\n", entity.getType());
            //System.out.println("Metadata: \n");
            //for (Map.Entry<String, String> entry : entity.getMetadataMap().entrySet()) {
                //System.out.printf("%s : %s\n", entry.getKey(), entry.getValue());
                //if (entry.getKey() == "wikipedia_url") {
                //    wikipediaUrl[wikipediaCount] = entry.getValue();
                //    wikipediaCount++;
                //}
            //}
            //for (EntityMention mention : entity.getMentionsList()) {
            //    System.out.printf("Begin offset: %d\n", mention.getText().getBeginOffset());
            //    System.out.printf("Content: %s\n", mention.getText().getContent());
            //    System.out.printf("Type: %s\n\n", mention.getType());
            //}
        }
        return locations;
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
        return "nothing";
    }
    
    static List<String> comb(int minSetSize, List<String> items) {
    
        List<String> results = new ArrayList<String>();

        for (int k = minSetSize; k <= items.size(); k++) {
            results.addAll(kcomb(items, 0, k, new String[k]));
        }
        return results;
}

    static List<String> kcomb(List<String> items, int n, int k, String[] arr) {
        List<String> innerResults = new ArrayList<String>();
        if (k == 0) {
        //System.out.println(Arrays.toString(arr));
            innerResults.add(Arrays.toString(arr));
        } else {
            for (int i = n; i <= items.size() - k; i++) {
                arr[arr.length - k] = items.get(i);
                innerResults.addAll(kcomb(items, i + 1, k - 1, arr));
            }
        }
        return innerResults;
    }
}


/* Bad locations
scene
somewhere
hotel
*/