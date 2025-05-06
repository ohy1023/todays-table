package store.myproject.onlineshop.repository.item;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import store.myproject.onlineshop.domain.item.Item;

import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long>, ItemCustomRepository {
    Optional<Item> findItemByItemName(String itemName);

    @Query("select i from Item i join fetch i.brand b where i.id = :itemId")
    Optional<Item> findById(@Param("itemId") Long itemId);

    @Lock(LockModeType.PESSIMISTIC_READ)
    Optional<Item> findPessimisticLockById(@Param("id") Long id);
}
