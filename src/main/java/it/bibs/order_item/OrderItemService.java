package it.bibs.order_item;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import it.bibs.events.BeforeDeleteProduct;
import it.bibs.util.ReferencedException;

@Service
@RequiredArgsConstructor
public class OrderItemService {

  private final OrderItemRepository orderItemRepository;

  @EventListener(BeforeDeleteProduct.class)
  public void on(final BeforeDeleteProduct event) {
    final ReferencedException referencedException = new ReferencedException();
    final OrderItem productOrderItem = orderItemRepository.findFirstByProductId(event.getId());
    if (productOrderItem != null) {
      referencedException.setKey("product.orderItem.product.referenced");
      referencedException.addParam(productOrderItem.getId());
      throw referencedException;
    }
  }
}
