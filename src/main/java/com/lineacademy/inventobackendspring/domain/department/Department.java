package com.lineacademy.inventobackendspring.domain.department;

import com.lineacademy.inventobackendspring.domain.common.BaseTimeEntity;
import com.lineacademy.inventobackendspring.domain.equipment.Equipment;
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
@Table(name = "departments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Department extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @OneToMany(mappedBy = "department")
    private List<Member> users = new ArrayList<>();

    @OneToMany(mappedBy = "department")
    private List<Equipment> equipment = new ArrayList<>();

    @Builder
    public Department(String name, String description, Organization organization) {
        this.name = name;
        this.description = description;
        this.organization = organization;
    }
}
