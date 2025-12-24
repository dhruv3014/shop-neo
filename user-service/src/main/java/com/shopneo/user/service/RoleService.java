package com.shopneo.user.service;

import com.shopneo.user.dto.request.CreateRoleRequest;
import com.shopneo.user.entities.Role;
import com.shopneo.user.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoleService {

  private final RoleRepository roleRepository;

  public Role createRole(CreateRoleRequest request) {
    if (roleRepository.existsByName(request.getName())) {
        throw new IllegalStateException("Role already exist");
    }

    Role newRole = new Role();
        newRole.setName(request.getName());
        newRole.setCreatedAt(Instant.now());

    return roleRepository.save(newRole);
  }

}
