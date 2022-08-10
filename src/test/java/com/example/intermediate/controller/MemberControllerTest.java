package com.example.intermediate.controller;

import com.example.intermediate.controller.request.LoginRequestDto;
import com.example.intermediate.controller.request.MemberRequestDto;
import com.example.intermediate.repository.MemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.event.annotation.BeforeTestMethod;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.validation.constraints.AssertFalse;
import javax.validation.constraints.AssertTrue;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@AutoConfigureMockMvc
class MemberControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MemberRepository memberRepository;


    @Test
    void signup1() throws Exception{
        // 올바른 케이스
        MemberRequestDto memberRequestDto1 = new MemberRequestDto("dalenvtq1", "qwerasdf", "qwerasdf");
        mockMvc.perform(post("/api/member/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberRequestDto1)))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"success\":true}"));
        assertFalse(memberRepository.findByNickname("dalenvtq1").isEmpty());
    }
    @Test
    void signup2() throws Exception{
        // 비번 미스매치 케이스
        MemberRequestDto memberRequestDto2 = new MemberRequestDto("dalenvtq2", "qwerasdf", "qwerasd");
        mockMvc.perform(post("/api/member/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberRequestDto2)))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"error\":{\"code\":\"PASSWORDS_NOT_MATCHED\"}}"));
        assertTrue(memberRepository.findByNickname("dalenvtq2").isEmpty());
    }

    @Test
    void signup3() throws Exception{
        // 비번 조건 벗어남 케이스
        MemberRequestDto memberRequestDto3 = new MemberRequestDto("dalenvtq3", "QWERASDF", "QWERASDF");
        mockMvc.perform(post("/api/member/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberRequestDto3)))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"error\":{\"code\":\"BAD_REQUEST\"}}"));
        assertTrue(memberRepository.findByNickname("dalenvtq3").isEmpty());
    }

    @Test
    void login1() throws Exception{
        //이후 작업을 위한 테스트용 아이디 만들기
        MemberRequestDto memberRequestDto = new MemberRequestDto("dalenvtq", "qwerasdf", "qwerasdf");
        MvcResult result = mockMvc.perform(post("/api/member/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberRequestDto)))
                .andReturn();
        // 정상 로그인
        LoginRequestDto loginRequestDto = new LoginRequestDto("dalenvtq", "qwerasdf");
        mockMvc.perform(post("/api/member/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDto)))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"success\":true}"))
                .andDo(print());
    }

    @Test
    void login2() throws Exception{
        // 가입되지 않은 아이디로 로그인
        LoginRequestDto loginRequestDto = new LoginRequestDto("dalenvtq123", "qwerasdf");
        mockMvc.perform(post("/api/member/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDto)))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"error\":{\"code\":\"MEMBER_NOT_FOUND\"}}"));
    }

    @Test
    void login3() throws Exception{
        // 틀린 비밀번호로 로그인
        LoginRequestDto loginRequestDto = new LoginRequestDto("dalenvtq", "qwerasdf1");
        mockMvc.perform(post("/api/member/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDto)))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"error\":{\"code\":\"INVALID_MEMBER\"}}"));
    }

    @Test
    void logout1() throws Exception{
        //이후 작업을 위한 테스트용 아이디 만들기
        MemberRequestDto memberRequestDto = new MemberRequestDto("logout1", "qwerasdf", "qwerasdf");
        mockMvc.perform(post("/api/member/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberRequestDto)));

        //로그인
        LoginRequestDto loginRequestDto = new LoginRequestDto("logout1", "qwerasdf");
        MvcResult result = mockMvc.perform(post("/api/member/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequestDto)))
                .andReturn();
        String headerAuthorization = result.getResponse().getHeader("Authorization");
        String headerRefreshToken = result.getResponse().getHeader("Refresh-Token");
        String headerAccessTokenExpireTime = result.getResponse().getHeader("Access-Token-Expire-Time");

        //로그아웃
        mockMvc.perform(post("/api/auth/member/logout")
                        .header("Authorization", headerAuthorization)
                        .header("Refresh-Token", headerRefreshToken)
                        .header("Access-Token-Expire-Time", headerAccessTokenExpireTime))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"success\":true}"));
    }
}