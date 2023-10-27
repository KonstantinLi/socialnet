package ru.skillbox.socialnet.entity.locationrelated;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "countries")
public class Country {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    /**
     * ID внешнего Апи
     */
    @Column(name = "external_id")
    private Long externalId;

}