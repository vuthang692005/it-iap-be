package com.example.test.config;

import com.example.test.entity.Permission;
import com.example.test.entity.Role;
import com.example.test.entity.User;
import com.example.test.repository.PermissionRepository;
import com.example.test.repository.RoleRepository;
import com.example.test.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Configuration
@Slf4j(topic = "ApplicationInitConfig")
@RequiredArgsConstructor
public class ApplicationInitConfig {
    private final PasswordEncoder passwordEncoder;

    @Value("${app.security.admin-full-permission:true}")
    private boolean adminFullPermission;

    @Bean
    @Transactional
    ApplicationRunner applicationRunner(
            UserRepository userRepository, RoleRepository roleRepository, PermissionRepository permissionRepository){
        return args -> {
            Set<String> initRoles = Set.of("USER","ADMIN");
            Set<String> initPermissions = Set.of("USER_CREATE", "USER_UPDATE");

            initRoles.forEach(initRole -> {
                    if (!roleRepository.existsById(initRole)) {
                        Role role = new Role();
                        role.setName(initRole);
                        roleRepository.save(role);

                        log.info("Đã tạo role '{}'", initRole);
                    }
                }
            );

            initPermissions.forEach(initPermission -> {
                if(!permissionRepository.existsById(initPermission)){
                    Permission permissionEntity = new Permission();
                    permissionEntity.setName(initPermission);
                    permissionRepository.save(permissionEntity);

                    log.info("Đã tạo permission '{}'", initPermission);
                }
            });

            Optional<Role> roleAdmin = roleRepository.findById("ADMIN");
            if(roleAdmin.isEmpty()){
                log.error("Không tìm thấy role ADMIN trong cơ sở dữ liệu");
                return;
            }

            if(!userRepository.existsByUsername("admin")){
                Set<Role> roles = new HashSet<>();
                roles.add(roleAdmin.get());

                User user = new User();
                user.setRoles(roles);
                user.setUsername("admin");
                user.setPassword(passwordEncoder.encode("admin12345"));
                user.setFullName("admin");
                user.setEmail("vumitha2005@gmail.com");

                userRepository.save(user);

                log.info("Người dùng admin đã được tạo với mật khẩu mặc định: admin12345, vui lòng đổi mật khẩu");
            }

            if(adminFullPermission) {
                Set<Permission> dbPermissions = new HashSet<>(permissionRepository.findAll());
                roleAdmin.get().setPermissions(dbPermissions);
                roleRepository.save(roleAdmin.get());
                log.info("Đã gán tất cả quyền cho role ADMIN");
            }
        };
    }
}
