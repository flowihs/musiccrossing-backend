package ru.github.musiccrossing.auth.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.github.musiccrossing.auth.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .map(UserDetailsImpl::new)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не был найден"));
    }

    public UserDetails loadUserById(Long id) {
        return userRepository.findById(id)
                .map(UserDetailsImpl::new)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не был найден"));
    }
}
