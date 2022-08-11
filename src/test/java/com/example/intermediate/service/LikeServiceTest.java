package com.example.intermediate.service;

import com.example.intermediate.controller.response.ResponseDto;
import com.example.intermediate.domain.*;
import com.example.intermediate.domain.like.*;
import com.example.intermediate.jwt.TokenProvider;
import com.example.intermediate.repository.*;
import lombok.Getter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class LikeServiceTest {
    static PostAndCommentCreator creator = new PostAndCommentCreator();
    static Member member;
    static Post post;
    static Comment comment;
    static SubComment subComment;

    @Mock
    private PostLikeRepository postLikeRepository;
    @Mock
    private CommentLikeRepository commentLikeRepository;
    @Mock
    private SubCommentLikeRepository subCommentLikeRepository;
    @Mock
    private PostRepository postRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private SubCommentRepository subCommentRepository;
    @Mock
    private TokenProvider tokenProvider;


    @BeforeAll
    static void createPostAndComment(){
        creator.create();
        member = creator.getMember();
        post = creator.getPost();
        comment = creator.getComment();
        subComment = creator.getSubComment();
    }

    @Test
    void likePost() {
        PostLikeId postLikeId = new PostLikeId(post, member);
        when(postRepository.findById(any())).thenReturn(Optional.of(post));
        when(postLikeRepository.findById(postLikeId)).thenReturn(Optional.empty());
        LikeService likeService = new LikeService(postLikeRepository,commentLikeRepository,subCommentLikeRepository,postRepository,commentRepository,subCommentRepository,tokenProvider);
        //좋아요
        ResponseDto<?> response = likeService.like(post.getId(), member, LikeService.ParentType.POST);
        assertEquals(response.getData(), "like success");
    }
    @Test
    void unlikePost() {
        PostLikeId postLikeId = new PostLikeId(post, member);
        when(postRepository.findById(any())).thenReturn(Optional.of(post));
        when(postLikeRepository.findById(postLikeId)).thenReturn(Optional.of(new PostLike()));
        LikeService likeService = new LikeService(postLikeRepository,commentLikeRepository,subCommentLikeRepository,postRepository,commentRepository,subCommentRepository,tokenProvider);
        //좋아요 취소
        ResponseDto<?> response1 = likeService.like(post.getId(), member, LikeService.ParentType.POST);
        assertEquals(response1.getData(), "unlike success");
    }

    @Test
    void likeComment() {
        CommentLikeId commentLikeId = new CommentLikeId(comment, member);
        when(commentRepository.findById(any())).thenReturn(Optional.of(comment));
        when(commentLikeRepository.findById(commentLikeId)).thenReturn(Optional.empty());
        LikeService likeService = new LikeService(postLikeRepository,commentLikeRepository,subCommentLikeRepository,postRepository,commentRepository,subCommentRepository,tokenProvider);
        //좋아요
        ResponseDto<?> response = likeService.like(comment.getId(), member, LikeService.ParentType.COMMENT);
        assertEquals(response.getData(), "like success");
    }

    @Test
    void unlikeComment() {
        CommentLikeId commentLikeId = new CommentLikeId(comment, member);
        when(commentRepository.findById(any())).thenReturn(Optional.of(comment));
        when(commentLikeRepository.findById(commentLikeId)).thenReturn(Optional.of(new CommentLike()));
        LikeService likeService = new LikeService(postLikeRepository,commentLikeRepository,subCommentLikeRepository,postRepository,commentRepository,subCommentRepository,tokenProvider);
        //좋아요 취소
        ResponseDto<?> response = likeService.like(comment.getId(), member, LikeService.ParentType.COMMENT);
        assertEquals(response.getData(), "unlike success");
    }

    @Test
    void likeSubComment() {
        SubCommentLikeId subCommentLikeId = new SubCommentLikeId(subComment, member);
        when(subCommentRepository.findById(any())).thenReturn(Optional.of(subComment));
        when(subCommentLikeRepository.findById(subCommentLikeId)).thenReturn(Optional.empty());
        LikeService likeService = new LikeService(postLikeRepository,commentLikeRepository,subCommentLikeRepository,postRepository,commentRepository,subCommentRepository,tokenProvider);
        //좋아요
        ResponseDto<?> response = likeService.like(subComment.getId(), member, LikeService.ParentType.SUBCOMMENT);
        assertEquals(response.getData(), "like success");
    }

    @Test
    void unlikeSubComment() {
        SubCommentLikeId subCommentLikeId = new SubCommentLikeId(subComment, member);
        when(subCommentRepository.findById(any())).thenReturn(Optional.of(subComment));
        when(subCommentLikeRepository.findById(subCommentLikeId)).thenReturn(Optional.of(new SubCommentLike()));
        LikeService likeService = new LikeService(postLikeRepository,commentLikeRepository,subCommentLikeRepository,postRepository,commentRepository,subCommentRepository,tokenProvider);
        //좋아요 취소
        ResponseDto<?> response = likeService.like(subComment.getId(), member, LikeService.ParentType.SUBCOMMENT);
        assertEquals(response.getData(), "unlike success");
    }
}

@Getter
class PostAndCommentCreator{

    private Member member;
    private Post post;
    private Comment comment;
    private SubComment subComment;

    public void create(){
        this.member = Member.builder()
                .id(1L)
                .nickname("likeServiceTest")
                .password("qwerasdf")
                .build();

        this.post = Post.builder()
                .id(1L)
                .member(member)
                .title("likeServiceTest")
                .content("likeServiceTest")
                .build();

        this.comment = Comment.builder()
                .id(1L)
                .member(member)
                .content("likeServiceTest")
                .post(post)
                .build();

        this.subComment = SubComment.builder()
                .id(1L)
                .member(member)
                .content("likeServiceTest")
                .comment(comment)
                .build();
    }

}