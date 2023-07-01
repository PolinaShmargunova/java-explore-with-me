package ru.practicum.explorewithme.model.compilation;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class NewCompilationDto {
    private List<Long> events;
    private Boolean pinned;
    @NotNull
    private String title;
}
