package com.store.crypto.controller.user;


import com.store.crypto.dto.user.role.request.AddRoleDTO;
import com.store.crypto.service.user.RoleService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@CrossOrigin(origins = "*")

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @SecurityRequirement(name = "Authorization")
    @PostMapping("/create")
    public ResponseEntity<Object> createRole(@RequestBody AddRoleDTO roleDTO) {
        return roleService.createRole(roleDTO);
    }

    @SecurityRequirement(name = "Authorization")
    @PutMapping("/update/{id}")
    public ResponseEntity<Object> updateRole(@PathVariable Integer id, @RequestBody AddRoleDTO roleDTO) {
        return roleService.updateRole(id, roleDTO);
    }

    @SecurityRequirement(name = "Authorization")
    @GetMapping("/get/{id}")
    public ResponseEntity<Object> getRoleById(@PathVariable Integer id) {
        return roleService.getRoleById(id);
    }

    @SecurityRequirement(name = "Authorization")
    @GetMapping("/list")
    public ResponseEntity<Object> getAllRoles() {
        return roleService.getAllRoles();
    }

    @SecurityRequirement(name = "Authorization")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Object> deleteRole(@PathVariable Integer id) {
        return roleService.deleteRole(id);
    }
}

