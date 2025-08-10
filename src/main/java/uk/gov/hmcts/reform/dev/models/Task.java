package uk.gov.hmcts.reform.dev.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.SEQUENCE;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Entity
public class Task {

    @Id
    @SequenceGenerator(name = "task_seq", sequenceName = "task_sequence", allocationSize = 1)
    @GeneratedValue(strategy = SEQUENCE, generator = "task_seq")
    @Schema(description = "Task ID", accessMode = READ_ONLY, example = "123")
    private Long id;

    @Schema(description = "Title of the task", example = "Finish writing API docs")
    @NotBlank
    @Size(max = 255)
    @Column(nullable = false)
    private String title;

    @Schema(description = "Detailed description of the task", example = "Document the endpoints and data models for "
        + "the task management API")
    @Size(max = 1000)
    private String description;

    @Schema(description = "Current status of the task", example = "IN_PROGRESS")
    @Enumerated(STRING)
    @NotNull
    @Column(nullable = false)
    private Status status;

    @Schema(description = "Due date for completing the task", example = "2025-07-01", type = "string", format = "date")
    @NotNull
    @Column(nullable = false)
    private LocalDate dueDate;
}
