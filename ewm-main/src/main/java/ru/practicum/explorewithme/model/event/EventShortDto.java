package ru.practicum.explorewithme.model.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.explorewithme.model.category.CategoryDto;
import ru.practicum.explorewithme.model.user.UserShortDto;
import ru.practicum.explorewithme.utils.CommonUtils;

import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventShortDto {
    String annotation;
    CategoryDto category;
    Long confirmedRequests;
    @JsonFormat(pattern = CommonUtils.DATE_TIME_PATTERN)
    LocalDateTime eventDate;
    Long id;
    UserShortDto initiator;
    Boolean paid;
    String title;
    Long views;
}