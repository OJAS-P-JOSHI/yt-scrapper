package com.example.yt_scrapper.services;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.yt_scrapper.config.YtConfig;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class YtService {

    @Autowired
    private YtConfig ytConfig;

    public String extractVideoId(String videoLink) {
        Pattern urlPattern1 = Pattern.compile("(?<=watch\\?v=|/videos/|embed\\/|youtu.be\\/|\\/v\\/|\\/e\\/|watch\\?v%3D|watch\\?feature=player_embedded&v=|%2Fvideos%2F|embed\\.|youtu\\.be\\/|\\/v\\/|\\/e\\/|watch\\?v=|watch\\?feature=player_embedded&v=|%2Fvideos%2F|embed\\.)([^\"&?\\/\\s]{11})", Pattern.CASE_INSENSITIVE);
        Matcher matcher1 = urlPattern1.matcher(videoLink);

        Pattern urlPattern2 = Pattern.compile("youtu.be\\/(.{11})", Pattern.CASE_INSENSITIVE);
        Matcher matcher2 = urlPattern2.matcher(videoLink);

        if (matcher1.find()) {
            return matcher1.group(1);
        } else if (matcher2.find()) {
            return matcher2.group(1);
        }

        return null;
    }

    public JsonNode getVideoDetails(String videoId) throws Exception {
        String apiUrl = ytConfig.getApiUrl();
        String apiKey = "key=" + ytConfig.getApiKey();
        String idParam = "id=" + videoId;
        String partParam = "part=snippet";

        String url = apiUrl + "?" + apiKey + "&" + partParam + "&" + idParam;
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(url, String.class);

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readTree(response).path("items").get(0).path("snippet");
    }

    public String[] extractTags(JsonNode videoDetails) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.treeToValue(videoDetails.path("tags"), String[].class);
    }

    public JsonNode getVideoComments(String videoId) throws Exception {
        String apiUrl = "https://www.googleapis.com/youtube/v3/commentThreads";
        String apiKey = "key=" + ytConfig.getApiKey();
        String videoIdParam = "videoId=" + videoId;
        String partParam = "part=snippet";
        String maxResultsParam = "maxResults=100"; // Limit to 10 comments

        String url = apiUrl + "?" + apiKey + "&" + partParam + "&" + videoIdParam + "&" + maxResultsParam;
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(url, String.class);

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readTree(response).path("items");
    }
}
