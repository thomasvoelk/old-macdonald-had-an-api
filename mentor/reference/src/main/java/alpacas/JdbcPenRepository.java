package alpacas;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcPenRepository implements PenRepository {

    private final JdbcClient jdbc;

    public JdbcPenRepository(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Pen save(Pen pen) {
        if (pen.getId() == null) {
            insertPen(pen);
        }
        List<Long> keptIds = new ArrayList<>();
        for (Alpaca alpaca : pen.getHerd().getAlpacas()) {
            if (alpaca.getId() == null) {
                insertAlpaca(pen, alpaca);
            } else {
                updateAlpaca(alpaca);
            }
            keptIds.add(alpaca.getId());
        }
        deleteAlpacasOfPenExcept(pen, keptIds);
        return pen;
    }

    @Override
    public Optional<Pen> findByName(String name) {
        return jdbc.sql("select id, name from pens where lower(name) = lower(:name)")
                .param("name", name)
                .query(this::toPen)
                .optional();
    }

    @Override
    public List<Pen> findAll() {
        return jdbc.sql("select id, name from pens order by id")
                .query(this::toPen)
                .list();
    }

    private void insertPen(Pen pen) {
        if (findByName(pen.getName()).isPresent()) {
            throw new IllegalStateException("There is already a pen named " + pen.getName());
        }
        KeyHolder keys = new GeneratedKeyHolder();
        jdbc.sql("insert into pens (name) values (:name)")
                .param("name", pen.getName())
                .update(keys, "id");
        pen.assignId(keys.getKey().longValue());
    }

    private void insertAlpaca(Pen pen, Alpaca alpaca) {
        KeyHolder keys = new GeneratedKeyHolder();
        jdbc.sql("""
                        insert into alpacas (pen_id, name, weight, fleece_length, fleece_color)
                        values (:penId, :name, :weight, :fleeceLength, :fleeceColor)""")
                .param("penId", pen.getId())
                .param("name", alpaca.getName())
                .param("weight", alpaca.getWeight())
                .param("fleeceLength", alpaca.getFleeceLength())
                .param("fleeceColor", alpaca.getFleeceColor().name())
                .update(keys, "id");
        alpaca.assignId(keys.getKey().longValue());
    }

    private void updateAlpaca(Alpaca alpaca) {
        jdbc.sql("""
                        update alpacas
                        set name = :name, weight = :weight, fleece_length = :fleeceLength, fleece_color = :fleeceColor
                        where id = :id""")
                .param("id", alpaca.getId())
                .param("name", alpaca.getName())
                .param("weight", alpaca.getWeight())
                .param("fleeceLength", alpaca.getFleeceLength())
                .param("fleeceColor", alpaca.getFleeceColor().name())
                .update();
    }

    private void deleteAlpacasOfPenExcept(Pen pen, List<Long> keptIds) {
        if (keptIds.isEmpty()) {
            jdbc.sql("delete from alpacas where pen_id = :penId")
                    .param("penId", pen.getId())
                    .update();
        } else {
            jdbc.sql("delete from alpacas where pen_id = :penId and id not in (:keptIds)")
                    .param("penId", pen.getId())
                    .param("keptIds", keptIds)
                    .update();
        }
    }

    private Pen toPen(ResultSet row, int rowNumber) throws SQLException {
        long id = row.getLong("id");
        Herd herd = new Herd();
        for (Alpaca alpaca : alpacasOfPen(id)) {
            herd.addAlpaca(alpaca);
        }
        Pen pen = new Pen(row.getString("name"), herd);
        pen.assignId(id);
        return pen;
    }

    private List<Alpaca> alpacasOfPen(long penId) {
        return jdbc.sql("""
                        select id, name, weight, fleece_length, fleece_color
                        from alpacas where pen_id = :penId order by id""")
                .param("penId", penId)
                .query((row, rowNumber) -> {
                    Alpaca alpaca = new Alpaca(row.getString("name"), row.getDouble("weight"),
                            row.getInt("fleece_length"), FleeceColor.valueOf(row.getString("fleece_color")));
                    alpaca.assignId(row.getLong("id"));
                    return alpaca;
                })
                .list();
    }
}
