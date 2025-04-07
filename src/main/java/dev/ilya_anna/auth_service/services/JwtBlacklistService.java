package dev.ilya_anna.auth_service.services;

public interface JwtBlacklistService {
    void addAccess(String access);
    void addRefresh(String refresh);
    boolean validateAccess(String access);
    boolean validateRefresh(String refresh);
}
