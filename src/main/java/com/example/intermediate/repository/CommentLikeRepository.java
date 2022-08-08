package com.example.intermediate.repository;

import com.example.intermediate.domain.Comment;
import com.example.intermediate.domain.CommentLike;
import com.example.intermediate.domain.CommentLikeId;
import com.example.intermediate.domain.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentLikeRepository extends JpaRepository<CommentLike, CommentLikeId> {
    List<Comment> findAllByCommentLikeId_Comment();
    List<Comment> findAllByCommentLikeId_Member();
    Optional<Comment> findByCommentLikeId();
}
