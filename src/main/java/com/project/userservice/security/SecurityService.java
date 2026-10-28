package com.project.userservice.security;

import com.project.userservice.config.SecurityUtils;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component("securityService")
public class SecurityService {

    public boolean isOwner(UUID id) {
        if (id == null) {
            return false;
        }
        String currentUserId = SecurityUtils.getCurrentUserId();
        return id.toString().equals(currentUserId);
    }
}