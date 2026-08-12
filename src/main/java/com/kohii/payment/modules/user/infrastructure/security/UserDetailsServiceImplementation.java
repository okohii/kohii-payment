package com.kohii.payment.modules.user.infrastructure.security;

import com.kohii.payment.modules.user.infrastructure.persistence.entities.UserJPA;
import com.kohii.payment.modules.user.infrastructure.persistence.repositories.UserJpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImplementation implements UserDetailsService {

    private final UserJpaRepository userJpaRepository;

    public UserDetailsServiceImplementation(UserJpaRepository userJpaRepository) {
        this.userJpaRepository = userJpaRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserJPA user = userJpaRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return user;
    }
}
