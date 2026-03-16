package com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.authentication;

import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.Role;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.Permission;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, Integer> {
    boolean existsByRoleAndPermission(Role role, Permission permission);

    @Query("select p.permissionCode from RolePermission rp join rp.permission p where rp.role.id = :roleId")
    List<String> findPermissionCodesByRoleId(@Param("roleId") Integer roleId);
}
