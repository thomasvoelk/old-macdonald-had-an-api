package alpacas;

import java.util.List;
import java.util.Optional;

public interface PenRepository {

    /** Stores the pen and its alpacas. Whatever has no id yet gets one. Returns the same pen. */
    Pen save(Pen pen);

    Optional<Pen> findByName(String name);

    List<Pen> findAll();

    default Pen getByName(String name) {
        return findByName(name).orElseThrow(
                () -> new PenNotFoundException("The Pen you are looking for " + name + " does not exist"));
    }
}
