package com.example.intermediate.controller;

import com.example.intermediate.controller.request.CommentRequestDto;
import com.example.intermediate.controller.request.LoginRequestDto;
import com.example.intermediate.controller.request.MemberRequestDto;
import com.example.intermediate.controller.request.PostRequestDto;
import com.example.intermediate.controller.response.CommentResponseDto;
import com.example.intermediate.controller.response.PostResponseDto;
import com.example.intermediate.controller.response.ResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class LikeControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void likePost() throws Exception{
        //이후 작업을 위한 테스트용 아이디 만들기
        MemberRequestDto memberRequestDto = new MemberRequestDto("likepost", "qwerasdf", "qwerasdf");
        mockMvc.perform(post("/api/member/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(memberRequestDto)));

        //로그인
        LoginRequestDto loginRequestDto = new LoginRequestDto("likepost", "qwerasdf");
        MvcResult result = mockMvc.perform(post("/api/member/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDto)))
                .andReturn();
        String headerAuthorization = result.getResponse().getHeader("Authorization");
        String headerRefreshToken = result.getResponse().getHeader("Refresh-Token");
        String headerAccessTokenExpireTime = result.getResponse().getHeader("Access-Token-Expire-Time");

        // 글쓰기
        PostRequestDto postRequestDto = new PostRequestDto("like post test", "test");
        MvcResult result1 = mockMvc.perform(post("/api/auth/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postRequestDto))
                        .header("Authorization", headerAuthorization)
                        .header("Refresh-Token", headerRefreshToken)
                        .header("Access-Token-Expire-Time", headerAccessTokenExpireTime))
                .andReturn();
        String content = result1.getResponse().getContentAsString();
        ResponseDto response = objectMapper.readValue(content, ResponseDto.class);
        String body = objectMapper.writeValueAsString(response.getData());
        PostResponseDto postResponseDto = objectMapper.readValue(body, PostResponseDto.class);
        long postId = postResponseDto.getId();

        //글에 좋아요 누르기
        mockMvc.perform(post(String.format("/api/auth/like/post/%d",postId))
                        .header("Authorization", headerAuthorization)
                        .header("Refresh-Token", headerRefreshToken)
                        .header("Access-Token-Expire-Time", headerAccessTokenExpireTime))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"success\":true,\"data\":\"like success\",\"error\":null}"));

        //글에 좋아요 취소
        mockMvc.perform(post(String.format("/api/auth/like/post/%d",postId))
                        .header("Authorization", headerAuthorization)
                        .header("Refresh-Token", headerRefreshToken)
                        .header("Access-Token-Expire-Time", headerAccessTokenExpireTime))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"success\":true,\"data\":\"unlike success\",\"error\":null}"));

    }

    @Test
    void likeComment() throws Exception{
        //이후 작업을 위한 테스트용 아이디 만들기
        MemberRequestDto memberRequestDto = new MemberRequestDto("likepost", "qwerasdf", "qwerasdf");
        mockMvc.perform(post("/api/member/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(memberRequestDto)));

        //로그인
        LoginRequestDto loginRequestDto = new LoginRequestDto("likepost", "qwerasdf");
        MvcResult result = mockMvc.perform(post("/api/member/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDto)))
                .andReturn();
        String headerAuthorization = result.getResponse().getHeader("Authorization");
        String headerRefreshToken = result.getResponse().getHeader("Refresh-Token");
        String headerAccessTokenExpireTime = result.getResponse().getHeader("Access-Token-Expire-Time");

        // 글쓰기
        PostRequestDto postRequestDto = new PostRequestDto("like post test", "test");
        MvcResult result1 = mockMvc.perform(post("/api/auth/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postRequestDto))
                        .header("Authorization", headerAuthorization)
                        .header("Refresh-Token", headerRefreshToken)
                        .header("Access-Token-Expire-Time", headerAccessTokenExpireTime))
                .andReturn();
        String content = result1.getResponse().getContentAsString();
        ResponseDto response = objectMapper.readValue(content, ResponseDto.class);
        String body = objectMapper.writeValueAsString(response.getData());
        PostResponseDto postResponseDto = objectMapper.readValue(body, PostResponseDto.class);
        long postId = postResponseDto.getId();

        //댓글 쓰기
        CommentRequestDto commentRequestDto = new CommentRequestDto(postId, "Test");
        MvcResult result2 = mockMvc.perform(post("/api/auth/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentRequestDto))
                        .header("Authorization", headerAuthorization)
                        .header("Refresh-Token", headerRefreshToken)
                        .header("Access-Token-Expire-Time", headerAccessTokenExpireTime))
                .andReturn();
        String content1 = result2.getResponse().getContentAsString();
        ResponseDto response1 = objectMapper.readValue(content1, ResponseDto.class);
        String body1 = objectMapper.writeValueAsString(response1.getData());
        CommentResponseDto commentResponseDto = objectMapper.readValue(body1, CommentResponseDto.class);
        long commentId = commentResponseDto.getId();


        //글에 좋아요 누르기
        mockMvc.perform(post(String.format("/api/auth/like/comment/%d",commentId))
                        .header("Authorization", headerAuthorization)
                        .header("Refresh-Token", headerRefreshToken)
                        .header("Access-Token-Expire-Time", headerAccessTokenExpireTime))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"success\":true,\"data\":\"like success\",\"error\":null}"));

        //글에 좋아요 취소
        mockMvc.perform(post(String.format("/api/auth/like/comment/%d",commentId))
                        .header("Authorization", headerAuthorization)
                        .header("Refresh-Token", headerRefreshToken)
                        .header("Access-Token-Expire-Time", headerAccessTokenExpireTime))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"success\":true,\"data\":\"unlike success\",\"error\":null}"));
    }
}