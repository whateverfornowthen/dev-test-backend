package uk.gov.hmcts.reform.dev.controllers;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;
import uk.gov.hmcts.reform.dev.config.BaseIT;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TaskControllerTest extends BaseIT {

    @Test
    @Sql("/data/taskData.sql")
    void shouldGetAllTasks() {
        RestAssured.given()
            .when().get("v1/tasks")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("size()", is(10));
    }

    @Test
    void shouldCreateTask() {
        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(readResource("/requests/taskRequest.json"))
            .when().post("v1/tasks")
            .then()
            .statusCode(HttpStatus.CREATED.value());

        assertEquals(1, taskRepository.count());
    }

    @Test
    void shouldReturnBadRequestForInvalidTaskData() {
        RestAssured.given()
            .contentType(ContentType.JSON)
            .body("{ \"title\": \"\", \"description\": \"\" }")
            .when().post("v1/tasks")
            .then()
            .statusCode(HttpStatus.BAD_REQUEST.value());

        assertEquals(0, taskRepository.count());
    }

    @Test
    @Sql("/data/taskData.sql")
    void shouldGetTaskById() {
        Long taskId = taskRepository.findByTitle("Schedule team meeting").getId();

        RestAssured.given()
            .when().get("v1/tasks/" + taskId)
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("title", equalTo("Schedule team meeting"))
            .body(
                "description",
                equalTo("Organise a 30-minute sync with the operations team for next week")
            )
            .body("status", equalTo("PENDING"))
            .body("dueDate", equalTo("2025-06-24"));
    }

    @Test
    @Sql("/data/taskData.sql")
    void shouldReturnNotFoundForNonExistentTask() {
        RestAssured.given()
            .when().get("v1/tasks/9999")
            .then()
            .statusCode(HttpStatus.NOT_FOUND.value())
            .body("message", equalTo("Task not found with id: 9999"));
    }

    @Test
    @Sql("/data/taskData.sql")
    void shouldDeleteTaskById() {
        Long taskId = taskRepository.findByTitle("Schedule team meeting").getId();

        RestAssured.given()
            .when().delete("v1/tasks/" + taskId)
            .then()
            .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @Sql("/data/taskData.sql")
    void shouldReturnNotFoundForDeleteNonExistentTask() {
        RestAssured.given()
            .when().delete("v1/tasks/9999")
            .then()
            .statusCode(HttpStatus.NOT_FOUND.value())
            .body("message", equalTo("Task not found with id: 9999"));
    }

    @Test
    @Sql("/data/taskData.sql")
    void shouldUpdateTaskStatusById() {
        Long taskId = taskRepository.findByTitle("Schedule team meeting").getId();

        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(readResource("/requests/statusUpdateRequest.json"))
            .when().patch("v1/tasks/" + taskId + "/status")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("title", equalTo("Schedule team meeting"))
            .body(
                "description",
                equalTo("Organise a 30-minute sync with the operations team for next week")
            )
            .body("status", equalTo("IN_PROGRESS"))
            .body("dueDate", equalTo("2025-06-24"));
    }

    @Test
    @Sql("/data/taskData.sql")
    void shouldNotUpdateTaskStatusWhenStatusNotValid() {
        Long taskId = taskRepository.findByTitle("Schedule team meeting").getId();

        RestAssured.given()
            .contentType(ContentType.JSON)
            .body("{ \"status\": \"DONE\" }")
            .when().patch("v1/tasks/" + taskId + "/status")
            .then()
            .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @Sql("/data/taskData.sql")
    void shouldReturnNotFoundForUpdateStatusOfNonExistentTask() {
        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(readResource("/requests/statusUpdateRequest.json"))
            .when().patch("v1/tasks/9999/status")
            .then()
            .statusCode(HttpStatus.NOT_FOUND.value())
            .body("message", equalTo("Task not found with id: 9999"));
    }

    @Test
    @Sql("/data/taskData.sql")
    void shouldUpdateTaskById() {
        Long taskId = taskRepository.findByTitle("Schedule team meeting").getId();

        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(readResource("/requests/taskUpdateRequest.json"))
            .when().put("v1/tasks/" + taskId)
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("title", equalTo("Schedule in-person team meeting"))
            .body(
                "description",
                equalTo("Organise an in-person meeting with the whole team for next month")
            )
            .body("status", equalTo("COMPLETED"))
            .body("dueDate", equalTo("2025-06-24"));
    }

    @Test
    @Sql("/data/taskData.sql")
    void shouldReturnNotFoundForUpdateNonExistentTask() {
        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(readResource("/requests/taskUpdateRequest.json"))
            .when().put("v1/tasks/9999")
            .then()
            .statusCode(HttpStatus.NOT_FOUND.value())
            .body("message", equalTo("Task not found with id: 9999"));
    }

}
