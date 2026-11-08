package alpacas;

class InMemoryPenRepositoryTest extends PenRepositoryContract {

    @Override
    PenRepository createRepository() {
        return new InMemoryPenRepository();
    }
}
