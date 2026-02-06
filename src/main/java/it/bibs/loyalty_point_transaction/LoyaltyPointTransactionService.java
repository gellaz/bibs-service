package it.bibs.loyalty_point_transaction;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import it.bibs.events.BeforeDeleteUser;
import it.bibs.util.ReferencedException;

@Service
@RequiredArgsConstructor
public class LoyaltyPointTransactionService {

  private final LoyaltyPointTransactionRepository loyaltyPointTransactionRepository;

  @EventListener(BeforeDeleteUser.class)
  public void on(final BeforeDeleteUser event) {
    final ReferencedException referencedException = new ReferencedException();
    final LoyaltyPointTransaction userLoyaltyPointTransaction =
        loyaltyPointTransactionRepository.findFirstByUserId(event.getId());
    if (userLoyaltyPointTransaction != null) {
      referencedException.setKey("user.loyaltyPointTransaction.user.referenced");
      referencedException.addParam(userLoyaltyPointTransaction.getId());
      throw referencedException;
    }
  }
}
