package ru.practicum.explorewithme.model.compilation;

import lombok.Data;

import java.util.List;

@Data
public class UpdateCompilationRequest {
    private List<Long> events;
    private Boolean pinned;
    private String title;
}
