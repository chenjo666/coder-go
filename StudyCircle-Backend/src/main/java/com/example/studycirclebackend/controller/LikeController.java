package com.example.studycirclebackend.controller;

import com.example.studycirclebackend.dto.Response;
import com.example.studycirclebackend.service.LikeService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/likes")
public class LikeController {
    @Resource
    private LikeService likeService;



    @PostMapping
    public Response createLike(@RequestBody Map<String, String> args) {
        Long objectId = Long.parseLong(args.get("objectId"));
        String objectType = args.get("objectType");
        return likeService.createLike(objectId, objectType);
    }

    @DeleteMapping("/{objectType}/{objectId}")
    public Response deleteLike(@PathVariable("objectId") Long objectId, @PathVariable("objectType") String objectType) {
        return likeService.deleteLike(objectId, objectType);
    }
}