package ru.skillbox.socialnet.entity.postrelated;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.skillbox.socialnet.entity.personrelated.Person;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "is_blocked")
    private boolean isBlocked;

    @Column(name = "is_deleted")
    private boolean isDeleted;

    @Column(name = "time")
    private LocalDateTime time;

    @Column(name = "time_delete")
    private LocalDateTime timeDelete;

    @Column(name = "title")
    private String title;

    @Column(name = "post_text", columnDefinition = "text")
    private String postText;

    @ManyToOne
    @JoinColumn(name = "author_id",
            nullable = false,
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_person"))
    private Person author;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "post2tag",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private List<Tag> tags = new ArrayList<>();

    /**
     * Файлы в посте
     */
    @OneToMany(mappedBy = "post",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private List<PostFile> files;
}