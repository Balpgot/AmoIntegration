package com.sender.dao;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@Table(name = "cpo")
@NoArgsConstructor
public class CPODAO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    public CPODAO(String name) {
        this.name = name;
    }
}
