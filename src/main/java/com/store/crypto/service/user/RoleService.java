package com.store.crypto.service.user;

import com.store.crypto.dto.user.role.request.AddRoleDTO;
import org.springframework.http.ResponseEntity;

public interface RoleService {
    ResponseEntity<Object> createRole(AddRoleDTO roleDTO);

    ResponseEntity<Object> updateRole(Integer id, AddRoleDTO roleDTO);

    ResponseEntity<Object> getRoleById(Integer id);

    ResponseEntity<Object> getAllRoles();

    ResponseEntity<Object> deleteRole(Integer id);
}
