package backend.stamp.ai.ai.subdtos;

import backend.stamp.store.entity.Store;

public record StoreName(
        String name,
        String address
) {
    public StoreName(Store store) {
        this(store.getName(), store.getAddress());
    }
}
