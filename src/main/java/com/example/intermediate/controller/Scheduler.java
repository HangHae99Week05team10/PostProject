package com.example.intermediate.controller;


import com.example.intermediate.domain.Comment;
import com.example.intermediate.domain.Post;
import com.example.intermediate.repository.PostRepository;
import com.example.intermediate.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Component
public class Scheduler {
    private  final PostRepository postRepository;

    @Scheduled(cron = "0 0/1 * * * *")
    @Transactional
    public void autoDeleteNoUsePost() throws InterruptedException {
        System.out.println("댓글 없는 게시글 삭제");

        List<Post> postList = postRepository.findAll();

        if (postList.size() != 0) {
            for (Post post : postList) {
                // 1초마다 한 게시글 씩 조회합니다.
                TimeUnit.SECONDS.sleep(1);
                // i 번째 게시글 꺼냅니다.
                // i 번째 게시글을 대상으로 댓글 유무 확인합니다.
                List<Comment> commentList = post.getComments();
                if (commentList.size() == 0) {
                    postRepository.delete(post);
                    System.out.println("게시글"+post.getId()+"에 댓글이 존재하지 않아 삭제 처리 되었습니다.");
                }
            }
        }
    }
}
