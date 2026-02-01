package com.disk.base.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

/**
 * RestClient工具
 *
 * @author weikunkun
 */
@Slf4j
public class RestClientUtil {
    public static HttpHeaders configureHeaders(HttpHeaders headers, Map headersMap) {
        headersMap.forEach((k, v) -> headers.add(k.toString(), v.toString()));
        return headers;
    }

    private static String buildUrl(String path, Map<String, String> querys) throws
            UnsupportedEncodingException {
        StringBuilder sbUrl = new StringBuilder();
        if (!StringUtils.isBlank(path)) {
            sbUrl.append(path);
        }
        if (null != querys) {
            StringBuilder sbQuery = new StringBuilder();
            for (Map.Entry<String, String> query : querys.entrySet()) {
                if (0 < sbQuery.length()) {
                    sbQuery.append("&");
                }
                if (StringUtils.isBlank(query.getKey()) && !StringUtils.isBlank(query.getValue())) {
                    sbQuery.append(query.getValue());
                }
                if (!StringUtils.isBlank(query.getKey())) {
                    sbQuery.append(query.getKey());
                    if (!StringUtils.isBlank(query.getValue())) {
                        sbQuery.append("=");
                        sbQuery.append(URLEncoder.encode(query.getValue(), "utf-8"));
                    }
                }
            }
            if (0 < sbQuery.length()) {
                sbUrl.append("?").append(sbQuery);
            }
        }

        return sbUrl.toString();
    }

    public static ResponseEntity doPost(String host, String path, Map<String, String> headersMap,
                                        Map<String, String> querys,
                                        Map<String, String> bodys) throws Exception {


        RestClient restClient = RestClient.builder()
                .baseUrl(host)
                .build();

        var result = restClient.post()
                .uri(buildUrl(path, querys))
                .headers(
                        headers -> configureHeaders(headers, headersMap))
                .body(bodys)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    log.error("http client error, request: {}, response: {}", request, response);
                }).onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                    log.error("http server error, request: {}, response: {}", request, response);
                }).toBodilessEntity();

        return result;
    }

}