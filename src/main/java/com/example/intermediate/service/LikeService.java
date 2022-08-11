package com.example.intermediate.service;

import com.example.intermediate.controller.response.ResponseDto;
import com.example.intermediate.domain.*;
import com.example.intermediate.domain.like.*;
import com.example.intermediate.jwt.TokenProvider;
import com.example.intermediate.repository.*;
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
    private final SubCommentLikeRepository subCommentLikeRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final SubCommentRepository subCommentRepository;

    private final TokenProvider tokenProvider;


    public ResponseDto<?> likePost(Long postId, HttpServletRequest request){
        ValidationResult result = validateMember(request);
        Member member = result.getMember();

        if (member==null){
            return ResponseDto.fail(result.getCode(), result.getMessage());
        }
        return like(postId, member, ParentType.POST);
    }
    public ResponseDto<?> likeComment(Long commentId, HttpServletRequest request){
        ValidationResult result = validateMember(request);
        Member member = result.getMember();

        if (member==null){
            return ResponseDto.fail(result.getCode(), result.getMessage());
        }
        return like(commentId, member, ParentType.COMMENT);
    }

    public ResponseDto<?> likeSubComment(Long subCommentId, HttpServletRequest request){
        ValidationResult result = validateMember(request);
        Member member = result.getMember();

        if (member==null){
            return ResponseDto.fail(result.getCode(), result.getMessage());
        }
        return like(subCommentId, member, ParentType.SUBCOMMENT);
    }

    ResponseDto<?> like(Long id, Member member, ParentType parentType){
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
        } else if (parentType.name().equals("SUBCOMMENT")) {
            SubComment subComment = subCommentRepository.findById(id).orElse(null);
            if (subComment == null){
                return ResponseDto.fail("POST_NOT_FOUND", "해당 게시글을 찾을 수 없습니다.");
            }
            SubCommentLikeId subCommentLikeId = SubCommentLikeId.builder()
                    .subComment(subComment)
                    .member(member)
                    .build();
            SubCommentLike subCommentLike = SubCommentLike.builder()
                    .subCommentLikeId(subCommentLikeId)
                    .build();
            if(subCommentLikeRepository.findById(subCommentLikeId).isEmpty()){
                subCommentLikeRepository.save(subCommentLike);
                return ResponseDto.success("like success");
            }else {
                subCommentLikeRepository.delete(subCommentLike);
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
    private static class ValidationResult{
        private String code;
        private String message;
        private Member member;
    }

    enum ParentType { COMMENT, POST, SUBCOMMENT;}
}
