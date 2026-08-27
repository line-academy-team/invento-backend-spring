package com.lineacademy.inventobackendspring.domain.user;

import com.lineacademy.inventobackendspring.domain.common.BaseTimeEntity;
import com.lineacademy.inventobackendspring.domain.enums.UserRole;
import com.lineacademy.inventobackendspring.domain.member.Member;
import com.lineacademy.inventobackendspring.domain.organization.Organization;
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

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private Member member;

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

    public void updateName(String name) {
        this.name = name;
    }

    public void updateImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void updatePasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void updateRole(UserRole role) {
        this.role = role;
    }
}
