package ru.skillbox.socialnet.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skillbox.socialnet.data.entity.Person;
import ru.skillbox.socialnet.repository.PersonRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final PersonRepository personRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        try {
            Person person = personRepository.findByEmail(email).orElseThrow(
                    () -> new UsernameNotFoundException(String.format("User with email %s is not found", email))
            );
            return new User(
                    person.getId() + "," + person.getEmail(),
                    person.getPassword(),
                    List.of(new SimpleGrantedAuthority("ROLE_USER"))
            );
        } catch (NumberFormatException ex) {
            throw new RuntimeException("User's id should have numeric format");
        }
    }
}
