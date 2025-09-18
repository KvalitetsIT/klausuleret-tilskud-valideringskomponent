package dk.kvalitetsit.itukt.integrationtest;

import dk.kvalitetsit.itukt.common.configuration.ConnectionConfiguration;
import dk.kvalitetsit.itukt.common.configuration.DataSourceBuilder;
import dk.kvalitetsit.itukt.common.configuration.DatasourceConfiguration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

public class Database {
    private final DataSource dataSource;

    public Database(String host, Integer port, String name, String username, String password) {
        this.dataSource = new DataSourceBuilder().build(
                new DatasourceConfiguration(
                        String.format("jdbc:mariadb://%s:%s/%s", host, port, name),
                        username,
                        password,
                        new ConnectionConfiguration(null, null, null)
                )
        );
    }

    public DataSource getDatasource() {
        return this.dataSource;
    }

    public void clear() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(this.dataSource);
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0;");
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SHOW FULL TABLES WHERE Table_type = 'BASE TABLE'"
        );
        for (Map<String, Object> row : rows) {
            // Safer: pick first column explicitly
            String tableName = (String) row.get(row.keySet().iterator().next());
            if (!tableName.endsWith("_seq")) {
                jdbcTemplate.execute("TRUNCATE TABLE " + tableName);
            }
        }
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1;");
    }
}
