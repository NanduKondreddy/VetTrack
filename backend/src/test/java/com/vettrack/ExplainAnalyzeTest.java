package com.vettrack;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;

@SpringBootTest
@ActiveProfiles("test")
public class ExplainAnalyzeTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void runExplainAnalyze() {
        System.out.println("================ REAL EXPLAIN PLAN OUTPUT ================");
        String sql = "EXPLAIN ANALYZE " +
                "SELECT v.id, a.name, o.name, v.next_due_on " +
                "FROM vaccinations v " +
                "JOIN animals a ON v.animal_id = a.id " +
                "JOIN owners o ON a.owner_id = o.id " +
                "WHERE v.next_due_on < CURRENT_DATE";

        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql);
        for (Map<String, Object> row : results) {
            for (Map.Entry<String, Object> entry : row.entrySet()) {
                System.out.println(entry.getValue());
            }
        }
        System.out.println("==========================================================");
    }
}
