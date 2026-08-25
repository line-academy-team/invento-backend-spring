package com.lineacademy.inventobackendspring.repository;

import com.lineacademy.inventobackendspring.domain.department.Department;
import com.lineacademy.inventobackendspring.domain.enums.MemberRole;
import com.lineacademy.inventobackendspring.domain.enums.MemberStatus;
import com.lineacademy.inventobackendspring.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByUserId(Long userId);

    boolean existsByUserIdAndOrganizationIdAndStatus(Long userId, Long organizationId, MemberStatus status);

    boolean existsByUserIdAndStatusIn(Long userId, List<MemberStatus> statuses);

    Optional<Member> findFirstByUserIdAndStatus(Long userId, MemberStatus status);

    Optional<Member> findFirstByUserIdAndOrganizationIdAndStatusAndRoleIn(
            Long userId,
            Long organizationId,
            MemberStatus status,
            List<MemberRole> roles
    );

    Optional<Member> findFirstByUserIdAndStatusAndRoleIn(Long userId, MemberStatus memberStatus, List<MemberRole> list);

    @Query("SELECT m FROM Member m " +
            "WHERE m.organization.id = :orgId " +
            "AND m.status = :status " +
            "AND (:search IS NULL " +
            "    OR LOWER(m.user.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "    OR LOWER(m.department.name) LIKE LOWER(CONCAT('%', :search, '%'))" +
            ") ORDER BY m.user.name ASC")
    List<Member> findOrgMembersWithSearch(
            @Param("orgId") Long orgId,
            @Param("status") MemberStatus status,
            @Param("search") String search
    );

    // Prisma의 updateMany를 대체하는 일괄 업데이트(Bulk Update)
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Member m SET m.department = :department " +
            "WHERE m.id IN :memberIds AND m.organization.id = :orgId AND m.status = 'APPROVED'")
    void updateDepartmentForMembers(
            @Param("department") Department department,
            @Param("memberIds") List<Long> memberIds,
            @Param("orgId") Long orgId
    );

    @Query("SELECT m FROM Member m " +
            "WHERE m.organization.id = :orgId " +
            "AND (:search IS NULL " +
            "    OR LOWER(m.user.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "    OR LOWER(m.user.email) LIKE LOWER(CONCAT('%', :search, '%'))" +
            ") ORDER BY m.createdAt DESC")
    List<Member> findOrgJoinRequestsWithSearch(@Param("orgId") Long orgId, @Param("search") String search);

    Optional<Member> findByIdAndOrganizationId(Long id, Long organizationId);

    List<Member> findByDepartmentIdAndRole(Long departmentId, MemberRole role);

    // 부서 삭제 전 멤버들의 소속 부서를 null로 초기화하는 벌크 연산
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Member m SET m.department = null WHERE m.department.id = :departmentId")
    void updateDepartmentIdToNull(@Param("departmentId") Long departmentId);
}