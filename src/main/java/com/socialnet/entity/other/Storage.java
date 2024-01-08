package com.socialnet.entity.other;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "storage")
@Schema(description = "Entity Storage information")
public class Storage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "owner_id")
    private Long ownerId;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_size")
    private long fileSize;

    @Column(name = "file_type")
    private String fileType;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

}