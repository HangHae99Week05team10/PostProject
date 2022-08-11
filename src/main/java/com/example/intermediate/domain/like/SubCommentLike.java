package com.example.intermediate.domain.like;


import com.example.intermediate.domain.Timestamped;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SubCommentLike extends Timestamped {
    @EmbeddedId
    private SubCommentLikeId subCommentLikeId;
}
