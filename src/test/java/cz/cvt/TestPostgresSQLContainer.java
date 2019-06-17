package cz.cvt;

import org.testcontainers.containers.PostgreSQLContainer;

public class TestPostgresSQLContainer extends PostgreSQLContainer<TestPostgresSQLContainer> {

    private static final String IMAGE_VERSION = "postgres:11.3";
    private static TestPostgresSQLContainer dbContainer;

    private TestPostgresSQLContainer() {
        super(IMAGE_VERSION);
    }

    public static TestPostgresSQLContainer getInstance() {
        if (dbContainer == null) {
            dbContainer = new TestPostgresSQLContainer();
        }
        return dbContainer;
    }

    @Override
    public void start() {
        super.start();
        System.setProperty("DB_URL", dbContainer.getJdbcUrl());
        System.setProperty("DB_USERNAME", dbContainer.getUsername());
        System.setProperty("DB_PASSWORD", dbContainer.getPassword());
    }

    @Override
    public void stop() {
        // do nothing, JVM handles shut down
    }
}