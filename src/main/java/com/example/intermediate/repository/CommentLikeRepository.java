package com.example.intermediate.repository;

import com.example.intermediate.domain.Comment;
import com.example.intermediate.domain.CommentLike;
import com.example.intermediate.domain.CommentLikeId;
import com.example.intermediate.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentLikeRepository extends JpaRepository<CommentLike, CommentLikeId> {
    List<CommentLike> findAllByCommentLikeId_Comment(Comment comment);
    List<CommentLike> findAllByCommentLikeId_Member(Member member);
}
