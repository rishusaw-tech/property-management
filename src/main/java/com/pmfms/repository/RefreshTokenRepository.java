package com.pmfms.repository;

import com.pmfms.entity.RefreshToken;
import com.pmfms.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    @Modifying
    void deleteByToken(String token);

    @Modifying
    void deleteByUser(User user);
}
