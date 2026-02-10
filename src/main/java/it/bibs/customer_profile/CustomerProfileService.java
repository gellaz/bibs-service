package it.bibs.customer_profile;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import it.bibs.events.BeforeDeleteUser;
import it.bibs.util.ReferencedException;

@Service
@RequiredArgsConstructor
public class CustomerProfileService {

  private final CustomerProfileRepository customerProfileRepository;

  @EventListener(BeforeDeleteUser.class)
  public void on(final BeforeDeleteUser event) {
    final ReferencedException referencedException = new ReferencedException();
    final CustomerProfile customerProfile =
        customerProfileRepository.findFirstByUserId(event.getId());
    if (customerProfile != null) {
      referencedException.setKey("user.customerProfile.user.referenced");
      referencedException.addParam(customerProfile.getId());
      throw referencedException;
    }
  }
}
