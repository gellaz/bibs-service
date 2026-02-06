package it.bibs.loyalty_account;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import it.bibs.events.BeforeDeleteUser;
import it.bibs.util.ReferencedException;

@Service
@RequiredArgsConstructor
public class LoyaltyAccountService {

  private final LoyaltyAccountRepository loyaltyAccountRepository;

  @EventListener(BeforeDeleteUser.class)
  public void on(final BeforeDeleteUser event) {
    final ReferencedException referencedException = new ReferencedException();
    final LoyaltyAccount userLoyaltyAccount =
        loyaltyAccountRepository.findFirstByUserId(event.getId());
    if (userLoyaltyAccount != null) {
      referencedException.setKey("user.loyaltyAccount.user.referenced");
      referencedException.addParam(userLoyaltyAccount.getId());
      throw referencedException;
    }
  }
}
