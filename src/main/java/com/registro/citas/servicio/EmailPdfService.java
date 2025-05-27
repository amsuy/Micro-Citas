package com.registro.citas.servicio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;

@Service
public class EmailPdfService {

    @Autowired
    private JavaMailSender mailSender;

    public void enviarPdfPorCorreo(String destinatario, String asunto, String mensaje, byte[] pdf) throws Exception {
        MimeMessage correo = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(correo, true);

        helper.setTo(destinatario);
        helper.setSubject(asunto);
        helper.setText(mensaje, false);

        helper.addAttachment("CitaProgramada.pdf", new ByteArrayDataSource(pdf, "application/pdf"));

        mailSender.send(correo);
    }
}
