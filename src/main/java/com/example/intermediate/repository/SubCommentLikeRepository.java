package com.example.intermediate.repository;

import com.example.intermediate.domain.Comment;
import com.example.intermediate.domain.Member;
import com.example.intermediate.domain.SubComment;
import com.example.intermediate.domain.like.CommentLike;
import com.example.intermediate.domain.like.CommentLikeId;
import com.example.intermediate.domain.like.SubCommentLike;
import com.example.intermediate.domain.like.SubCommentLikeId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubCommentLikeRepository extends JpaRepository<SubCommentLike, SubCommentLikeId> {
    List<SubCommentLike> findAllBySubCommentLikeId_SubComment(SubComment subComment);
    List<SubCommentLike> findAllBySubCommentLikeId_Member(Member member);
}
