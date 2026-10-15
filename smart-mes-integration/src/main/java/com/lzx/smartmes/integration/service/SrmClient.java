package com.lzx.smartmes.integration.service;

import com.lzx.smartmes.integration.exception.IntegrationException;
import com.lzx.smartmes.integration.vo.SrmMaterialResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@Slf4j
public class SrmClient {

    private final RestClient client;

    public SrmClient(
            @Value("${smart-mes.srm.base-url:http://localhost:9007}")
            String base
    ) {
        client = RestClient.builder()
                .baseUrl(base)
                .build();
    }

    public SrmMaterialResponse stock(String code) {

        try {
            SrmMaterialResponse response = client.get()
                    .uri(
                            "/mock/srm/material/{code}",
                            code
                    )
                    .retrieve()
                    .body(SrmMaterialResponse.class);

            if (response == null) {
                throw new IntegrationException(
                        "SRM returned empty stock"
                );
            }

            return response;

        } catch (Exception e) {

            log.warn(
                    "SRM stock request failed for {}: {}",
                    code,
                    e.getMessage()
            );

            throw new IntegrationException(
                    "SRM unavailable; production release should be retried"
            );
        }
    }
}