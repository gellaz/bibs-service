package it.bibs.user;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import it.bibs.business_profile.BusinessProfile;
import it.bibs.business_profile.BusinessProfileRepository;
import it.bibs.events.BeforeDeleteUser;
import it.bibs.loyalty_account.LoyaltyAccount;
import it.bibs.loyalty_account.LoyaltyAccountRepository;
import it.bibs.security.AclService;
import it.bibs.util.CustomCollectors;
import it.bibs.util.NotFoundException;

@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final BusinessProfileRepository businessProfileRepository;
  private final LoyaltyAccountRepository loyaltyAccountRepository;
  private final ApplicationEventPublisher publisher;
  private final UserMapper userMapper;
  private final AclService aclService;

  public UserDTO getMe() {
    final String identitySubject = aclService.getCurrentUserSubject();

    return userRepository
        .findByIdentitySubject(identitySubject)
        .map(this::mapToDTO)
        .orElseThrow(NotFoundException::new);
  }

  public List<UserDTO> findAll() {
    final List<User> users = userRepository.findAll(Sort.by("id"));
    return users.stream().map(this::mapToDTO).toList();
  }

  public UserDTO get(final UUID id) {
    return userRepository.findById(id).map(this::mapToDTO).orElseThrow(NotFoundException::new);
  }

  private UserDTO mapToDTO(final User user) {
    final UserDTO userDTO = userMapper.toDTO(user);
    final BusinessProfile businessProfile =
        businessProfileRepository.findFirstByUserId(user.getId());
    if (businessProfile != null) {
      userDTO.setBusinessProfile(userMapper.toBusinessProfileDTO(businessProfile));
    }
    final LoyaltyAccount loyaltyAccount = loyaltyAccountRepository.findFirstByUserId(user.getId());
    if (loyaltyAccount != null) {
      userDTO.setLoyaltyAccount(userMapper.toLoyaltyAccountDTO(loyaltyAccount));
    }
    return userDTO;
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
