package kz.projects.ams.services.impl;

import kz.projects.ams.models.User;
import kz.projects.ams.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Реализация {@link UserDetailsService} для загрузки деталей пользователя по имени пользователя.
 * Использует {@link UserRepository} для поиска пользователя по email.
 */
@Service
public class MyUserDetailsService implements UserDetailsService {
  @Autowired
  private UserRepository userRepository;

  /**
   * Загружает {@link UserDetails} по указанному имени пользователя.
   * Если пользователь не найден, выбрасывает {@link UsernameNotFoundException}.
   *
   * @param username имя пользователя (email)
   * @return {@link UserDetails} объект, представляющий найденного пользователя
   * @throws UsernameNotFoundException если пользователь с указанным email не найден
   */
  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Optional<User> userOptional = userRepository.findByEmail(username);
    if (userOptional.isEmpty()){
      throw new UsernameNotFoundException("Username not found");
    }

    return userOptional.get();
  }
}
