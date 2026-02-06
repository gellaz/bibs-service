package it.bibs.store;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import it.bibs.entity.AuditableEntity;
import it.bibs.user.User;

@Entity
@Table(name = "stores")
@Getter
@Setter
public class Store extends AuditableEntity {

  @Column(nullable = false, columnDefinition = "text")
  private String name;

  @Column(columnDefinition = "text")
  private String description;

  @Column(nullable = false, columnDefinition = "text")
  private String addressLine1;

  @Column(columnDefinition = "text")
  private String addressLine2;

  @Column(nullable = false, columnDefinition = "text")
  private String city;

  @Column(nullable = false, columnDefinition = "text")
  private String zipCode;

  @Column private String province;

  @Column(nullable = false, length = 2)
  private String country;

  @Column private Double latitude;

  @Column private Double longitude;

  @ManyToMany
  @JoinTable(
      name = "store_members",
      joinColumns = @JoinColumn(name = "storeId"),
      inverseJoinColumns = @JoinColumn(name = "userId"))
  private Set<User> user = new HashSet<>();
}
