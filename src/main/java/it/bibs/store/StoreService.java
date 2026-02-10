package it.bibs.store;

import java.util.List;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import it.bibs.events.BeforeDeleteStore;
import it.bibs.security.AclService;
import it.bibs.seller_profile.SellerProfileService;
import it.bibs.store_member.StoreMember;
import it.bibs.store_member.StoreMemberRepository;
import it.bibs.store_member.StoreMemberRole;
import it.bibs.user.User;
import it.bibs.user.UserRepository;
import it.bibs.util.NotFoundException;

@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class StoreService {

  private final StoreRepository storeRepository;
  private final UserRepository userRepository;
  private final StoreMemberRepository storeMemberRepository;
  private final ApplicationEventPublisher publisher;
  private final StoreMapper storeMapper;
  private final AclService aclService;
  private final SellerProfileService sellerProfileService;

  public List<StoreDTO> findAll() {
    final List<Store> stores = storeRepository.findAll(Sort.by("id"));
    return stores.stream().map(store -> storeMapper.updateStoreDTO(store, new StoreDTO())).toList();
  }

  public StoreDTO get(final UUID id) {
    return storeRepository
        .findById(id)
        .map(store -> storeMapper.updateStoreDTO(store, new StoreDTO()))
        .orElseThrow(NotFoundException::new);
  }

  public UUID create(final StoreDTO storeDTO) {
    final User currentUser = getCurrentUserWithVatGate();
    final Store store = new Store();
    storeMapper.updateStore(storeDTO, store);
    final Store savedStore = storeRepository.save(store);

    // Auto-add the creator as OWNER
    final StoreMember ownerMember = new StoreMember();
    ownerMember.setStore(savedStore);
    ownerMember.setUser(currentUser);
    ownerMember.setRole(StoreMemberRole.OWNER);
    storeMemberRepository.save(ownerMember);

    return savedStore.getId();
  }

  public void update(final UUID id, final StoreDTO storeDTO) {
    final Store store = storeRepository.findById(id).orElseThrow(NotFoundException::new);
    storeMapper.updateStore(storeDTO, store);
    storeRepository.save(store);
  }

  public void delete(final UUID id) {
    final Store store = storeRepository.findById(id).orElseThrow(NotFoundException::new);
    publisher.publishEvent(new BeforeDeleteStore(id));
    storeMemberRepository.deleteAllByStoreId(id);
    storeRepository.delete(store);
  }

  /**
   * Returns the currently authenticated user after enforcing the VAT VERIFIED gate. Admins bypass
   * the check.
   */
  private User getCurrentUserWithVatGate() {
    final String subject = aclService.getCurrentUserSubject();
    final User user =
        userRepository.findByIdentitySubject(subject).orElseThrow(NotFoundException::new);
    if (!aclService.isAdmin()) {
      sellerProfileService.requireVerifiedSeller(user.getId());
    }
    return user;
  }
}
