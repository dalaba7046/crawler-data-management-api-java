package com.portfolio.crawlerdata.exception;

/**
 * 找不到資源時拋出，對應 FastAPI 版的 HTTPException(status_code=404)。
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
