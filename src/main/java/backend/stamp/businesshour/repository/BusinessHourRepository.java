package backend.stamp.businesshour.repository;

import backend.stamp.businesshour.entity.BusinessHour;
import backend.stamp.store.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface BusinessHourRepository extends JpaRepository<BusinessHour, Long> {
    @Transactional
    void deleteByStore(Store store);
}
