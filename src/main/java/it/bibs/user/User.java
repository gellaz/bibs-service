package it.bibs.user;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import it.bibs.customer_profile.CustomerProfile;
import it.bibs.entity.AuditableEntity;
import it.bibs.seller_profile.SellerProfile;
import it.bibs.store_member.StoreMember;
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

  @OneToMany(mappedBy = "user")
  private Set<StoreMember> storeMemberships = new HashSet<>();

  @OneToOne(mappedBy = "user")
  private CustomerProfile customerProfile;

  @OneToOne(mappedBy = "user")
  private SellerProfile sellerProfile;
}
