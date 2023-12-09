package com.bpx.brainify.services.implementations;

import com.bpx.brainify.exceptions.InputOutputException;
import com.bpx.brainify.exceptions.OpenAiException;
import com.bpx.brainify.models.dtos.GPTInputDTO;
import com.bpx.brainify.services.interfaces.OpenAiService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@Service
public class OpenAiServiceImpl implements OpenAiService {

    @Value("${openai.api.key}")
    private String OpenAiKey;

    @Value("${openai.api.url.embedding}")
    private String embeddingUrl;

    @Value("${openai.model.embedding}")
    private String embeddingModel;

    @Value("${openai.api.url.chat}")
    private String chatUrl;

    @Value("${openai.model.chat}")
    private String chatModel;
    @Value("${openai.prompt.questions}")
    private String questionPrompt;

    @Value("${openai.prompt.summarize}")
    private String summarizePrompt;
    private final ObjectMapper objectMapper;
    private final OkHttpClient client;

    public OpenAiServiceImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.client = new OkHttpClient.Builder()
                .readTimeout(60000, TimeUnit.MILLISECONDS)
                .build();
    }

    @Override
    public List<Double> getEmbedding(GPTInputDTO text) {
        try {
            String input = text.getInput();
            Map<String, String> requestBodyMap = new HashMap<>();
            requestBodyMap.put("input", input);
            requestBodyMap.put("model", embeddingModel);
            requestBodyMap.put("encoding_format", "float");
            String requestBodyString = objectMapper.writeValueAsString(requestBodyMap);
            MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(requestBodyString, mediaType); //aici am modificat

            Request request = new Request.Builder()
                    .url(embeddingUrl)
                    .addHeader("Authorization", "Bearer " + OpenAiKey)
                    .addHeader("Content-Type", "application/json")
                    .post(requestBody)
                    .build();

            Response response = client.newCall(request).execute();

            if (response.body() != null) {
                JsonNode jsonNode = objectMapper.readTree(response.body().string());
                JsonNode embeddingNode = jsonNode.path("data").get(0).path("embedding");
                List<Double> embedding = new ArrayList<>();
                for (JsonNode node : embeddingNode) {
                    embedding.add(Double.parseDouble(String.valueOf(node)));
                }
                response.close();
                return embedding;
            }
        } catch (IOException exception) {
            throw new InputOutputException(exception.getMessage());
        }
        throw new OpenAiException("OpenAI service temporarily not available!");
    }

    @Override
    public String chat(GPTInputDTO input) {
        try {
            StringBuilder inputSb = new StringBuilder();
            inputSb.append(switch (input.getAction()) {
                case CHAT -> input.getInput();
                case SUMMARIZE -> summarizePrompt + input.getInput();
                case QUESTIONS -> questionPrompt.formatted(input.getQuestionNumber()) + input.getInput();
            });
            String inputString = inputSb.toString();
            String requestPayload = "{\n"
                    + "\"model\": \"" + chatModel + "\",\n"
                    + " \"messages\": [\n"
                    + "{\n"
                    + "\"role\": \"user\",\n"
                    + "\"content\": \"" + inputString + "\"\n"
                    + "}\n"
                    + "]\n"
                    + "}";
            MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(requestPayload, mediaType);

            Request request = new Request.Builder()
                    .url(chatUrl)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "Bearer " + OpenAiKey)
                    .post(requestBody)
                    .build();

            Response response = client.newCall(request).execute();
            if (response.body() != null) {
                JsonNode jsonNode = objectMapper.readTree(response.body().string());
                String contentValue = jsonNode.path("choices").get(0).path("message").path("content").asText();
                response.close();
                return contentValue;
            }
        } catch (IOException exception) {
            throw new InputOutputException(exception.getMessage());
        }
        throw new OpenAiException("OpenAI service temporarily not available!");
    }


}
