package it.bibs.business_profile;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import it.bibs.events.BeforeDeleteUser;
import it.bibs.util.ReferencedException;

@Service
@RequiredArgsConstructor
public class BusinessProfileService {

  private final BusinessProfileRepository businessProfileRepository;

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
