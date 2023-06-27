package ru.practicum.explorewithme.model.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.explorewithme.utils.CommonUtils;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewEventDto {
    @NotBlank
    @Size(min = 20, max = 2000)
    String annotation;
    @NotNull Long category;
    @NotNull
    @Size(min = 20, max = 7000)
    String description;
    @NotNull
    @JsonFormat(pattern = CommonUtils.DATE_TIME_PATTERN)
    LocalDateTime eventDate;
    @NotNull LocationDto location;
    Boolean paid = false;
    @PositiveOrZero Integer participantLimit = 0;
    Boolean requestModeration = false;
    @NotBlank
    @Size(min = 3, max = 120)
    String title;
}
