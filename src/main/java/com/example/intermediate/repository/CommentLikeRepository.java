package com.example.intermediate.repository;

import com.example.intermediate.domain.Comment;
import com.example.intermediate.domain.like.CommentLike;
import com.example.intermediate.domain.like.CommentLikeId;
import com.example.intermediate.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentLikeRepository extends JpaRepository<CommentLike, CommentLikeId> {
//    List<CommentLike> findAllByCommentLikeId_Comment(Comment comment);
//    List<CommentLike> findAllByCommentLikeId_Member(Member member);

    List<CommentLike> findAllByCommentLikeId_MemberId(Long memberId);


//    Optional<CommentLikeId> findCommentLikeByMemberAndCommentId(Long memberId, Long commentId);
//    List<CommentLikeId> findCommentLikeByMemberId(Long memberId);
}
