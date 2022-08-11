package com.example.intermediate.repository;

import com.example.intermediate.domain.Member;
import com.example.intermediate.domain.Post;
import com.example.intermediate.domain.like.PostLike;
import com.example.intermediate.domain.like.PostLikeId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostLikeRepository extends JpaRepository<PostLike, PostLikeId> {
//    List<PostLike> findAllByPostLikeId_Member(Member member);
//    List<PostLike> findAllByPostLikeId_Post(Post post);

    List<PostLike> findByPostLikeId_MemberId(Long memberId);
}
