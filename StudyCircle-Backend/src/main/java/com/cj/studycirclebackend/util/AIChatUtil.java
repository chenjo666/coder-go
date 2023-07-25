package com.cj.studycirclebackend.util;

import cn.hutool.core.convert.ConvertException;
import cn.hutool.http.HttpException;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.cj.studycirclebackend.dto.ai.AIChoice;
import com.cj.studycirclebackend.dto.ai.AIMessage;
import com.cj.studycirclebackend.dto.ai.AIResponse;
import com.cj.studycirclebackend.dto.ai.AIUsage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class AIChatUtil {

    @Value("${open.ai.default.model}")
    private String model;
    @Value("${open.ai.key}")
    private String apiKey;
    @Value("${open.ai.api.url}")
    private String apiUrl;
    @Value("${open.ai.proxy.host}")
    private String proxyHost;
    @Value("${open.ai.proxy.port}")
    private Integer proxyPort;

    public AIResponse createChatCompletion(List<AIMessage> messages) {
        Map<String, Object> params = new HashMap<>();
        params.put("model", model);
        params.put("messages", messages);

        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
        try {
            String body = HttpRequest.post(apiUrl)
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .body(JSONUtil.toJsonStr(params))
                    .setProxy(proxy)
                    .execute()
                    .body();
            JSONObject jsonObject = JSONUtil.parseObj(body);
            return jsonObjectToAIResponse(jsonObject);
        } catch (HttpException | ConvertException e) {
            return null;
        }
    }


    private AIResponse jsonObjectToAIResponse(JSONObject jsonObject) {
        AIResponse aiResponse = new AIResponse();
        aiResponse.setId(jsonObject.getStr("id"));
        aiResponse.setObject(jsonObject.getStr("object"));
        aiResponse.setCreated(jsonObject.getLong("created"));
        JSONArray choicesJSON = jsonObject.getJSONArray("choices");
        List<AIChoice> aiChoices = new ArrayList<>();
        for (Object o : choicesJSON) {
            AIChoice choice = new AIChoice();
            JSONObject json = (JSONObject) o;
            choice.setIndex(json.getLong("index"));
            JSONObject jsonMessage = json.getJSONObject("message");
            AIMessage aiMessage = new AIMessage(jsonMessage.getStr("role"), jsonMessage.getStr("content"));
            choice.setMessage(aiMessage);
            choice.setFinishReason(json.getStr("finish_reason"));
            aiChoices.add(choice);
        }
        JSONObject usageJSON = jsonObject.getJSONObject("usage");
        AIUsage aiUsage = new AIUsage();
        aiUsage.setPromptTokens(usageJSON.getLong("prompt_tokens"));
        aiUsage.setCompletionTokens(usageJSON.getLong("completion_tokens"));
        aiUsage.setTotalTokens(usageJSON.getLong("total_tokens"));
        aiResponse.setChoices(aiChoices);
        aiResponse.setUsage(aiUsage);
        return aiResponse;
    }
}
