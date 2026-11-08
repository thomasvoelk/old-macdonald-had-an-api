package alpacas;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryPenRepository implements PenRepository {

    private final List<Pen> pens = new ArrayList<>();
    private final AtomicLong nextId = new AtomicLong(1);

    @Override
    public Pen save(Pen pen) {
        if (pen.getId() == null) {
            if (findByName(pen.getName()).isPresent()) {
                throw new IllegalStateException("There is already a pen named " + pen.getName());
            }
            pen.assignId(nextId.getAndIncrement());
            pens.add(pen);
        }
        for (Alpaca alpaca : pen.getHerd().getAlpacas()) {
            if (alpaca.getId() == null) {
                alpaca.assignId(nextId.getAndIncrement());
            }
        }
        return pen;
    }

    @Override
    public Optional<Pen> findByName(String name) {
        return pens.stream().filter(pen -> pen.getName().equalsIgnoreCase(name)).findFirst();
    }

    @Override
    public List<Pen> findAll() {
        return List.copyOf(pens);
    }
}
