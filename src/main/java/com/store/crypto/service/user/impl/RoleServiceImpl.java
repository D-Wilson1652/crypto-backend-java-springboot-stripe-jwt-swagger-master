package com.store.crypto.service.user.impl;

import com.store.crypto.dto.generic.GenericResponse;
import com.store.crypto.dto.user.role.request.AddRoleDTO;
import com.store.crypto.dto.user.role.response.RoleResponseDTO;
import com.store.crypto.model.user.Role;
import com.store.crypto.repository.user.RoleRepository;
import com.store.crypto.service.user.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    public ResponseEntity<Object> createRole(AddRoleDTO roleDTO) {
        GenericResponse response = new GenericResponse();
        try {
            if (roleRepository.findByName(roleDTO.getName()).isPresent()) {
                response.setData(null);
                response.setMessage("Role already exists.");
                response.setStatusCode(HttpStatus.BAD_REQUEST.value());
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            Role role = new Role(roleDTO.getName());
            Role savedRole = roleRepository.save(role);

            RoleResponseDTO roleResponseDTO = RoleResponseDTO.builder()
                    .id(savedRole.getId())
                    .name(savedRole.getName())
                    .build();
            response.setData(roleResponseDTO);
            response.setMessage("Role created successfully.");
            response.setStatusCode(HttpStatus.OK.value());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setData(null);
            response.setMessage(e.getMessage());
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<Object> updateRole(Integer id, AddRoleDTO roleDTO) {
        GenericResponse response = new GenericResponse();
        try {
            Optional<Role> existingRole = roleRepository.findById(id);
            if (existingRole.isEmpty()) {
                response.setData(null);
                response.setMessage("Role not found.");
                response.setStatusCode(HttpStatus.BAD_REQUEST.value());
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            Optional<Role> roleWithSameName = roleRepository.findByName(roleDTO.getName());
            if (roleWithSameName.isPresent() && !existingRole.get().getId().equals(roleWithSameName.get().getId())) {
                response.setData(null);
                response.setMessage("Role with this name already exists.");
                response.setStatusCode(HttpStatus.BAD_REQUEST.value());
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            existingRole.get().setName(roleDTO.getName());
            roleRepository.save(existingRole.get());

            RoleResponseDTO roleResponseDTO = RoleResponseDTO.builder()
                    .id(existingRole.get().getId())
                    .name(existingRole.get().getName())
                    .build();
            response.setData(roleResponseDTO);
            response.setMessage("Role updated successfully.");
            response.setStatusCode(HttpStatus.OK.value());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setData(null);
            response.setMessage(e.getMessage());
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<Object> getRoleById(Integer id) {
        GenericResponse response = new GenericResponse();
        try {
            Optional<Role> role = roleRepository.findById(id);
            if (role.isPresent()) {
                RoleResponseDTO roleResponseDTO = RoleResponseDTO.builder()
                        .id(role.get().getId())
                        .name(role.get().getName())
                        .build();
                response.setData(roleResponseDTO);
                response.setMessage("Role retrieved successfully.");
                response.setStatusCode(HttpStatus.OK.value());
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                response.setData(null);
                response.setMessage("Role not found.");
                response.setStatusCode(HttpStatus.BAD_REQUEST.value());
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            response.setData(null);
            response.setMessage(e.getMessage());
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<Object> getAllRoles() {
        GenericResponse response = new GenericResponse();
        try {
            List<Role> roles = roleRepository.findAll();
            if (roles.isEmpty()) {
                response.setData(null);
                response.setMessage("No roles found.");
                response.setStatusCode(HttpStatus.BAD_REQUEST.value());
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            } else {

                List<RoleResponseDTO> roleResponseDTOS = roles.stream().map(role -> RoleResponseDTO.builder()
                        .id(role.getId())
                        .name(role.getName())
                        .build()).collect(Collectors.toList());
                response.setData(roleResponseDTOS);
                response.setMessage("Roles retrieved successfully.");
                response.setStatusCode(HttpStatus.OK.value());
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        } catch (Exception e) {
            response.setData(null);
            response.setMessage(e.getMessage());
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<Object> deleteRole(Integer id) {
        GenericResponse response = new GenericResponse();
        response.setData(null);
        try {
            Optional<Role> role = roleRepository.findById(id);
            if (role.isEmpty()) {
                response.setMessage("Role doesn't exist that you want to delete.");
                response.setStatusCode(HttpStatus.BAD_REQUEST.value());
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            } else {
                roleRepository.delete(role.get());
                response.setMessage("Role deleted successfully.");
                response.setStatusCode(HttpStatus.OK.value());
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        } catch (Exception e) {
            response.setMessage(e.getMessage());
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
