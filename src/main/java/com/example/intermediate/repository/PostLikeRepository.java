package com.example.intermediate.repository;

import com.example.intermediate.domain.Comment;
import com.example.intermediate.domain.PostLike;
import com.example.intermediate.domain.PostLikeId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostLikeRepository extends JpaRepository<PostLike, PostLikeId> {
    List<Comment> findAllByMember();
    List<Comment> findAllByPost();
}
