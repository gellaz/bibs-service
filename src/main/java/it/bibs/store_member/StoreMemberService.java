package it.bibs.store_member;

import java.util.List;
import java.util.UUID;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import it.bibs.events.BeforeDeleteStore;
import it.bibs.events.BeforeDeleteUser;
import it.bibs.security.AclService;
import it.bibs.store.Store;
import it.bibs.store.StoreRepository;
import it.bibs.user.User;
import it.bibs.user.UserRepository;
import it.bibs.util.NotFoundException;
import it.bibs.util.ReferencedException;
import it.bibs.util.UnauthorizedException;

@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class StoreMemberService {

  private final StoreMemberRepository storeMemberRepository;
  private final StoreRepository storeRepository;
  private final UserRepository userRepository;
  private final AclService aclService;

  public List<StoreMemberDTO> findAllByStore(final UUID storeId) {
    if (!storeRepository.existsById(storeId)) {
      throw new NotFoundException("Store not found");
    }
    return storeMemberRepository.findAllByStoreId(storeId).stream().map(this::toDTO).toList();
  }

  public UUID addMember(final UUID storeId, final StoreMemberDTO memberDTO) {
    requireStoreOwner(storeId);

    final Store store = storeRepository.findById(storeId).orElseThrow(NotFoundException::new);
    final User user =
        userRepository.findById(memberDTO.getUserId()).orElseThrow(NotFoundException::new);

    if (storeMemberRepository.existsByStoreIdAndUserId(storeId, user.getId())) {
      final ReferencedException ex = new ReferencedException();
      ex.setKey("storeMember.user.alreadyMember");
      throw ex;
    }

    final StoreMember member = new StoreMember();
    member.setStore(store);
    member.setUser(user);
    member.setRole(memberDTO.getRole());
    return storeMemberRepository.save(member).getId();
  }

  public void removeMember(final UUID storeId, final UUID memberId) {
    requireStoreOwner(storeId);

    final StoreMember member =
        storeMemberRepository.findById(memberId).orElseThrow(NotFoundException::new);

    if (!member.getStore().getId().equals(storeId)) {
      throw new NotFoundException("Member not found in this store");
    }

    if (member.getRole() == StoreMemberRole.OWNER) {
      final ReferencedException ex = new ReferencedException();
      ex.setKey("storeMember.owner.cannotRemove");
      throw ex;
    }

    storeMemberRepository.delete(member);
  }

  @EventListener(BeforeDeleteUser.class)
  public void onBeforeDeleteUser(final BeforeDeleteUser event) {
    final List<StoreMember> memberships = storeMemberRepository.findAllByUserId(event.getId());
    if (!memberships.isEmpty()) {
      final ReferencedException ex = new ReferencedException();
      ex.setKey("user.storeMember.referenced");
      ex.addParam(memberships.getFirst().getId());
      throw ex;
    }
  }

  @EventListener(BeforeDeleteStore.class)
  public void onBeforeDeleteStore(final BeforeDeleteStore event) {
    storeMemberRepository.deleteAllByStoreId(event.getId());
  }

  private void requireStoreOwner(final UUID storeId) {
    if (aclService.isAdmin()) {
      return;
    }
    final String subject = aclService.getCurrentUserSubject();
    final User currentUser =
        userRepository.findByIdentitySubject(subject).orElseThrow(NotFoundException::new);
    if (!storeMemberRepository.existsByStoreIdAndUserIdAndRole(
        storeId, currentUser.getId(), StoreMemberRole.OWNER)) {
      throw new UnauthorizedException("Only the store owner can perform this action");
    }
  }

  private StoreMemberDTO toDTO(final StoreMember member) {
    final StoreMemberDTO dto = new StoreMemberDTO();
    dto.setId(member.getId());
    dto.setUserId(member.getUser().getId());
    dto.setRole(member.getRole());
    return dto;
  }
}
