package com.example.intermediate.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MyPageResponseDto {
    private String nickname;
    private List<PostResponseDto> postList;
    private List<CommentResponseDto> commentList;
    private List<PostLikeResponseDto> postLikes;
    private List<CommentLikeResponseDto> commentLikes;
}
