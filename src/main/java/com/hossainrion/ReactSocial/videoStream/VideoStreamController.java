package com.hossainrion.ReactSocial.videoStream;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/video")
public class VideoStreamController {

    private final VideoStreamService videoStreamService;

    public VideoStreamController(VideoStreamService videoStreamService) {
        this.videoStreamService = videoStreamService;
    }

    @GetMapping("/stream/{title}/{suffix}")
    public Mono<ResponseEntity<byte[]>> streamVideo( @RequestHeader(value = "Range", required = false) String httpRangeList,
                                                     @PathVariable("title") String title, @PathVariable("suffix") String suffix) {
        return Mono.just(videoStreamService.prepareContent(title,suffix, httpRangeList));
    }
}
