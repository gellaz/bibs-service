package it.bibs.user;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import it.bibs.entity.AuditableEntity;
import it.bibs.loyalty_account.LoyaltyAccount;
import it.bibs.store.Store;
import it.bibs.user_address.UserAddress;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User extends AuditableEntity {

  @Column(nullable = false, unique = true, columnDefinition = "text")
  private String identitySubject;

  @Column(nullable = false, unique = true, columnDefinition = "text")
  private String email;

  @Column(columnDefinition = "text")
  private String firstName;

  @Column(columnDefinition = "text")
  private String lastName;

  @OneToMany(mappedBy = "user")
  private Set<UserAddress> userAddresses = new HashSet<>();

  @ManyToMany(mappedBy = "user")
  private Set<Store> stores = new HashSet<>();

  @OneToOne(mappedBy = "user")
  private LoyaltyAccount loyaltyAccount;
}
