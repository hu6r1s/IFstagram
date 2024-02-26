package com.nbcampif.ifstagram.domain.user.repository;

import com.nbcampif.ifstagram.domain.user.UserRole;
import com.nbcampif.ifstagram.domain.user.model.User;
import com.nbcampif.ifstagram.domain.user.repository.entity.UserEntity;
import jakarta.persistence.EntityNotFoundException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class UserRepository {

  private final UserJpaRepository userJpaRepository;

  public void createUser(User user) {
    userJpaRepository.save(UserEntity.fromModel(user));
  }

  public Optional<User> findUser(Long userId) {
    return userJpaRepository.findById(userId).map(UserEntity::toModel);
  }

  public User findUserOrElseThrow(Long userId) {
    return findUser(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));
  }

  public boolean existsByRole(UserRole userRole) {
    return userJpaRepository.existsByRole(userRole);
  }
}
