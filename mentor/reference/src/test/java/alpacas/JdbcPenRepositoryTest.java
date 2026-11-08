package alpacas;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.JdbcTest;
import org.springframework.jdbc.core.simple.JdbcClient;

@JdbcTest
class JdbcPenRepositoryTest extends PenRepositoryContract {

    @Autowired
    JdbcClient jdbcClient;

    @Override
    PenRepository createRepository() {
        jdbcClient.sql("delete from alpacas").update();
        jdbcClient.sql("delete from pens").update();
        return new JdbcPenRepository(jdbcClient);
    }
}
