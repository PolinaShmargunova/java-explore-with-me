package ru.practicum.explorewithme.model.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.explorewithme.utils.CommonUtils;

import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ParticipationRequestDto {
    Long id;
    @JsonFormat(pattern = CommonUtils.DATE_TIME_PATTERN)
    LocalDateTime created;
    Long event;
    Long requester;
    RequestState status;
}
