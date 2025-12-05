package com.felipejaber.talos.infra.external.smtp.impl;

import com.felipejaber.talos.infra.external.smtp.SmtpService;
import org.springframework.stereotype.Service;

@Service
public class SmtpServiceImpl implements SmtpService {
    @Override
    public void sendEmail(String to, String subject, String body) {

    }
}
