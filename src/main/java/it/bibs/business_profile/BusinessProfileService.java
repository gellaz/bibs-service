package it.bibs.business_profile;

import java.util.List;
import java.util.UUID;

import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import it.bibs.events.BeforeDeleteUser;
import it.bibs.user.UserRepository;
import it.bibs.util.NotFoundException;
import it.bibs.util.ReferencedException;

@Service
@RequiredArgsConstructor
public class BusinessProfileService {

  private final BusinessProfileRepository businessProfileRepository;
  private final UserRepository userRepository;
  private final BusinessProfileMapper businessProfileMapper;

  public List<BusinessProfileDTO> findAll() {
    final List<BusinessProfile> businessProfiles = businessProfileRepository.findAll(Sort.by("id"));
    return businessProfiles.stream()
        .map(
            businessProfile ->
                businessProfileMapper.updateBusinessProfileDTO(
                    businessProfile, new BusinessProfileDTO()))
        .toList();
  }

  public BusinessProfileDTO get(final UUID id) {
    return businessProfileRepository
        .findById(id)
        .map(
            businessProfile ->
                businessProfileMapper.updateBusinessProfileDTO(
                    businessProfile, new BusinessProfileDTO()))
        .orElseThrow(NotFoundException::new);
  }

  public UUID create(final BusinessProfileDTO businessProfileDTO) {
    final BusinessProfile businessProfile = new BusinessProfile();
    businessProfileMapper.updateBusinessProfile(
        businessProfileDTO, businessProfile, userRepository);
    return businessProfileRepository.save(businessProfile).getId();
  }

  public void update(final UUID id, final BusinessProfileDTO businessProfileDTO) {
    final BusinessProfile businessProfile =
        businessProfileRepository.findById(id).orElseThrow(NotFoundException::new);
    businessProfileMapper.updateBusinessProfile(
        businessProfileDTO, businessProfile, userRepository);
    businessProfileRepository.save(businessProfile);
  }

  public void delete(final UUID id) {
    final BusinessProfile businessProfile =
        businessProfileRepository.findById(id).orElseThrow(NotFoundException::new);
    businessProfileRepository.delete(businessProfile);
  }

  public boolean vatNumberExists(final String vatNumber) {
    return businessProfileRepository.existsByVatNumberIgnoreCase(vatNumber);
  }

  public boolean userExists(final UUID id) {
    return businessProfileRepository.existsByUserId(id);
  }

  @EventListener(BeforeDeleteUser.class)
  public void on(final BeforeDeleteUser event) {
    final ReferencedException referencedException = new ReferencedException();
    final BusinessProfile userBusinessProfile =
        businessProfileRepository.findFirstByUserId(event.getId());
    if (userBusinessProfile != null) {
      referencedException.setKey("user.businessProfile.user.referenced");
      referencedException.addParam(userBusinessProfile.getId());
      throw referencedException;
    }
  }
}
