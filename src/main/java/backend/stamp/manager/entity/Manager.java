package backend.stamp.manager.entity;


import backend.stamp.account.entity.Account;
import backend.stamp.store.entity.Store;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Builder
@Entity
@Table(name="managers")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Manager {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="manager_id",nullable = false)
    private Long managerId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_fk", nullable = false, unique = true)
    private Account account;

    @Column(nullable = false, unique = true)
    private String businessNum;

    @Builder.Default
    @OneToMany(mappedBy = "manager",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Store> stores = new ArrayList<>();

}

