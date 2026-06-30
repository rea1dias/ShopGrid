package com.shopgrid.admin.domain.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;

public class RestPage<T> extends PageImpl<T> {

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public RestPage(
            @JsonProperty("content") List<T> content,
            @JsonProperty("number") int page,
            @JsonProperty("size") int size,
            @JsonProperty("totalElements") long totalElements) {
        super(content, PageRequest.of(page, size), totalElements);
    }

    public RestPage() {
        super(new ArrayList<>(), PageRequest.of(0, 20), 0);
    }
}
