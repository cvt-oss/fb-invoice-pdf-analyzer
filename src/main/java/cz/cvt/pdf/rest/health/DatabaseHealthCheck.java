package cz.cvt.pdf.rest.health;

import javax.inject.Inject;

import org.eclipse.microprofile.health.Health;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;

import io.agroal.api.AgroalDataSource;

@Health
public class DatabaseHealthCheck implements HealthCheck {
    @Inject
    AgroalDataSource ds;

    @Override
    public HealthCheckResponse call() {
        HealthCheckResponseBuilder responseBuilder = HealthCheckResponse.named("Database conn");

        responseBuilder = checkDatabaseConnection(responseBuilder);

        return responseBuilder.build();
    }

    private HealthCheckResponseBuilder checkDatabaseConnection(HealthCheckResponseBuilder responseBuilder) {
        try {
            ds.getConnection();
            responseBuilder.withData("connection", true);
            responseBuilder.up();
        } catch (Exception e) {
            responseBuilder.down().withData("error", e.getMessage());
        }
        return responseBuilder;
    }
}