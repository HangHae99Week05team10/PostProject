package com.example.intermediate.repository;

import com.example.intermediate.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface PostLikeRepository extends JpaRepository<PostLike, PostLikeId> {
    List<PostLike> findAllByPostLikeId_Member(Member member);
    List<PostLike> findAllByPostLikeId_Post(Post post);
}
