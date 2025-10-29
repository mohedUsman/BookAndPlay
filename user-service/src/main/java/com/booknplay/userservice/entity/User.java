package com.booknplay.userservice.entity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    //@Column(columnDefinition = "char(36)")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "phone", length = 10)
    private String phone;

    @Column(name="created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

//    @ManyToMany(fetch = FetchType.EAGER)
//    @JoinTable(
//            name="roles",
//            joinColumns = @JoinColumn(name = "user_id"),
//            inverseJoinColumns = @JoinColumn(name="role_id")
//    )
//    private Set<Role> roles = new HashSet<>();
   @ManyToMany(fetch = FetchType.EAGER)
   @JoinTable(
        name = "user_roles",                  // distinct join table
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
   )
   private Set<Role> roles = new HashSet<>();

    @PreUpdate
    public void setLastUpdate(){
        this.updatedAt = LocalDateTime.now();
    }

}
