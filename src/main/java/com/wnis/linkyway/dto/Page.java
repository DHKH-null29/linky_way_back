package com.wnis.linkyway.dto;

import java.util.Collections;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Page<T> {

    private final List<T> content;
    private final Boolean hasNext;
    private final Long lastIndex;

    public static <T> Page<T> of(List<T> content, boolean hasNext, Long lastIndex) {
        return new Page<>(content, hasNext, lastIndex);
    }

    public List<T> getContent() {
        return Collections.unmodifiableList(this.content);
    }

    public boolean isHasNext() {
        return hasNext;
    }

    public Long getLastIndex() {
        return lastIndex;
    }
}
