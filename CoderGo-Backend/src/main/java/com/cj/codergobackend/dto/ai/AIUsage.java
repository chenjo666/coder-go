package com.cj.codergobackend.dto.ai;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class AIUsage implements Serializable {
    @JsonProperty("prompt_tokens")
    private long promptTokens;
    @JsonProperty("completion_tokens")
    private long completionTokens;
    @JsonProperty("total_tokens")
    private long totalTokens;
}
