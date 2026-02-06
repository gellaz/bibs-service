package it.bibs.order;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import it.bibs.events.BeforeDeleteStore;
import it.bibs.events.BeforeDeleteUser;
import it.bibs.util.ReferencedException;

@Service
@RequiredArgsConstructor
public class OrderService {

  private final OrderRepository orderRepository;

  @EventListener(BeforeDeleteStore.class)
  public void on(final BeforeDeleteStore event) {
    final ReferencedException referencedException = new ReferencedException();
    final Order storeOrder = orderRepository.findFirstByStoreId(event.getId());
    if (storeOrder != null) {
      referencedException.setKey("store.order.store.referenced");
      referencedException.addParam(storeOrder.getId());
      throw referencedException;
    }
  }

  @EventListener(BeforeDeleteUser.class)
  public void on(final BeforeDeleteUser event) {
    final ReferencedException referencedException = new ReferencedException();
    final Order userOrder = orderRepository.findFirstByUserId(event.getId());
    if (userOrder != null) {
      referencedException.setKey("user.order.user.referenced");
      referencedException.addParam(userOrder.getId());
      throw referencedException;
    }
  }
}
