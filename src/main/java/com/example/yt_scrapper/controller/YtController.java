// src/main/java/com/example/yt_scrapper/controller/YtController.java

package com.example.yt_scrapper.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.yt_scrapper.services.YtService;
import com.fasterxml.jackson.databind.JsonNode;

@Controller
public class YtController {

    @Autowired
    private YtService youtubeService;

    @GetMapping("/")
    public String getHome() {
        return "index";
    }

    @PostMapping("/processing")
    public String getData(@RequestParam String videoLink, Model model) {
        String videoId = youtubeService.extractVideoId(videoLink);
        if (videoId != null) {
            try {
                JsonNode videoDetails = youtubeService.getVideoDetails(videoId);
                String title = videoDetails.path("title").asText();
                String description = videoDetails.path("description").asText();
                String thumbUrl = videoDetails.path("thumbnails").path("standard").path("url").asText();
                String tags[] = youtubeService.extractTags(videoDetails);

                model.addAttribute("vtitle", title);
                model.addAttribute("vdesc", description);
                model.addAttribute("vthumb", thumbUrl);
                model.addAttribute("vtags", tags);
                
                return "details";
            } catch (Exception e) {
                e.printStackTrace();
                return "error";
            }
        }
        return "error";
    }
}
