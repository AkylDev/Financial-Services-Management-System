package kz.projects.ams.services.impl;

import kz.projects.ams.models.User;
import kz.projects.ams.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MyUserDetailsService implements UserDetailsService {
  @Autowired
  private UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Optional<User> userOptional = userRepository.findByEmail(username);
    if (userOptional.isEmpty()){
      throw new UsernameNotFoundException("Username not found");
    }

    return userOptional.get();
  }
}
