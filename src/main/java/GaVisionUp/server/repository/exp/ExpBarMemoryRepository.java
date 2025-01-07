package GaVisionUp.server.repository.exp;


import GaVisionUp.server.entity.exp.ExpBar;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class ExpBarMemoryRepository implements ExpBarRepository {

    private final Map<Long, ExpBar> store = new HashMap<>();
    private final AtomicLong sequence = new AtomicLong(1); // ID 자동 증가

    @Override
    public ExpBar save(ExpBar expBar) {
        if (expBar.getId() == 0) {
            expBar.setId(sequence.getAndIncrement()); // 새로운 ID 부여
        }
        store.put(expBar.getId(), expBar);
        return expBar;
    }

    @Override
    public Optional<ExpBar> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Optional<ExpBar> findByUserId(int userId) {
        return store.values().stream()
                .filter(expBar -> expBar.getUserId() == userId)
                .findFirst();
    }

    @Override
    public void updateTotalExp(int userId, int exp) {
        Optional<ExpBar> optionalExpBar = findByUserId(userId);
        optionalExpBar.ifPresent(expBar -> {
            expBar.setTotalExp(expBar.getTotalExp() + exp);
            store.put(expBar.getId(), expBar); // 업데이트
        });
    }
}