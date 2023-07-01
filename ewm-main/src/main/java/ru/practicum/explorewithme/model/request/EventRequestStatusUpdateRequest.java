package ru.practicum.explorewithme.model.request;

import lombok.Data;

import java.util.List;

@Data
public class EventRequestStatusUpdateRequest {
    private List<Long> requestIds;
    private RequestUserState status;
}
