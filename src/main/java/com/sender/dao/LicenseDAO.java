package com.sender.dao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@Table(name = "license")
@AllArgsConstructor
@NoArgsConstructor
public class LicenseDAO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    public LicenseDAO(String name) {
        this.name = name;
    }
}
