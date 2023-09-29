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
@Table(name = "currencies")
public class Currency {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /** Наименование */
  @Column(name = "name")
  private String name;

  /** Цена */
  @Column(name = "price")
  private String price;

  /** Дата и время обновления */
  @Column(name = "update_time")
  private String updateTime;

}