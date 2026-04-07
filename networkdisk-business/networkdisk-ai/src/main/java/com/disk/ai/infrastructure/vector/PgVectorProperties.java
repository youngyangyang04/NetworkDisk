package com.disk.ai.infrastructure.vector;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "com.disk.ai.pgvector")
public class PgVectorProperties {

    private boolean enabled = false;

    private boolean initSchema = false;

    private String url = "jdbc:postgresql://127.0.0.1:5432/networkdisk_ai";

    private String username = "postgres";

    private String password = "postgres";

    private Integer dimension = 768;

    private Integer maximumPoolSize = 4;

    private Integer minimumIdle = 1;
}
