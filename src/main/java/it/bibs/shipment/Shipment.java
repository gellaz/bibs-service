package it.bibs.shipment;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import it.bibs.entity.AuditableEntity;
import it.bibs.order.Order;

@Entity
@Table(name = "shipments")
@Getter
@Setter
public class Shipment extends AuditableEntity {

  @Column(nullable = false)
  private String recipientName;

  @Column private String phone;

  @Column(nullable = false)
  private String addressLine1;

  @Column private String addressLine2;

  @Column(nullable = false)
  private String city;

  @Column(nullable = false)
  private String zipCode;

  @Column private String province;

  @Column(nullable = false, length = 2)
  private String country;

  @Column private String carrier;

  @Column private String trackingCode;

  @Column(nullable = false)
  private Integer shippingCostCents;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private ShipmentStatus status;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "order_id", nullable = false, unique = true)
  private Order order;
}
