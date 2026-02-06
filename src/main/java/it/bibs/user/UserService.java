package it.bibs.user;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import it.bibs.events.BeforeDeleteUser;
import it.bibs.util.CustomCollectors;
import it.bibs.util.NotFoundException;

@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final ApplicationEventPublisher publisher;
  private final UserMapper userMapper;

  public List<UserDTO> findAll() {
    final List<User> users = userRepository.findAll(Sort.by("id"));
    return users.stream().map(user -> userMapper.updateUserDTO(user, new UserDTO())).toList();
  }

  public UserDTO get(final UUID id) {
    return userRepository
        .findById(id)
        .map(user -> userMapper.updateUserDTO(user, new UserDTO()))
        .orElseThrow(NotFoundException::new);
  }

  public UUID create(final UserDTO userDTO) {
    final User user = new User();
    userMapper.updateUser(userDTO, user);
    return userRepository.save(user).getId();
  }

  public void update(final UUID id, final UserDTO userDTO) {
    final User user = userRepository.findById(id).orElseThrow(NotFoundException::new);
    userMapper.updateUser(userDTO, user);
    userRepository.save(user);
  }

  public void delete(final UUID id) {
    final User user = userRepository.findById(id).orElseThrow(NotFoundException::new);
    publisher.publishEvent(new BeforeDeleteUser(id));
    userRepository.delete(user);
  }

  public boolean identitySubjectExists(final String identitySubject) {
    return userRepository.existsByIdentitySubject(identitySubject);
  }

  public boolean emailExists(final String email) {
    return userRepository.existsByEmail(email);
  }

  public Map<UUID, UUID> getUserValues() {
    return userRepository.findAll(Sort.by("id")).stream()
        .collect(CustomCollectors.toSortedMap(User::getId, User::getId));
  }
}
