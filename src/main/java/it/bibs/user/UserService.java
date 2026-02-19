package it.bibs.user;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import it.bibs.customer_profile.CustomerProfile;
import it.bibs.customer_profile.CustomerProfileRepository;
import it.bibs.events.BeforeDeleteUser;
import it.bibs.security.AclService;
import it.bibs.seller_profile.SellerProfile;
import it.bibs.seller_profile.SellerProfileRepository;
import it.bibs.seller_profile.VatVerificationStatus;
import it.bibs.util.CustomCollectors;
import it.bibs.util.NotFoundException;
import it.bibs.util.ReferencedException;

@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final SellerProfileRepository sellerProfileRepository;
  private final CustomerProfileRepository customerProfileRepository;
  private final ApplicationEventPublisher publisher;
  private final UserMapper userMapper;
  private final AclService aclService;

  // ── Self-service ──────────────────────────────────────────────────────────

  public UserDTO getMe() {
    final String identitySubject = aclService.getCurrentUserSubject();

    return userRepository
        .findByIdentitySubjectWithProfiles(identitySubject)
        .map(this::mapToDTO)
        .orElseThrow(NotFoundException::new);
  }

  /**
   * Onboard the current user as a seller. Creates a SellerProfile with PENDING VAT status. Fails if
   * the user already has a seller profile or if the VAT number is already in use.
   */
  public void onboardAsSeller(final String vatNumber) {
    final User user = getCurrentUser();

    if (sellerProfileRepository.existsByUserId(user.getId())) {
      final ReferencedException ex = new ReferencedException();
      ex.setKey("user.sellerProfile.alreadyExists");
      throw ex;
    }

    if (sellerProfileRepository.existsByVatNumberIgnoreCase(vatNumber)) {
      final ReferencedException ex = new ReferencedException();
      ex.setKey("sellerProfile.vatNumber.alreadyInUse");
      throw ex;
    }

    final SellerProfile profile = new SellerProfile();
    profile.setUser(user);
    profile.setVatNumber(vatNumber);
    profile.setVatVerificationStatus(VatVerificationStatus.PENDING);
    sellerProfileRepository.save(profile);
  }

  /**
   * Onboard the current user as a customer. Creates a CustomerProfile with zero balance. Fails if
   * the user already has a customer profile.
   */
  public void onboardAsCustomer() {
    final User user = getCurrentUser();

    if (customerProfileRepository.findFirstByUserId(user.getId()) != null) {
      final ReferencedException ex = new ReferencedException();
      ex.setKey("user.customerProfile.alreadyExists");
      throw ex;
    }

    final CustomerProfile profile = new CustomerProfile();
    profile.setUser(user);
    profile.setPointsBalance(0);
    customerProfileRepository.save(profile);
  }

  // ── CRUD ──────────────────────────────────────────────────────────────────

  public List<UserDTO> findAll() {
    final List<User> users = userRepository.findAll(Sort.by("id"));
    return users.stream().map(this::mapToDTO).toList();
  }

  public UserDTO get(final UUID id) {
    return userRepository.findById(id).map(this::mapToDTO).orElseThrow(NotFoundException::new);
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

  // ── Query helpers ─────────────────────────────────────────────────────────

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

  // ── Private ───────────────────────────────────────────────────────────────

  private User getCurrentUser() {
    final String identitySubject = aclService.getCurrentUserSubject();
    return userRepository
        .findByIdentitySubject(identitySubject)
        .orElseThrow(NotFoundException::new);
  }

  private UserDTO mapToDTO(final User user) {
    final UserDTO userDTO = userMapper.toDTO(user);
    final SellerProfile sellerProfile = user.getSellerProfile();
    if (sellerProfile != null) {
      userDTO.setSellerProfile(userMapper.toSellerProfileDTO(sellerProfile));
    }
    final CustomerProfile customerProfile = user.getCustomerProfile();
    if (customerProfile != null) {
      userDTO.setCustomerProfile(userMapper.toCustomerProfileDTO(customerProfile));
    }
    return userDTO;
  }
}
