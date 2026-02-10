package it.bibs.seller_profile;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import it.bibs.events.BeforeDeleteUser;
import it.bibs.user.UserRepository;
import it.bibs.util.NotFoundException;
import it.bibs.util.ReferencedException;
import it.bibs.util.UnauthorizedException;

@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class SellerProfileService {

  private final SellerProfileRepository sellerProfileRepository;
  private final UserRepository userRepository;
  private final SellerProfileMapper sellerProfileMapper;

  public List<SellerProfileDTO> findAll() {
    final List<SellerProfile> sellerProfiles = sellerProfileRepository.findAll(Sort.by("id"));
    return sellerProfiles.stream()
        .map(
            sellerProfile ->
                sellerProfileMapper.updateSellerProfileDTO(sellerProfile, new SellerProfileDTO()))
        .toList();
  }

  public List<SellerProfileDTO> findAllByStatus(final VatVerificationStatus status) {
    final List<SellerProfile> sellerProfiles =
        sellerProfileRepository.findAllByVatVerificationStatus(status, Sort.by("id"));
    return sellerProfiles.stream()
        .map(
            sellerProfile ->
                sellerProfileMapper.updateSellerProfileDTO(sellerProfile, new SellerProfileDTO()))
        .toList();
  }

  public SellerProfileDTO get(final UUID id) {
    return sellerProfileRepository
        .findById(id)
        .map(
            sellerProfile ->
                sellerProfileMapper.updateSellerProfileDTO(sellerProfile, new SellerProfileDTO()))
        .orElseThrow(NotFoundException::new);
  }

  public UUID create(final SellerProfileDTO sellerProfileDTO) {
    final SellerProfile sellerProfile = new SellerProfile();
    sellerProfileMapper.updateSellerProfile(sellerProfileDTO, sellerProfile, userRepository);
    return sellerProfileRepository.save(sellerProfile).getId();
  }

  public void update(final UUID id, final SellerProfileDTO sellerProfileDTO) {
    final SellerProfile sellerProfile =
        sellerProfileRepository.findById(id).orElseThrow(NotFoundException::new);
    sellerProfileMapper.updateSellerProfile(sellerProfileDTO, sellerProfile, userRepository);
    sellerProfileRepository.save(sellerProfile);
  }

  public void delete(final UUID id) {
    final SellerProfile sellerProfile =
        sellerProfileRepository.findById(id).orElseThrow(NotFoundException::new);
    sellerProfileRepository.delete(sellerProfile);
  }

  public void verifyVat(final UUID id) {
    final SellerProfile sellerProfile =
        sellerProfileRepository.findById(id).orElseThrow(NotFoundException::new);
    requirePendingStatus(sellerProfile);
    sellerProfile.setVatVerificationStatus(VatVerificationStatus.VERIFIED);
    sellerProfile.setVatVerifiedAt(OffsetDateTime.now());
    sellerProfileRepository.save(sellerProfile);
  }

  public void rejectVat(final UUID id) {
    final SellerProfile sellerProfile =
        sellerProfileRepository.findById(id).orElseThrow(NotFoundException::new);
    requirePendingStatus(sellerProfile);
    sellerProfile.setVatVerificationStatus(VatVerificationStatus.REJECTED);
    sellerProfile.setVatVerifiedAt(OffsetDateTime.now());
    sellerProfileRepository.save(sellerProfile);
  }

  /**
   * Checks that the given user has a VERIFIED seller profile. Used as a gate for owner-only
   * features (store creation, product management).
   */
  public void requireVerifiedSeller(final UUID userId) {
    final SellerProfile profile = sellerProfileRepository.findFirstByUserId(userId);
    if (profile == null || profile.getVatVerificationStatus() != VatVerificationStatus.VERIFIED) {
      throw new UnauthorizedException("VAT verification is required before performing this action");
    }
  }

  public boolean vatNumberExists(final String vatNumber) {
    return sellerProfileRepository.existsByVatNumberIgnoreCase(vatNumber);
  }

  public boolean userExists(final UUID id) {
    return sellerProfileRepository.existsByUserId(id);
  }

  @EventListener(BeforeDeleteUser.class)
  public void on(final BeforeDeleteUser event) {
    final ReferencedException referencedException = new ReferencedException();
    final SellerProfile sellerProfile = sellerProfileRepository.findFirstByUserId(event.getId());
    if (sellerProfile != null) {
      referencedException.setKey("user.sellerProfile.user.referenced");
      referencedException.addParam(sellerProfile.getId());
      throw referencedException;
    }
  }

  private void requirePendingStatus(final SellerProfile sellerProfile) {
    if (sellerProfile.getVatVerificationStatus() != VatVerificationStatus.PENDING) {
      final ReferencedException ex = new ReferencedException();
      ex.setKey("sellerProfile.vatVerificationStatus.notPending");
      ex.addParam(sellerProfile.getVatVerificationStatus());
      throw ex;
    }
  }
}
