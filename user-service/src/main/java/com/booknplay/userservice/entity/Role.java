package com.booknplay.userservice.entity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Integer id;

    @Column(name = "name", unique = true)
    private String name;

}
