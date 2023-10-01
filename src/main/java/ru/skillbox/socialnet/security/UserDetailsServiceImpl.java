package ru.skillbox.socialnet.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.skillbox.socialnet.entity.personrelated.Person;
import ru.skillbox.socialnet.repository.PersonRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final PersonRepository personRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        try {
            Person person = personRepository.findByEmail(email)
                    .orElseThrow(() ->
                            new UsernameNotFoundException(String.format(
                                    "Пользователь с email %s не найден", email))
                    );
            return new User(
                    person.getId() + "," + person.getEmail(),
                    person.getPassword(),
                    List.of(new SimpleGrantedAuthority("ROLE_USER"))
            );
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Переданное Id пользователя не является числом");
        }
    }
}
