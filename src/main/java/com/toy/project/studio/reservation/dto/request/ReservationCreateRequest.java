package com.toy.project.studio.reservation.dto.request;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ReservationCreateRequest(
        @NotBlank(message = "type is required.") String type,
        @NotNull(message = "date is required.")
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate date,
        @NotNull(message = "headcount is required.")
        @Min(value = 1, message = "headcount must be at least 1.")
        @JsonProperty("headcount")
        Integer headCount,
        @NotNull(message = "startTime is required.")
        @JsonFormat(pattern = "HH:mm")
        LocalTime startTime,
        @NotNull(message = "endTime is required.")
        @JsonFormat(pattern = "HH:mm")
        LocalTime endTime,
        @NotBlank(message = "name is required.")
        @Size(max = 50, message = "name must be 50 characters or fewer.")
        String name,
        @NotBlank(message = "phone is required.")
        @Size(max = 20, message = "phone must be 20 characters or fewer.")
        String phone,
        @Size(max = 50, message = "visitPath must be 50 characters or fewer.")
        String visitPath,
        String requestMessage,
        @NotEmpty(message = "agreedTerms is required.")
        List<Long> agreedTerms
) {
}
