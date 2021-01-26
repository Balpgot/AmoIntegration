package com.sender.dao;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;

@Entity
@Data
@Table(name = "okved")
@AllArgsConstructor
@NoArgsConstructor
public class OKVEDDAO {
    @Id
    private String id;
    private String name;
}
