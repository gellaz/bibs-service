package it.bibs.seller_profile;

import java.time.OffsetDateTime;

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
import it.bibs.user.User;

@Entity
@Table(name = "seller_profiles")
@Getter
@Setter
public class SellerProfile extends AuditableEntity {

  @Column(nullable = false, unique = true, length = 11)
  private String vatNumber;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private VatVerificationStatus vatVerificationStatus;

  @Column private OffsetDateTime vatVerifiedAt;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false, unique = true)
  private User user;
}
