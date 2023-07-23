package com.cj.studycirclebackend.dto.ai;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class AIChoice implements Serializable {
    private Long index;
    private AIMessage message;
    @JsonProperty("finish_reason")
    private String finishReason;
}
