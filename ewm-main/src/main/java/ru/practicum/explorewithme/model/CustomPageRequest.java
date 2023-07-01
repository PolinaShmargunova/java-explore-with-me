package ru.practicum.explorewithme.model;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class CustomPageRequest extends PageRequest {
    int from;

    public CustomPageRequest(int from, int size) {
        super(from / size, size, Sort.unsorted());
        this.from = from;
    }

    public CustomPageRequest(int from, int size, Sort sort) {
        super(from / size, size, sort);
        this.from = from;
    }

    @Override
    public long getOffset() {
        return from;
    }
}
