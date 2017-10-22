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
import com.google.maps.model.Geometry;
import com.google.maps.model.LatLng;
import java.util.ArrayList;
import java.util.Arrays;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Alex
 * set GCLOUD_PROJECT=
 * set GOOGLE_APPLICATION_CREDENTIALS=C:\Users\Alex\Documents\NetBeansProjects\NLPTesting-50cc75ecc46e.json
 * set GOOGLE_APPLICATION_CREDENTIALS=/C/Users/Alex/Documents/NetBeansProjects/NLPTesting-50cc75ecc46e.json
 * set GOOGLE_MAPS_API_KEY=xxx
 */
public class NLMSomething {
    
    public static void main(String... args) throws Exception {
        // Instantiates a client
        LanguageServiceClient language = LanguageServiceClient.create();
        // The text to analyze
        System.out.println("----------");
        String userProfileLocation = "Queensland";
        System.out.println("Twitter user location (from their profile):\n  " + userProfileLocation);
        
        String tweet = "Major Flood Warning for the Kolan River and Baffle Creek. Stay safe and keep up to date at http://www.bom.gov.au/qld/warnings/  #QldFlood";
        
        System.out.println("----------");
        System.out.println("Twitter text:\n  " + tweet);
        tweet = preProcessTweet(tweet);
        System.out.println("----------");
        System.out.println("Pre-processed Twitter text:\n  " + tweet);
        //System.exit(0);
        
        List<String> locationEntities = nlpSometext(tweet);
        
        System.out.println("----------");
        
        System.out.println("Location entities from Google NLP API:");
        for (String singleLocationEntity : locationEntities) {
            System.out.println("  " + singleLocationEntity);
        }
        if (userProfileLocation != null) {
            locationEntities.add(userProfileLocation);
        }
        System.out.println("----------");
        System.out.println("List of combinations of the location entities set:");
        List<String> locationEntitiesCombos = comb(2, locationEntities);
        for (String singleLocationEntitySet : locationEntitiesCombos) {
            // Remove the []'s.
            singleLocationEntitySet = singleLocationEntitySet.substring(0, singleLocationEntitySet.length() - 1);
            singleLocationEntitySet = singleLocationEntitySet.substring(1, singleLocationEntitySet.length());
            
            // Get geometry (as opposed to points).
            /*
            Geometry possibleGeometry = geocodeStringToGeometry(singleLocationEntitySet);
            String formattedResult;
            if (possibleGeometry == null) {
                formattedResult = "Could not geocode.";
            } else {
                formattedResult = "box: " + possibleGeometry.bounds.toString() + " point: " + possibleGeometry.location.toString();
            }
            System.out.println("  " + singleLocationEntitySet);
            System.out.println("    " + formattedResult);
            */
            
            // Get points.
            LatLng possiblePoint = geocode(singleLocationEntitySet);
            String possiblePointString;
            if (possiblePoint == null) {
                possiblePointString = "Could not geocode";
            } else  {
                possiblePointString = possiblePoint.toString();
            }
            
            System.out.println("  " + singleLocationEntitySet);
            System.out.println("    " + possiblePointString);

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
//            for (Map.Entry<String, String> entry : entity.getMetadataMap().entrySet()) {
//                //System.out.printf("%s : %s\n", entry.getKey(), entry.getValue());
//                if (entry.getKey() == "wikipedia_url") {
//                    wikipediaUrl[wikipediaCount] = entry.getValue();
//                    wikipediaCount++;
//                }
//            }
            //for (EntityMention mention : entity.getMentionsList()) {
            //    System.out.printf("Begin offset: %d\n", mention.getText().getBeginOffset());
            //    System.out.printf("Content: %s\n", mention.getText().getContent());
            //    System.out.printf("Type: %s\n\n", mention.getType());
            //}
        }
        locations = removeDuplicateLocationEntities(locations);
        return locations;
    }
    
    private static List<String> removeDuplicateLocationEntities(List<String> input) {
        // Need to complete this
        return input;
    }
    
    public static LatLng geocode(String textLocation) {
        String apiKeyString = System.getenv("GOOGLE_MAPS_API_KEY");
        LatLng latLngResult;
        GeoApiContext context = new GeoApiContext.Builder()
                .apiKey(apiKeyString)
                .build();
        
        try {
            GeocodingResult[] results =  GeocodingApi.geocode(context, textLocation).await();
            latLngResult = results[0].geometry.location;
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            //System.out.println(gson.toJson(results[0].addressComponents));
            //System.out.println(latLngResult);
            return latLngResult;
        } catch (Exception e) {
            System.out.println("Error");
            return null;
        }
        
        //return true;
    }
    
    public static Geometry geocodeStringToGeometry(String textLocation) {
        String apiKeyString = System.getenv("GOOGLE_MAPS_API_KEY");
        Geometry result;
        GeoApiContext context = new GeoApiContext.Builder()
                .apiKey(apiKeyString)
                .build();
        
        try {
            GeocodingResult[] results =  GeocodingApi.geocode(context, textLocation).await();
            result = results[0].geometry;
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            //System.out.println(gson.toJson(results[0].addressComponents));
            //System.out.println(latLngResult);
            return latLngResult;
        } catch (Exception e) {
            System.out.println("Error");
            return null;
        }
        
        //return true;
    }
    
    static List<String> comb(int minSetSize, List<String> items) {
        int maxSetSize = 5;
        if (items.size() > maxSetSize) {
            return null;
        }
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
    
    static String preProcessTweet(String input) {
        // Do some basic things to clean up the tweet.
        // 1. Remove hyperlinks, they seem to mess up the NLP
        // 2. Remove the hash symbol from hash tags
        // 3. Insert spaces in hashtags if they use camel case or title case.
        //      For example, #BestThingEver becomes Best Thing Ever.
        
        String result = removeUrl(input);
        result = fixHashtags(result);
        return result;
    }
    
    static private String removeUrl(String input) {
        //https://stackoverflow.com/questions/12366496/removing-the-url-from-text-using-java
        String urlPattern = "((https?|ftp|gopher|telnet|file|Unsure|http):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
        Pattern p = Pattern.compile(urlPattern,Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(input);
        int i = 0;
        while (m.find()) {
            input = input.replaceAll(m.group(i),"").trim();
            i++;
        }
        return fixHashtags(input);
    }
    
    static private String fixHashtags(String input) {
        String result = input;
        Pattern hashRegex = Pattern.compile("#(\\S+)");
        Matcher mat = hashRegex.matcher(input);
        while (mat.find()) {
            String fixed = fixCamelCase(mat.group(1));
            result = result.replace(mat.group(0), fixed);
        }
        return result;
    }
    
    static private String fixCamelCase(String input) {
        //https://stackoverflow.com/questions/7593969/regex-to-split-camelcase-or-titlecase-advanced
        String result = "";
        for (String w : input.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])")) {
            result += w + " ";
        }
        result = result.trim();
        return result;
    }
    
    static private LatLng getCoordinatesFromWikipediaPage(String url)     {
        // example https://en.wikipedia.org/w/api.php?action=query&prop=coordinates&titles=New_York_City_Marathon&format=json
        /* Example output
        {
  "batchcomplete": "",
  "query": {
    "normalized": [
      {
        "from": "New_York_City_Marathon",
        "to": "New York City Marathon"
      }
    ],
    "pages": {
      "429897": {
        "pageid": 429897,
        "ns": 0,
        "title": "New York City Marathon",
        "coordinates": [
          {
            "lat": 40.772,
            "lon": -73.978,
            "primary": "",
            "globe": "earth"
          }
        ]
      }
    }
  }
}
        */
        
        return null;
    }
}


/* Bad locations
scene
somewhere
hotel
intersection
*/