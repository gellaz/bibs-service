package it.bibs.loyalty_account;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import it.bibs.entity.AuditableEntity;
import it.bibs.user.User;

@Entity
@Table(name = "loyalty_accounts")
@Getter
@Setter
public class LoyaltyAccount extends AuditableEntity {

  @Column(nullable = false)
  private Integer pointsBalance;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false, unique = true)
  private User user;
}
