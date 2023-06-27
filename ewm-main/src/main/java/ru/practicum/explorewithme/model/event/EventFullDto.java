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
public class EventFullDto {

    String annotation;
    CategoryDto category;
    Long confirmedRequests;
    @JsonFormat(pattern = CommonUtils.DATE_TIME_PATTERN)
    LocalDateTime createdOn;
    String description;
    @JsonFormat(pattern = CommonUtils.DATE_TIME_PATTERN)
    LocalDateTime eventDate;
    Long id;
    UserShortDto initiator;
    LocationDto location;
    Boolean paid;
    Integer participantLimit = 0;
    @JsonFormat(pattern = CommonUtils.DATE_TIME_PATTERN)
    LocalDateTime publishedOn;
    Boolean requestModeration = false;
    EventState state;
    String title;
    Long views;
}