package uk.gov.hmcts.reform.dev.dto;

import jakarta.validation.constraints.NotNull;
import uk.gov.hmcts.reform.dev.models.Status;

public record StatusUpdateDto(
    @NotNull
    Status status
) {}
