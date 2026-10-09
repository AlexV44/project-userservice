package com.project.userservice.repository.specification;

import com.project.userservice.entity.User;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserSpecification {

    public static Specification<User> hasName(String name) {
        return (root, query, cb) -> !StringUtils.hasText(name)
                ? null
                : cb.equal(cb.lower(root.get("name")), name.trim().toLowerCase());
    }

    public static Specification<User> hasSurname(String surname) {
        return (root, query, cb) -> !StringUtils.hasText(surname)
                ? null
                : cb.equal(cb.lower(root.get("surname")), surname.trim().toLowerCase());
    }
}
