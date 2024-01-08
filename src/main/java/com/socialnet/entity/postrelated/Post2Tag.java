package com.socialnet.entity.postrelated;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "post2tag")
public class Post2Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "post_id", foreignKey = @ForeignKey(name = "fk_post2tag_post"))
    private Post post;

    @ManyToOne
    @JoinColumn(name = "tag_id", foreignKey = @ForeignKey(name = "fk_post2tag_tag"))
    private Tag tag;
}

