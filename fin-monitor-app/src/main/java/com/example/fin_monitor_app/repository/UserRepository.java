package com.example.fin_monitor_app.repository;

import com.example.fin_monitor_app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.login = :login")
    boolean existsByLogin(@Param("login") String login);

    @Query("SELECT u FROM User u WHERE u.login = :login AND u.password = :password")
    Optional<User> findByLoginAndPassword(@Param("login") String login, @Param("password") String password);

    Optional<User> findByLogin(java.lang.String login);
}
