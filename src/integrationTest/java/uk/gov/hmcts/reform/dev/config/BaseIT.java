package uk.gov.hmcts.reform.dev.config;

import io.restassured.RestAssured;
import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlMergeMode;
import org.springframework.util.StreamUtils;
import org.testcontainers.containers.PostgreSQLContainer;
import uk.gov.hmcts.reform.dev.Application;
import uk.gov.hmcts.reform.dev.repositories.TaskRepository;

import java.nio.charset.StandardCharsets;

@SpringBootTest(
    classes = Application.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql({"/data/clearAll.sql"})
@SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
public abstract class BaseIT {

    @ServiceConnection
    private static final PostgreSQLContainer<?> postgreSQLContainer =
        new PostgreSQLContainer<>("postgres:15")
            .withInitScript("data/init.sql");

    static {
        postgreSQLContainer.withReuse(true).start();
    }

    @LocalServerPort
    public int serverPort;

    @Autowired
    public TaskRepository taskRepository;

    @PostConstruct
    public void initRestAssured() {
        RestAssured.port = serverPort;
    }

    @SneakyThrows
    public String readResource(final String resourceName) {
        return StreamUtils.copyToString(getClass().getResourceAsStream(resourceName), StandardCharsets.UTF_8);
    }
}
