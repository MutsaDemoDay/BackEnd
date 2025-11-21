package backend.stamp.store.repository;

import backend.stamp.account.entity.Account;
import backend.stamp.manager.entity.Manager;
import backend.stamp.store.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StoreRepository extends JpaRepository<Store, Long> {

    List<Store> findByNameContaining(String keyword);

    List<Store> findByEventApplyIsNotNull();
    List<Store> findTop10ByOrderByJoinDateDesc();
    Optional<Store> findByName(String name);
    Optional<Store> findById(Long id);
    boolean existsByVerificationCode(String code);
    //실제 매장 정보 조회
    List<Store> findByIdIn(List<Long> ids);
    Optional<Store> findByManager_Account(Account account);


    // 1. 내 주변 가게 찾기 (거리순 정렬, limit 적용)
    @Query(value = "SELECT *, " +
            "(6371000 * acos(cos(radians(:latitude)) * cos(radians(s.latitude)) * " +
            "cos(radians(s.longitude) - radians(:longitude)) + " +
            "sin(radians(:latitude)) * sin(radians(s.latitude)))) AS distance_meters " +
            "FROM stores s " +
            "ORDER BY distance_meters ASC " +
            "LIMIT :limit",
            nativeQuery = true)
    List<Store> findNearbyStores(
            @Param("latitude") Double latitude,
            @Param("longitude") Double longitude,
            @Param("limit") Integer limit);


    // 2. 검색 및 거리순 정렬 (검색 쿼리 + 거리순 정렬)
    @Query(value = "SELECT *, " +
            "(6371000 * acos(cos(radians(:latitude)) * cos(radians(s.latitude)) * " +
            "cos(radians(s.longitude) - radians(:longitude)) + " +
            "sin(radians(:latitude)) * sin(radians(s.latitude)))) AS distance_meters " +
            "FROM stores s " +
            "WHERE s.store_name LIKE CONCAT('%', :storeName, '%') " +
            "ORDER BY distance_meters ASC",
            nativeQuery = true)
    List<Store> findByNameContainingAndOrderByDistance(
            @Param("storeName") String storeName,
            @Param("latitude") Double latitude,
            @Param("longitude") Double longitude);
    Optional<Store> findByManager(Manager manager);

    List<Store> findByAddressContaining(String gu);


}
