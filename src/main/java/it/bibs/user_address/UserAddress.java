package it.bibs.user_address;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import it.bibs.entity.AuditableEntity;
import it.bibs.user.User;

@Entity
@Table(name = "user_addresses")
@Getter
@Setter
public class UserAddress extends AuditableEntity {

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private UserAddressType addressType;

  @Column(columnDefinition = "text")
  private String label;

  @Column(columnDefinition = "text")
  private String recipientName;

  @Column(columnDefinition = "text")
  private String phone;

  @Column(nullable = false, columnDefinition = "text")
  private String addressLine1;

  @Column(columnDefinition = "text")
  private String addressLine2;

  @Column(nullable = false, columnDefinition = "text")
  private String city;

  @Column(nullable = false, columnDefinition = "text")
  private String zipCode;

  @Column(columnDefinition = "text")
  private String province;

  @Column(nullable = false, length = 2)
  private String country;

  @Column private Double latitude;

  @Column private Double longitude;

  @Column(nullable = false)
  private Boolean isDefault;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;
}
