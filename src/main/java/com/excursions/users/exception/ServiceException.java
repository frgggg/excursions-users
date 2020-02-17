package com.excursions.users.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import static com.excursions.users.log.message.ServiceExceptionLogMessages.SERVICE_EXCEPTION_LOG;

@Slf4j
public class ServiceException extends RuntimeException {

    public static final String SERVICE_EXCEPTION_SERVICE_NAME_DEFAULT_VALUE = "service not set";

    @Getter
    private String serviceName = SERVICE_EXCEPTION_SERVICE_NAME_DEFAULT_VALUE;

    public ServiceException(String serviceName, String message) {
        super(message);
        if(serviceName != null) {
            this.serviceName = serviceName;
        }
        log.error(SERVICE_EXCEPTION_LOG, serviceName, message);
    }
}
