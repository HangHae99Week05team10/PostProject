package com.example.intermediate.service;

import com.example.intermediate.controller.request.LoginRequestDto;
import com.example.intermediate.controller.request.MemberRequestDto;
import com.example.intermediate.controller.request.TokenDto;
import com.example.intermediate.controller.response.*;
import com.example.intermediate.domain.Comment;
import com.example.intermediate.domain.Member;
import com.example.intermediate.domain.Post;
import com.example.intermediate.domain.like.CommentLike;
import com.example.intermediate.domain.like.CommentLikeId;
import com.example.intermediate.domain.like.PostLike;
import com.example.intermediate.domain.like.PostLikeId;
import com.example.intermediate.jwt.TokenProvider;
import com.example.intermediate.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class MemberService{

  private final MemberRepository memberRepository;
  private final PostRepository postRepository;
  private final SubCommentRepository subCommentRepository;
  private final PostLikeRepository postLikeRepository;
  private final CommentLikeRepository commentLikeRepository;
  private final CommentRepository commentRepository;
  private final PasswordEncoder passwordEncoder;
  private final TokenProvider tokenProvider;

  @Transactional
  public ResponseDto<?> createMember(MemberRequestDto requestDto) {
    if (null != isPresentMember(requestDto.getNickname())) {
      return ResponseDto.fail("DUPLICATED_NICKNAME",
          "중복된 닉네임 입니다.");
    }

    if (!requestDto.getPassword().equals(requestDto.getPasswordConfirm())) {
      return ResponseDto.fail("PASSWORDS_NOT_MATCHED",
          "비밀번호와 비밀번호 확인이 일치하지 않습니다.");
    }

    Member member = Member.builder()
            .nickname(requestDto.getNickname())
                .password(passwordEncoder.encode(requestDto.getPassword()))
                    .build();
    memberRepository.save(member);
    return ResponseDto.success(
        MemberResponseDto.builder()
            .id(member.getId())
            .nickname(member.getNickname())
            .createdAt(member.getCreatedAt())
            .modifiedAt(member.getModifiedAt())
            .build()
    );
  }

  @Transactional
  public ResponseDto<?> login(LoginRequestDto requestDto, HttpServletResponse response) {
    Member member = isPresentMember(requestDto.getNickname());
    if (null == member) {
      return ResponseDto.fail("MEMBER_NOT_FOUND",
          "사용자를 찾을 수 없습니다.");
    }

    if (!member.validatePassword(passwordEncoder, requestDto.getPassword())) {
      return ResponseDto.fail("INVALID_MEMBER", "사용자를 찾을 수 없습니다.");
    }

//    UsernamePasswordAuthenticationToken authenticationToken =
//        new UsernamePasswordAuthenticationToken(requestDto.getNickname(), requestDto.getPassword());
//    Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

    TokenDto tokenDto = tokenProvider.generateTokenDto(member);
    tokenToHeaders(tokenDto, response);

    return ResponseDto.success(
        MemberResponseDto.builder()
            .id(member.getId())
            .nickname(member.getNickname())
            .createdAt(member.getCreatedAt())
            .modifiedAt(member.getModifiedAt())
            .build()
    );
  }

  public ResponseDto<?> logout(HttpServletRequest request) {
    if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
      return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
    }
    Member member = tokenProvider.getMemberFromAuthentication();
    if (null == member) {
      return ResponseDto.fail("MEMBER_NOT_FOUND",
          "사용자를 찾을 수 없습니다.");
    }

    return tokenProvider.deleteRefreshToken(member);
  }

  @Transactional(readOnly = true)
  public Member isPresentMember(String nickname) {
    Optional<Member> optionalMember = memberRepository.findByNickname(nickname);
    return optionalMember.orElse(null);
  }

  public void tokenToHeaders(TokenDto tokenDto, HttpServletResponse response) {
    response.addHeader("Authorization", "Bearer " + tokenDto.getAccessToken());
    response.addHeader("Refresh-Token", tokenDto.getRefreshToken());
    response.addHeader("Access-Token-Expire-Time", tokenDto.getAccessTokenExpiresIn().toString());
  }

  // 마이 페이지
  @Transactional
  public ResponseDto<?> myPage(HttpServletRequest request) {
    if (null == request.getHeader("Refresh-Token")) {
      return ResponseDto.fail("NOT_FOUND", "로그인이 필요합니다.");
    }
    if (null == request.getHeader("Authorization")) {
      return ResponseDto.fail("NOT_FOUND", "로그인이 필요합니다.");
    }
    Member member = validateMember(request);
    if (null == member) {
      return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
    }

    // Post
    List<Post> postList = postRepository.findByMemberId(member.getId());
    List<PostResponseDto> postResponseDtoList = new ArrayList<>();

    for (Post post : postList) {
      postResponseDtoList.add(
              PostResponseDto.builder()
                      .id(post.getId())
                      .title(post.getTitle())
                      .content(post.getContent())
                      .author(post.getMember().getNickname())
                      .likes(post.getTotalPostLike())
                      .createdAt(post.getCreatedAt())
                      .modifiedAt(post.getModifiedAt())
                      .build()
      );
    }

    // Comment/Sub-Comment
    List<Comment> commentList = commentRepository.findByMemberId(member.getId());
    List<CommentResponseDto> commentResponseDtoList = new ArrayList<>();

    for (Comment comment : commentList) {
      commentResponseDtoList.add(
              CommentResponseDto.builder()
                      .id(comment.getId())
                      .content(comment.getContent())
                      .author(comment.getMember().getNickname())
                      .subCommentList(subCommentRepository.findByMemberIdAndCommentId(member.getId(), comment.getId()).stream().map(SubCommentResponseDto::new).collect(Collectors.toList()))
                      .likes(comment.getTotalCommentLike())
                      .createdAt(comment.getCreatedAt())
                      .modifiedAt(comment.getModifiedAt())
                      .build()
      );
    }


    // Post 좋아요
    List<PostLike> postLikeIdList = postLikeRepository.findByPostLikeId_MemberId(member.getId());
    List<PostLikeResponseDto>  postLikeResponseDtoList = new ArrayList<>();

    for (PostLike postLike: postLikeIdList) {
      postLikeResponseDtoList.add(
              PostLikeResponseDto.builder()
                      .postId(postLike.getPostLikeId().getPost().getId())
                      .title(postLike.getPostLikeId().getPost().getTitle())
                      .content(postLike.getPostLikeId().getPost().getContent())
                      .author(postLike.getPostLikeId().getMember().getNickname())
                      .build()
      );
    }

    // Comment 좋아요
    List<CommentLike> commentLikeIdList = commentLikeRepository.findAllByCommentLikeId_MemberId(member.getId());
    List<CommentLikeResponseDto> commentLikeResponseDtoList = new ArrayList<>();

    for (CommentLike commentLikeId : commentLikeIdList ) {
      commentLikeResponseDtoList.add(
              CommentLikeResponseDto.builder()
                      .commentId(commentLikeId.getCommentLikeId().getComment().getId())
                      .content(commentLikeId.getCommentLikeId().getComment().getContent())
                      .build()
      );
    }

    return ResponseDto.success(
            MyPageResponseDto.builder()
                    .nickname(member.getNickname())
                    .postList(postResponseDtoList)
                    .commentList(commentResponseDtoList)
                    .postLikes(postLikeResponseDtoList)
                    .commentLikes(commentLikeResponseDtoList)
                    .build()
    );
  }

  @Transactional
  public Member validateMember(HttpServletRequest request) {
    if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
      return null;
    }
    return tokenProvider.getMemberFromAuthentication();
  }

}
