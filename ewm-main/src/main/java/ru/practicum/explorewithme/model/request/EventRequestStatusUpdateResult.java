package ru.practicum.explorewithme.model.request;

import lombok.Data;

import java.util.LinkedList;
import java.util.List;

@Data
public class EventRequestStatusUpdateResult {
    List<ParticipationRequestDto> confirmedRequests;
    List<ParticipationRequestDto> rejectedRequests;

    public EventRequestStatusUpdateResult() {
        confirmedRequests = new LinkedList<>();
        rejectedRequests = new LinkedList<>();
    }

    public void addConfirmedRequest(ParticipationRequestDto request) {
        confirmedRequests.add(request);
    }

    public void addRejectedRequest(ParticipationRequestDto request) {
        rejectedRequests.add(request);
    }
}
