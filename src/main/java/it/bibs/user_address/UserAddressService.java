package it.bibs.user_address;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import it.bibs.events.BeforeDeleteUser;
import it.bibs.util.ReferencedException;

@Service
@RequiredArgsConstructor
public class UserAddressService {

  private final UserAddressRepository userAddressRepository;

  @EventListener(BeforeDeleteUser.class)
  public void on(final BeforeDeleteUser event) {
    final ReferencedException referencedException = new ReferencedException();
    final UserAddress userUserAddress = userAddressRepository.findFirstByUserId(event.getId());
    if (userUserAddress != null) {
      referencedException.setKey("user.userAddress.user.referenced");
      referencedException.addParam(userUserAddress.getId());
      throw referencedException;
    }
  }
}
