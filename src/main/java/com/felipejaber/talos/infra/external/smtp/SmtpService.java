package com.felipejaber.talos.infra.external.smtp;

public interface SmtpService {
    void sendEmail(String to, String subject, String body);
}
