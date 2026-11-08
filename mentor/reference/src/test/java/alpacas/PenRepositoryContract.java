package alpacas;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static alpacas.FleeceColor.BROWN;
import static alpacas.FleeceColor.WHITE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

abstract class PenRepositoryContract {

    abstract PenRepository createRepository();

    private PenRepository repository;

    @BeforeEach
    void createTheRepository() {
        repository = createRepository();
    }

    private static Pen penWithLita(String penName) {
        Herd herd = new Herd();
        herd.addAlpaca(new Alpaca("Lita", 70, 4, WHITE));
        return new Pen(penName, herd);
    }

    @Test
    void savingGivesThePenAndItsAlpacasIds() {
        Pen saved = repository.save(penWithLita("Ring"));

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getHerd().getAlpacas()).allSatisfy(a -> assertThat(a.getId()).isNotNull());
    }

    @Test
    void getByNameThrowsForAnUnknownPen() {
        repository.save(penWithLita("Ring"));

        assertThat(repository.getByName("ring").getName()).isEqualTo("Ring");
        assertThrows(PenNotFoundException.class, () -> repository.getByName("Nowhere"));
    }

    @Test
    void twoPensGetDifferentIds() {
        Pen ring = repository.save(penWithLita("Ring"));
        Pen cell = repository.save(new Pen("Cell", new Herd()));

        assertThat(ring.getId()).isNotEqualTo(cell.getId());
    }

    @Test
    void aSavedPenIsFoundByNameIgnoringCase() {
        Pen saved = repository.save(penWithLita("Ring"));

        Pen found = repository.findByName("rInG").orElseThrow();

        assertThat(found.getId()).isEqualTo(saved.getId());
        assertThat(found.getHerd().findByName("Lita")).isPresent();
    }

    @Test
    void anUnknownPenIsNotFound() {
        assertThat(repository.findByName("Nowhere")).isEmpty();
    }

    @Test
    void findAllReturnsEveryPenInTheOrderItWasSaved() {
        repository.save(new Pen("Ring", new Herd()));
        repository.save(new Pen("Cell", new Herd()));

        List<Pen> pens = repository.findAll();

        assertThat(pens).extracting(Pen::getName).containsExactly("Ring", "Cell");
    }

    @Test
    void savingAgainKeepsOldIdsAndGivesNewAlpacasTheirs() {
        Pen pen = repository.save(penWithLita("Ring"));
        long penId = pen.getId();
        long litaId = pen.getHerd().findByName("Lita").orElseThrow().getId();
        pen.getHerd().addAlpaca(new Alpaca("Batista", 80, 10, BROWN));

        repository.save(pen);

        Pen found = repository.findByName("Ring").orElseThrow();
        assertThat(found.getId()).isEqualTo(penId);
        assertThat(found.getHerd().findByName("Lita").orElseThrow().getId()).isEqualTo(litaId);
        assertThat(found.getHerd().findByName("Batista").orElseThrow().getId())
                .isNotNull().isNotEqualTo(litaId);
    }

    @Test
    void anAlpacaRemovedFromTheHerdIsGoneAfterSaving() {
        Pen pen = repository.save(penWithLita("Ring"));
        pen.getHerd().removeByName("Lita");

        repository.save(pen);

        assertThat(repository.findByName("Ring").orElseThrow().getHerd().findByName("Lita")).isEmpty();
    }

    @Test
    void aSecondNewPenWithTheSameNameIsRejected() {
        repository.save(new Pen("Ring", new Herd()));

        assertThrows(IllegalStateException.class, () -> repository.save(new Pen("ring", new Herd())));
    }
}
