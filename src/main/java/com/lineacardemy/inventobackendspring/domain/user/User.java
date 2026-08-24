package com.lineacardemy.inventobackendspring.domain.user;

import com.lineacardemy.inventobackendspring.domain.common.BaseTimeEntity;
import com.lineacardemy.inventobackendspring.domain.enums.UserRole;
import com.lineacardemy.inventobackendspring.domain.organization.Organization;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private String name;

    @Column(name = "image_url")
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role = UserRole.USER;

    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL)
    private List<Organization> organizations = new ArrayList<>();

    // TODO : Member와 관계 작성

    @Builder
    private User(
            String email,
            String passwordHash,
            String name,
            String imageUrl,
            UserRole role
    ) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.name = name;
        this.imageUrl = imageUrl;
        if (role != null) this.role = role;
    }
}
