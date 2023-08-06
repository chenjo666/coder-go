package com.cj.codergobackend.dto.ai;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class AIResponse implements Serializable {
    private String id;
    private String object;
    private Long created;
    private List<AIChoice> choices;
    private AIUsage usage;
}
