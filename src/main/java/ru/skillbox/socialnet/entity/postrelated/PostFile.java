package ru.skillbox.socialnet.entity.postrelated;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "post_files")
public class PostFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name")
    private String name;

    @Column(name = "path")
    private String path;

    @ManyToOne
    @JoinColumn(name = "post_id",
            nullable = false,
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_file_post"))
    private Post post;
}