package com.example.intermediate.controller;

import com.example.intermediate.controller.request.SubCommentRequestDto;
import com.example.intermediate.controller.response.ResponseDto;
import com.example.intermediate.service.SubCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Validated
@RequiredArgsConstructor
@RestController
public class SubCommentController {

    private final SubCommentService subCommentService;

    //대댓글 작성
    @RequestMapping(value = "/api/auth/sub-comment", method = RequestMethod.POST)
    public ResponseDto<?> createSubComment(@RequestBody SubCommentRequestDto subCommentRequestDto,
                                           HttpServletRequest request
    ) {
        return subCommentService.createSubComment(subCommentRequestDto,request);
    }

    //대댓글 조회
    @RequestMapping(value = "/api/sub-comment/{id}", method = RequestMethod.GET)
    public ResponseDto<?> getAllSubComment(@PathVariable Long id) {
        return subCommentService.getAllSubCommentsByComment(id);
    }

    // 대댓글 수정
    @RequestMapping(value = "/api/auth/sub-comment/{id}",method = RequestMethod.PUT)
    public ResponseDto<?> updateSubComment(@PathVariable Long id,
                                           @RequestBody SubCommentRequestDto requestDto,
                                           HttpServletRequest request
    ) {
        return subCommentService.updateSubComment(id, requestDto,request);
    }

    // 대댓글 삭제
    @RequestMapping(value = "api/auth/sub-comment/{id}", method = RequestMethod.DELETE)
    public ResponseDto<?> deleteSubComment(@PathVariable Long id, HttpServletRequest request) {
        return subCommentService.deleteSubComment(id, request);
    }
}
