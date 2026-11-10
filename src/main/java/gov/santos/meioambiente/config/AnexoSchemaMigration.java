package gov.santos.meioambiente.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/** Remove a restrição criada pela antiga relação de uma imagem por chamado no H2. */
@Component
public class AnexoSchemaMigration implements ApplicationRunner {
    private final JdbcTemplate jdbc;

    public AnexoSchemaMigration(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public void run(ApplicationArguments args) {
        var unicas = restricoes("UNIQUE");
        if (unicas.isEmpty()) return;

        // O H2 reutiliza o índice único na chave estrangeira. Remover a FK primeiro
        // permite que o índice desapareça junto com a restrição antiga.
        var estrangeiras = restricoes("FOREIGN KEY");
        for (String nome : estrangeiras) jdbc.execute("ALTER TABLE ANEXO DROP CONSTRAINT " + identificador(nome));
        for (String nome : unicas) jdbc.execute("ALTER TABLE ANEXO DROP CONSTRAINT " + identificador(nome));
        for (String nome : estrangeiras) {
            jdbc.execute("ALTER TABLE ANEXO ADD CONSTRAINT " + identificador(nome)
                + " FOREIGN KEY (CHAMADO_ID) REFERENCES CHAMADO(ID)");
        }
    }

    private java.util.List<String> restricoes(String tipo) {
        String sql = """
            SELECT tc.CONSTRAINT_NAME
              FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS tc
              JOIN INFORMATION_SCHEMA.KEY_COLUMN_USAGE kcu
                ON tc.CONSTRAINT_NAME = kcu.CONSTRAINT_NAME
               AND tc.TABLE_SCHEMA = kcu.TABLE_SCHEMA
             WHERE tc.TABLE_SCHEMA = 'PUBLIC'
               AND tc.TABLE_NAME = 'ANEXO'
               AND tc.CONSTRAINT_TYPE = ?
             GROUP BY tc.CONSTRAINT_NAME
            HAVING COUNT(*) = 1 AND MAX(kcu.COLUMN_NAME) = 'CHAMADO_ID'
            """;
        return jdbc.queryForList(sql, String.class, tipo);
    }

    private String identificador(String nome) { return "\"" + nome.replace("\"", "\"\"") + "\""; }
}
