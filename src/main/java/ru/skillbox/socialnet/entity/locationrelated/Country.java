package ru.skillbox.socialnet.entity.locationrelated;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "countries")
public class Country {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    /**
     * Наименование
     */
    private String name;

    /**
     * Полное наименование
     */
    @Column(name = "full_name")
    private String fullName;

    /**
     * Код страны
     */
    @Column(name = "code2")
    private String code2;

    /**
     * Международное наименование
     */
    @Column(name = "international_name")
    private String internationalName;


}