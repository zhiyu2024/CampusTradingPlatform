package cn.gdsdxy.campustrading.common.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class DoubaoService {

    private static final OkHttpClient HTTP_CLIENT = new OkHttpClient().newBuilder()
            .readTimeout(60, TimeUnit.SECONDS)
            .build();

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final String API_URL = "https://ark.cn-beijing.volces.com/api/v3/responses";
    private static final String API_KEY = "08930152-69ab-49cc-a3e0-499aa7290c94";
    private static final String VISION_MODEL_ID = "doubao-seed-1-6-vision-250815";
    private static final String FLASH_MODEL_ID = "doubao-seed-1-6-flash-250828";

    public String generateProductDescription(String productName) {
        if (productName == null || productName.trim().isEmpty()) {
            return null;
        }

        try {
            String prompt = buildDescPrompt(productName);

            ObjectNode requestBody = OBJECT_MAPPER.createObjectNode();
            requestBody.put("model", VISION_MODEL_ID);
            requestBody.put("store", false);
            requestBody.put("max_output_tokens", 300);

            ArrayNode inputArray = requestBody.putArray("input");
            ObjectNode message = inputArray.addObject();
            message.put("role", "user");

            ArrayNode contentArray = message.putArray("content");
            ObjectNode textContent = contentArray.addObject();
            textContent.put("type", "input_text");
            textContent.put("text", prompt);

            String jsonBody = OBJECT_MAPPER.writeValueAsString(requestBody);
            log.info("豆包描述生成请求: {}", jsonBody);

            MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(mediaType, jsonBody);

            Request request = new Request.Builder()
                    .url(API_URL)
                    .post(body)
                    .addHeader("Authorization", "Bearer " + API_KEY)
                    .addHeader("Content-Type", "application/json")
                    .build();

            try (Response response = HTTP_CLIENT.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    log.error("豆包 API 调用失败: code={}", response.code());
                    return null;
                }

                String responseBody = response.body().string();
                log.info("豆包描述生成返回: {}", responseBody);

                return parseOutputText(responseBody);
            }
        } catch (Exception e) {
            log.error("调用豆包 API 异常", e);
        }

        return null;
    }

    public MatchResult calculateMatchScore(String myItem, String myWant, String otherItem, String otherWant) {
        try {
            int keywordScore = calculateKeywordMatchScore(myItem, myWant, otherItem, otherWant);
            if (keywordScore >= 80) {
                return new MatchResult(keywordScore, "完美互补！你有对方想要的，对方有你想要的", true);
            }

            String prompt = buildMatchPrompt(myItem, myWant, otherItem, otherWant);

            ObjectNode requestBody = OBJECT_MAPPER.createObjectNode();
            requestBody.put("model", FLASH_MODEL_ID);
            requestBody.put("store", false);
            requestBody.put("max_output_tokens", 200);

            ArrayNode inputArray = requestBody.putArray("input");
            ObjectNode message = inputArray.addObject();
            message.put("role", "user");

            ArrayNode contentArray = message.putArray("content");
            ObjectNode textContent = contentArray.addObject();
            textContent.put("type", "input_text");
            textContent.put("text", prompt);

            String jsonBody = OBJECT_MAPPER.writeValueAsString(requestBody);
            log.info("豆包匹配请求: {}", jsonBody);

            MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(mediaType, jsonBody);

            Request request = new Request.Builder()
                    .url(API_URL)
                    .post(body)
                    .addHeader("Authorization", "Bearer " + API_KEY)
                    .addHeader("Content-Type", "application/json")
                    .build();

            try (Response response = HTTP_CLIENT.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    log.error("豆包匹配 API 调用失败: code={}", response.code());
                    return new MatchResult(keywordScore, keywordScore > 0 ? "需求互补，推荐交换" : "匹配失败", keywordScore >= 60);
                }

                String responseBody = response.body().string();
                log.info("豆包匹配返回: {}", responseBody);

                String text = parseOutputText(responseBody);
                MatchResult aiResult = parseMatchResult(text);

                if (aiResult.getScore() < keywordScore) {
                    return new MatchResult(keywordScore, "需求互补，推荐交换", true);
                }

                return aiResult;
            }
        } catch (Exception e) {
            log.error("调用豆包匹配 API 异常", e);
        }

        int keywordScore = calculateKeywordMatchScore(myItem, myWant, otherItem, otherWant);
        if (keywordScore > 0) {
            return new MatchResult(keywordScore, "需求互补，推荐交换", keywordScore >= 60);
        }

        return new MatchResult(0, "匹配失败", false);
    }

    private int calculateKeywordMatchScore(String myItem, String myWant, String otherItem, String otherWant) {
        if (myItem == null || myWant == null || otherItem == null) {
            return 0;
        }

        myItem = myItem.toLowerCase();
        myWant = myWant.toLowerCase();
        otherItem = otherItem.toLowerCase();
        otherWant = (otherWant != null) ? otherWant.toLowerCase() : "";

        boolean aHasBWant = containsKeyword(myItem, otherWant);
        boolean bHasAWant = containsKeyword(otherItem, myWant);

        if (aHasBWant && bHasAWant) {
            return 95;
        } else if (aHasBWant || bHasAWant) {
            return 60;
        }

        return 0;
    }

    private boolean containsKeyword(String text, String keyword) {
        if (text == null || keyword == null || keyword.trim().isEmpty()) {
            return false;
        }
        text = text.toLowerCase();
        keyword = keyword.toLowerCase();
        return text.contains(keyword) || keyword.contains(text);
    }

    private String parseOutputText(String responseBody) {
        try {
            JsonNode root = OBJECT_MAPPER.readTree(responseBody);
            
            // 尝试解析豆包API的返回格式
            JsonNode output = root.path("output");
            if (output.isArray() && output.size() > 0) {
                for (JsonNode outputItem : output) {
                    String type = outputItem.path("type").asText();
                    if ("message".equals(type)) {
                        JsonNode contentNode = outputItem.path("content");
                        if (contentNode.isArray() && contentNode.size() > 0) {
                            for (JsonNode item : contentNode) {
                                String contentType = item.path("type").asText();
                                if ("output_text".equals(contentType)) {
                                    return item.path("text").asText().trim();
                                }
                            }
                        }
                    }
                }
            }
            
            // 尝试其他可能的格式
            JsonNode choices = root.path("choices");
            if (choices.isArray() && choices.size() > 0) {
                JsonNode firstChoice = choices.get(0);
                JsonNode message = firstChoice.path("message");
                if (!message.isMissingNode()) {
                    return message.path("content").asText().trim();
                }
            }
            
            // 直接尝试获取text字段
            if (root.has("text")) {
                return root.path("text").asText().trim();
            }
            
            log.warn("无法解析API返回: {}", responseBody);
        } catch (Exception e) {
            log.error("解析输出失败: {}", responseBody, e);
        }
        return null;
    }

    private MatchResult parseMatchResult(String text) {
        if (text == null || text.trim().isEmpty()) {
            return new MatchResult(0, "匹配失败", false);
        }

        try {
            int score = 0;
            String reason = text;
            boolean recommend = false;

            String scorePattern = "(\\d{1,3})%";
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(scorePattern);
            java.util.regex.Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                score = Integer.parseInt(matcher.group(1));
            }

            if (text.contains("推荐") || text.contains("可以交换") || score >= 60) {
                recommend = true;
            }

            return new MatchResult(score, reason, recommend);
        } catch (Exception e) {
            log.error("解析匹配结果失败", e);
            return new MatchResult(0, text, false);
        }
    }

    private String buildDescPrompt(String productName) {
        return "根据【" + productName + "】，直接生成80-120字二手商品描述，不要思考！\n" +
                "\n" +
                "结构：成色→亮点→交易方式\n" +
                "必含：成色、使用情况、性价比、适合人群、可小刀\n" +
                "风格：学生党自用、省钱、亲切、校园、不夸大\n" +
                "\n" +
                "直接输出，不要解释！";
    }

    private String buildMatchPrompt(String myItem, String myWant, String otherItem, String otherWant) {
        return "你是校园以物换物智能匹配助手。\n" +
                "\n" +
                "用户A：拥有【" + myItem + "】，想换【" + myWant + "】\n" +
                "用户B：拥有【" + otherItem + "】，想换【" + (otherWant != null ? otherWant : "任意") + "】\n" +
                "\n" +
                "判断标准：\n" +
                "- 完美互补：用户A有用户B想要的，且用户B有用户A想要的 → 90-100分\n" +
                "- 单向匹配：一方有另一方想要的 → 50-70分\n" +
                "- 不匹配：都没有对方想要的 → 0-40分\n" +
                "\n" +
                "输出规则：\n" +
                "1. 只返回 匹配度(%) + 原因 + 是否推荐\n" +
                "2. 100字以内\n" +
                "3. 不要解释，不要思考，不要多余内容\n" +
                "4. 校园风格简洁自然\n" +
                "\n" +
                "直接输出！";
    }

    public AuditResult auditProductContent(String productName, String description, String imageUrl) {
        try {
            String prompt = buildAuditPrompt(productName, description, imageUrl);

            ObjectNode requestBody = OBJECT_MAPPER.createObjectNode();
            requestBody.put("model", FLASH_MODEL_ID);
            requestBody.put("store", false);
            requestBody.put("max_output_tokens", 200);

            ArrayNode inputArray = requestBody.putArray("input");
            ObjectNode message = inputArray.addObject();
            message.put("role", "user");

            ArrayNode contentArray = message.putArray("content");
            ObjectNode textContent = contentArray.addObject();
            textContent.put("type", "input_text");
            textContent.put("text", prompt);

            String jsonBody = OBJECT_MAPPER.writeValueAsString(requestBody);
            log.info("豆包审核请求: {}", jsonBody);

            MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(mediaType, jsonBody);

            Request request = new Request.Builder()
                    .url(API_URL)
                    .post(body)
                    .addHeader("Authorization", "Bearer " + API_KEY)
                    .addHeader("Content-Type", "application/json")
                    .build();

            try (Response response = HTTP_CLIENT.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    log.error("豆包审核 API 调用失败: code={}, message={}", response.code(), response.message());
                    return new AuditResult((byte) 0, "审核失败：API调用失败(" + response.code() + ")", false);
                }

                String responseBody = response.body().string();
                log.info("豆包审核返回: {}", responseBody);

                String text = parseOutputText(responseBody);
                if (text == null || text.trim().isEmpty()) {
                    return new AuditResult((byte) 0, "审核失败：返回结果为空", false);
                }
                return parseAuditResult(text);
            }
        } catch (Exception e) {
            log.error("调用豆包审核 API 异常", e);
            return new AuditResult((byte) 0, "审核失败：" + e.getMessage(), false);
        }
    }

    private String buildAuditPrompt(String productName, String description, String imageUrl) {
        return "你是校园二手交易平台的AI内容审核助手。\n" +
                "\n" +
                "请审核以下商品信息，判断是否违规：\n" +
                "商品名称：" + (productName != null ? productName : "") + "\n" +
                "商品描述：" + (description != null ? description : "") + "\n" +
                "商品图片：" + (imageUrl != null ? imageUrl : "无") + "（有图则参考，无图则忽略）\n" +
                "\n" +
                "违规判定标准：\n" +
                "1. 敏感词/违禁品：如色情、暴力、政治敏感、毒品、枪支、管制刀具、假冒伪劣等\n" +
                "2. 违规内容：涉政、违法、低俗、虚假宣传、侵权等\n" +
                "3. 校园违规：如校园贷、作弊、代考、代写等\n" +
                "\n" +
                "输出规则：\n" +
                "1. 只返回：审核结果（通过/疑似违规/拒绝） + 违规原因（通过则写\"无违规\"）\n" +
                "2. 100字以内\n" +
                "3. 不要思考，不要解释，直接输出\n" +
                "4. 严格按照标准，不宽松不严格\n" +
                "\n" +
                "直接输出！";
    }

    private AuditResult parseAuditResult(String text) {
        if (text == null || text.trim().isEmpty()) {
            return new AuditResult((byte) 0, "审核失败", false);
        }

        try {
            byte status = 0;
            String reason = text;

            if (text.contains("拒绝")) {
                status = 3;
            } else if (text.contains("疑似违规")) {
                status = 2;
            } else if (text.contains("通过")) {
                status = 1;
            }

            return new AuditResult(status, reason, status == 1);
        } catch (Exception e) {
            log.error("解析审核结果失败", e);
            return new AuditResult((byte) 0, text, false);
        }
    }

    public static class AuditResult {
        private final byte status;
        private final String reason;
        private final boolean passed;

        public AuditResult(byte status, String reason, boolean passed) {
            this.status = status;
            this.reason = reason;
            this.passed = passed;
        }

        public byte getStatus() {
            return status;
        }

        public String getReason() {
            return reason;
        }

        public boolean isPassed() {
            return passed;
        }
    }
}
