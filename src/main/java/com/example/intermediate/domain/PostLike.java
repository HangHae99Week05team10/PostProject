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
public class PostLike extends Timestamped{
    @EmbeddedId
    private PostLikeId postLikeId;



}
