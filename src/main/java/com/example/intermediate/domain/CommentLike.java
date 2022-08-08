package com.example.intermediate.domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentLike extends Timestamped{
    @EmbeddedId
    private CommentLikeId commentLikeId;
}
