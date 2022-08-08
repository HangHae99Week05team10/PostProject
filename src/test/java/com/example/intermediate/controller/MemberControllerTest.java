package com.example.intermediate.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

import com.example.intermediate.IntermediateApplication;
import com.example.intermediate.controller.request.MemberRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;


@ComponentScan(basePackageClasses = IntermediateApplication.class)
@WebMvcTest(MemberController.class)
class MemberControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void signup() throws Exception{
        // 올바른 케이스
        MemberRequestDto memberRequestDto1 = new MemberRequestDto("dalenvtq1", "qwerasdf", "qwerasdf");
        // 비번 미스매치 케이스
        MemberRequestDto memberRequestDto2 = new MemberRequestDto("dalenvtq2", "qwerasdf", "qwerasd");
        // 비번 조건 벗어남 케이스
        MemberRequestDto memberRequestDto3 = new MemberRequestDto("dalenvtq3", "QWERASDF", "QWERASDF");

        mockMvc.perform(post("/api/member/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(memberRequestDto1)))
                .andExpect(status().isOk())
                .andDo(print());

        mockMvc.perform(post("/api/member/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberRequestDto2)))
                .andExpect(status().isBadRequest())
                .andDo(print());

        mockMvc.perform(post("/api/member/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberRequestDto3)))
                .andExpect(status().isBadRequest())
                .andDo(print());

    }

    @Test
    void login() {
    }

    @Test
    void logout() {
    }
}