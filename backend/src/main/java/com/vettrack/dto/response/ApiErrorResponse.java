package com.vettrack.dto.response;

import java.time.OffsetDateTime;
import java.util.Map;

public class ApiErrorResponse {
    private OffsetDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    private String requestId;
    private Map<String, String> fieldErrors;

    public ApiErrorResponse() {}

    public ApiErrorResponse(int status, String error, String message, String path, String requestId, Map<String, String> fieldErrors) {
        this.timestamp = OffsetDateTime.now();
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
        this.requestId = requestId;
        this.fieldErrors = fieldErrors;
    }

    public OffsetDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(OffsetDateTime timestamp) { this.timestamp = timestamp; }
    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }
    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }
    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }
    public Map<String, String> getFieldErrors() { return fieldErrors; }
    public void setFieldErrors(Map<String, String> fieldErrors) { this.fieldErrors = fieldErrors; }
}
