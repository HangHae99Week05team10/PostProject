package com.example.intermediate.controller;

import com.example.intermediate.controller.response.ResponseDto;
import com.example.intermediate.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/like")
public class LikeController {
    private LikeService likeService;

    @PostMapping("/post/{id}")
    public ResponseDto<?> likePost(@PathVariable Long id, HttpServletRequest request){
        return likeService.likePost(id,request);
    }

    @PostMapping("/comment/{id}")
    public ResponseDto<?> likeComment(@PathVariable Long id, HttpServletRequest request){
        return likeService.likeComment(id,request);
    }
}
