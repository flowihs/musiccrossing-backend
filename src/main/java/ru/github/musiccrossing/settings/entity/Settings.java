package ru.github.musiccrossing.settings.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "settings")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Settings {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
}
