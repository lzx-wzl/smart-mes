package com.lzx.smartmes.integration.exception;

import com.lzx.smartmes.common.exception.BusinessException;

public class IntegrationException extends BusinessException {

    public IntegrationException(String message) {
        super(message);
    }
}