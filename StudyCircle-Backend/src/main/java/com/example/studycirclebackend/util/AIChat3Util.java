package com.example.studycirclebackend.util;

import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.completion.CompletionResult;
import com.theokanning.openai.service.OpenAiService;
import com.unfbx.chatgpt.OpenAiClient;
import com.unfbx.chatgpt.entity.completions.CompletionResponse;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.net.Proxy;

@Component
public class AIChat3Util {
    @Value("${open.ai.davinci.model}")
    private String openAiModel;
    @Value("${open.ai.key2}")
    private String key;
    @Value("${open.ai.api.host}")
    private String apiHost;
    @Value("${open.ai.proxy.host}")
    private String proxyHost;
    @Value("${open.ai.proxy.port}")
    private Integer proxyPort;
    @Resource
    private OpenAiService openAiService;

    public CompletionResult chatWithService(String prompt){
        CompletionRequest completionRequest = CompletionRequest.builder()
                .prompt(prompt)
                .model(openAiModel)
                .echo(true)
                .temperature(0.7)
                .topP(1d)
                .frequencyPenalty(0d)
                .presencePenalty(0d)
                .maxTokens(1000)
                .build();
        CompletionResult completionResult = openAiService.createCompletion(completionRequest);
//        String text = completionResult.getChoices().get(0).getText();
        return completionResult;
    }

    public CompletionResponse chatWithClientProxy(String question) {
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
        OpenAiClient openAiClient = OpenAiClient.builder()
                .apiKey(key)
                .connectTimeout(50)
                .writeTimeout(50)
                .readTimeout(50)
                .proxy(proxy)
                .apiHost(apiHost)
                .build();
//        String text = openAiClient.completions(question).getChoices()[0].getText();
        return openAiClient.completions(question);
    }


}
