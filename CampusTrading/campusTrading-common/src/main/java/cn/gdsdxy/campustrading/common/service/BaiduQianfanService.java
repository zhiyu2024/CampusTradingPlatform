package cn.gdsdxy.campustrading.common.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class BaiduQianfanService {

    private static final OkHttpClient HTTP_CLIENT = new OkHttpClient().newBuilder()
            .readTimeout(300, TimeUnit.SECONDS)
            .build();

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final String API_URL = "https://aip.baidubce.com/rpc/2.0/nlp/v1/sentiment_classify?charset=UTF-8";

    private String accessToken = "24.de42e0a0191311ba5fd4ac2a3d592036.2592000.1778683373.282335-122859187";

    public SentimentResult analyzeSentiment(String text) {
        if (text == null || text.trim().isEmpty()) {
            return new SentimentResult(0, BigDecimal.ZERO);
        }

        try {
            MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
            String jsonBody = "{\"text\":\"" + escapeJson(text) + "\"}";
            RequestBody body = RequestBody.create(mediaType,jsonBody);

            Request request = new Request.Builder()
                    .url(API_URL + "&access_token=" + accessToken)
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .build();

            try (Response response = HTTP_CLIENT.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    log.error("百度千帆 API 调用失败: code={}", response.code());
                    return new SentimentResult(0, BigDecimal.ZERO);
                }

                String responseBody = response.body().string();
                log.info("百度千帆返回: {}", responseBody);

                JsonNode root = OBJECT_MAPPER.readTree(responseBody);
                JsonNode items = root.path("items");
                if (items.isArray() && items.size() > 0) {
                    JsonNode item = items.get(0);
                    int baiduSentiment = item.path("sentiment").asInt();
                    BigDecimal confidence = new BigDecimal(item.path("confidence").asText());

                    int dbSentiment = mapBaiduSentimentToDb(baiduSentiment);
                    return new SentimentResult(dbSentiment, confidence);
                }
            }
        } catch (IOException e) {
            log.error("调用百度千帆 API 异常", e);
        }

        return new SentimentResult(0, BigDecimal.ZERO);
    }

    private int mapBaiduSentimentToDb(int baiduSentiment) {
        switch (baiduSentiment) {
            case 0: return 2;
            case 1: return 3;
            case 2: return 1;
            default: return 0;
        }
    }

    private String escapeJson(String text) {
        return text.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }

    public static class SentimentResult {
        private final int sentiment;
        private final BigDecimal confidence;

        public SentimentResult(int sentiment, BigDecimal confidence) {
            this.sentiment = sentiment;
            this.confidence = confidence;
        }

        public int getSentiment() {
            return sentiment;
        }

        public BigDecimal getConfidence() {
            return confidence;
        }
    }
}
