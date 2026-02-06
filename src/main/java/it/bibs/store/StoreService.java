package it.bibs.store;

import java.util.List;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import it.bibs.events.BeforeDeleteStore;
import it.bibs.events.BeforeDeleteUser;
import it.bibs.user.UserRepository;
import it.bibs.util.NotFoundException;

@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class StoreService {

  private final StoreRepository storeRepository;
  private final UserRepository userRepository;
  private final ApplicationEventPublisher publisher;
  private final StoreMapper storeMapper;

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
    final Store store = new Store();
    storeMapper.updateStore(storeDTO, store, userRepository);
    return storeRepository.save(store).getId();
  }

  public void update(final UUID id, final StoreDTO storeDTO) {
    final Store store = storeRepository.findById(id).orElseThrow(NotFoundException::new);
    storeMapper.updateStore(storeDTO, store, userRepository);
    storeRepository.save(store);
  }

  public void delete(final UUID id) {
    final Store store = storeRepository.findById(id).orElseThrow(NotFoundException::new);
    publisher.publishEvent(new BeforeDeleteStore(id));
    storeRepository.delete(store);
  }

  @EventListener(BeforeDeleteUser.class)
  public void on(final BeforeDeleteUser event) {
    // remove many-to-many relations at owning side
    storeRepository
        .findAllByUserId(event.getId())
        .forEach(store -> store.getUser().removeIf(user -> user.getId().equals(event.getId())));
  }
}
