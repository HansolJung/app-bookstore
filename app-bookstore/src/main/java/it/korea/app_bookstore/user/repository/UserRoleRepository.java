package it.korea.app_bookstore.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import it.korea.app_bookstore.user.entity.UserRoleEntity;

public interface UserRoleRepository extends JpaRepository<UserRoleEntity, String> {

}
