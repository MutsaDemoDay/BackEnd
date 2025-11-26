package backend.stamp.auth.service;

import backend.stamp.auth.dto.external.NtsStatusData;
import backend.stamp.auth.dto.external.NtsStatusRequest;
import backend.stamp.auth.dto.external.NtsStatusResponse;
import backend.stamp.global.exception.ApplicationException;
import backend.stamp.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.http.HttpHeaders;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BusinessStatusClient {
    private final RestTemplate restTemplate;

    @Value("${nts.status.base-url}")
    private String baseUrl;

    @Value("${nts.status.service-key}")
    private String serviceKey;

    public NtsStatusData inquireStatus(String rawBusinessNumber) {
        String bizNo = rawBusinessNumber.replaceAll("[^0-9]", "");
        if (bizNo.length() != 10) {
            throw new ApplicationException(ErrorCode.INVALID_BUSINESS_NUMBER);
        }

        String url = UriComponentsBuilder
                .fromHttpUrl(baseUrl + "/api/nts-businessman/v1/status")
                .queryParam("serviceKey", serviceKey)
                .build(true)
                .toUriString();

        NtsStatusRequest requestBody = new NtsStatusRequest(List.of(bizNo));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<NtsStatusRequest> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<NtsStatusResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    NtsStatusResponse.class
            );

            NtsStatusResponse body = response.getBody();
            if (body == null || body.getData() == null || body.getData().isEmpty()) {
                throw new ApplicationException(ErrorCode.BUSINESS_STATUS_CHECK_FAILED);
            }

            NtsStatusData data = body.getData().get(0);

            if (isNotRegistered(data)) {
                throw new ApplicationException(ErrorCode.INVALID_BUSINESS_NUMBER);
            }

            return data;
        } catch (RestClientException e) {
            throw new ApplicationException(ErrorCode.BUSINESS_STATUS_CHECK_FAILED);
        }
    }

    private boolean isNotRegistered(NtsStatusData data) {
        String status = data.getStatus();
        String taxType = data.getTaxType();
        String notRegisteredMessage = "국세청에 등록되지 않은 사업자등록번호입니다.";

        return (status != null && status.contains(notRegisteredMessage))
                || (taxType != null && taxType.contains(notRegisteredMessage));
    }
}
