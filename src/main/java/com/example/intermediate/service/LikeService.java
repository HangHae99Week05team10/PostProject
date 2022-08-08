package com.example.intermediate.service;

import com.example.intermediate.controller.response.ResponseDto;
import com.example.intermediate.domain.*;
import com.example.intermediate.jwt.TokenProvider;
import com.example.intermediate.repository.CommentLikeRepository;
import com.example.intermediate.repository.CommentRepository;
import com.example.intermediate.repository.PostLikeRepository;
import com.example.intermediate.repository.PostRepository;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final PostLikeRepository postLikeRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final PostRepository postRepository;
    protected final CommentRepository commentRepository;
    private final TokenProvider tokenProvider;


    public ResponseDto<?> likePost(Long postId, HttpServletRequest request){
        return like(postId, request, ParentType.POST);
    }
    public ResponseDto<?> likeComment(Long commentId, HttpServletRequest request){
        return like(commentId, request, ParentType.COMMENT);
    }

    public ResponseDto<?> like(Long id, HttpServletRequest request, ParentType parentType){
        ValidationResult result = validateMember(request);
        Member member = result.getMember();
        if (member==null){
            return ResponseDto.fail(result.getCode(), result.getMessage());
        }

        if (parentType.name().equals("COMMENT")){
            Comment comment = commentRepository.findById(id).orElse(null);
            if (comment == null){
                return ResponseDto.fail("COMMENT_NOT_FOUND", "해당 댓글을 찾을 수 없습니다.");
            }
            CommentLikeId commentLikeId = CommentLikeId.builder()
                    .comment(comment)
                    .member(member)
                    .build();
            CommentLike commentLike = CommentLike.builder()
                    .commentLikeId(commentLikeId)
                    .build();
            if(commentLikeRepository.findById(commentLikeId).isEmpty()){
                commentLikeRepository.save(commentLike);
                return ResponseDto.success("like success");
            }else {
                commentLikeRepository.delete(commentLike);
                return ResponseDto.success("unlike success");
            }
        } else if (parentType.name().equals("POST")) {
            Post post = postRepository.findById(id).orElse(null);
            if (post == null){
                return ResponseDto.fail("POST_NOT_FOUND", "해당 게시글을 찾을 수 없습니다.");
            }
            PostLikeId postLikeId = PostLikeId.builder()
                    .post(post)
                    .member(member)
                    .build();
            PostLike postLike = PostLike.builder()
                    .postLikeId(postLikeId)
                    .build();
            if(postLikeRepository.findById(postLikeId).isEmpty()){
                postLikeRepository.save(postLike);
                return ResponseDto.success("like success");
            }else {
                postLikeRepository.delete(postLike);
                return ResponseDto.success("unlike success");
            }
        }else{
            throw new IllegalArgumentException();
        }
    }
    @Transactional
    public ValidationResult validateMember(HttpServletRequest request) {
        if (null == request.getHeader("Refresh-Token")) {
            return ValidationResult.builder()
                    .member(null)
                    .code("MEMBER_NOT_FOUND")
                    .message("로그인이 필요합니다.")
                    .build();
        }

        if (null == request.getHeader("Authorization")) {
            return ValidationResult.builder()
                    .member(null)
                    .code("MEMBER_NOT_FOUND")
                    .message("로그인이 필요합니다.")
                    .build();
        }

        if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
            return ValidationResult.builder()
                    .member(null)
                    .code("INVALID_TOKEN")
                    .message("Token이 유효하지 않습니다.")
                    .build();
        }
        Member member = tokenProvider.getMemberFromAuthentication();
        return ValidationResult.builder()
                .member(member)
                .build();
    }

    @Data
    @Builder
    private class ValidationResult{
        private String code = "";
        private String message = "";
        private Member member;
    }

    private enum ParentType { COMMENT, POST;}
}
