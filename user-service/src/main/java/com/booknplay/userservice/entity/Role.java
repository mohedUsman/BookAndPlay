package com.booknplay.userservice.entity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id", updatable = false, nullable = false)
    private Integer id;

    @Column(name = "name", unique = true, nullable = false, length = 100)
    private String name;

}
