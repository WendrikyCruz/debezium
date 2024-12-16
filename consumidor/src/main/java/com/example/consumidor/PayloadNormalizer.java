package com.example.consumidor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.tomcat.util.codec.binary.Base64;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class PayloadNormalizer {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static Map<String, Object> normalizePayload(String jsonString) {
        Map<String, Object> normalizedData = new HashMap<>();
        try {
            JsonNode root = objectMapper.readTree(jsonString);
            JsonNode payload = root.path("payload").path("after"); // Obtém 'after' do payload

            if (payload.isObject()) {
                Iterator<Map.Entry<String, JsonNode>> fields = payload.fields();
                while (fields.hasNext()) {
                    Map.Entry<String, JsonNode> field = fields.next();
                    String key = field.getKey();
                    JsonNode valueNode = field.getValue();

                    // Decodificar valores "VariableScaleDecimal"
                    if (valueNode.has("value") && valueNode.has("scale")) {
                        String base64Value = valueNode.get("value").asText();
                        int scale = valueNode.get("scale").asInt();
                        BigDecimal decodedValue = decodeBase64ToBigDecimal(base64Value, scale);
                        normalizedData.put(key, decodedValue);
                    } else {
                        normalizedData.put(key, valueNode.asText());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return normalizedData;
    }

    private static BigDecimal decodeBase64ToBigDecimal(String base64Value, int scale) {
        byte[] decodedBytes = Base64.decodeBase64(base64Value);
        if (decodedBytes.length == 0) return BigDecimal.ZERO;

        // Interpretando os bytes como um número inteiro
        java.math.BigInteger unscaledValue = new java.math.BigInteger(decodedBytes);
        return new BigDecimal(unscaledValue, scale);
    }


}
