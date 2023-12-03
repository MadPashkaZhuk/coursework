package com.zhuk.hospital.client;

import com.zhuk.hospital.dto.UpdateMedicationQuantityDto;
import com.zhuk.hospital.enums.ApiMessageEnum;
import com.zhuk.hospital.enums.ErrorCodeEnum;
import com.zhuk.hospital.exception.BaseApiException;
import com.zhuk.hospital.exception.medication.*;
import com.zhuk.hospital.utils.ErrorCodeHelper;
import com.zhuk.hospital.utils.MessageSourceWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@RequiredArgsConstructor
public class MedicationRestClient {
    private final MedicationApiProperties medicationApiProperties;
    private final MessageSourceWrapper messageSourceWrapper;
    private final ErrorCodeHelper errorCodeHelper;
    private final RestTemplate restTemplate;

    public void reduceQuantity(Long medicationId, Integer quantity) {
        updateQuantity(medicationId, -quantity);
    }
    public void increaseQuantity(Long medicationId, Integer quantity) {
        updateQuantity(medicationId, quantity);
    }

    private void updateQuantity(Long medicationId, Integer quantity) {
        try {
            restTemplate.exchange(
                    getDefaultUriForMedicationId(medicationId),
                    HttpMethod.PATCH,
                    getHttpEntityForQuantity(quantity),
                    Void.class);
        }
        catch (HttpStatusCodeException ex) {
            throw getProperExceptionFromApi(ex);
        } catch (Throwable ex) {
            throw new MedicationUnknownException(HttpStatus.INTERNAL_SERVER_ERROR,
                    messageSourceWrapper.getMessageCode(ApiMessageEnum.MEDICATION_UNKNOWN_EXCEPTION),
                    errorCodeHelper.getCode(ErrorCodeEnum.UNKNOWN_ERROR_CODE));
        }
    }
    private HttpEntity<UpdateMedicationQuantityDto> getHttpEntityForQuantity(Integer quantity) {
        UpdateMedicationQuantityDto dto = UpdateMedicationQuantityDto.builder()
                .quantity(quantity)
                .build();
        return new HttpEntity<>(dto, getAuthHeaders());
    }

    private BaseApiException getProperExceptionFromApi(HttpStatusCodeException ex) {
        BaseApiException exception;
        if(ex.getStatusCode() == HttpStatus.BAD_REQUEST) {
            exception = new MedicationBadRequestException(HttpStatus.BAD_REQUEST,
                    messageSourceWrapper.getMessageCode(ApiMessageEnum.MEDICATION_BAD_REQUEST),
                    errorCodeHelper.getCode(ErrorCodeEnum.MEDICATION_BAD_REQUEST_CODE));
        } else if(ex.getStatusCode() == HttpStatus.UNAUTHORIZED) {
            exception = new MedicationUnauthorizedException(HttpStatus.UNAUTHORIZED,
                    messageSourceWrapper.getMessageCode(ApiMessageEnum.MEDICATION_UNAUTHORIZED),
                    errorCodeHelper.getCode(ErrorCodeEnum.MEDICATION_UNAUTHORIZED_CODE));
        } else if(ex.getStatusCode() == HttpStatus.FORBIDDEN) {
            exception = new MedicationForbiddenException(HttpStatus.FORBIDDEN,
                    messageSourceWrapper.getMessageCode(ApiMessageEnum.MEDICATION_FORBIDDEN),
                    errorCodeHelper.getCode(ErrorCodeEnum.MEDICATION_FORBIDDEN_CODE));
        } else if(ex.getStatusCode() == HttpStatus.NOT_FOUND) {
            exception = new MedicationNotFoundException(HttpStatus.NOT_FOUND,
                    messageSourceWrapper.getMessageCode(ApiMessageEnum.MEDICATION_NOT_FOUND),
                    errorCodeHelper.getCode(ErrorCodeEnum.MEDICATION_NOT_FOUND_CODE));
        }
        else {
            exception = new MedicationUnknownException(HttpStatus.INTERNAL_SERVER_ERROR,
                    messageSourceWrapper.getMessageCode(ApiMessageEnum.MEDICATION_UNKNOWN_EXCEPTION),
                    errorCodeHelper.getCode(ErrorCodeEnum.UNKNOWN_ERROR_CODE));
        }
        return exception;
    }

    private HttpHeaders getAuthHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(medicationApiProperties.getUsername(), medicationApiProperties.getPassword());
        return headers;
    }

    private String getDefaultUriForMedicationId(Long medicationId) {
        return UriComponentsBuilder.fromUriString(medicationApiProperties.getUrl() + "/"+ medicationId.toString())
                .toUriString();
    }
}
