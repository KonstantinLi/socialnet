package ru.skillbox.socialnet.dto;

import java.time.Duration;

public interface LogUploader {
    void uploadLog(String path);
    void deleteExpiredLogs(Duration expired);
}
