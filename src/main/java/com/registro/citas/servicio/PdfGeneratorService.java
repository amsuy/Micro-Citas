package com.registro.citas.servicio;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.draw.LineSeparator;
import com.registro.citas.dto.CitaProgramadaDTO;

public class PdfGeneratorService {

    public static byte[] generarPDF(CitaProgramadaDTO cita) throws Exception {
        Document doc = new Document(PageSize.A4, 50, 50, 50, 50);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(doc, out);
        doc.open();

        // ===== Logo =====
        try {
            InputStream is = PdfGeneratorService.class.getClassLoader().getResourceAsStream("logo.png");
            if (is != null) {
                Image logo = Image.getInstance(is.readAllBytes());
                logo.scaleToFit(150, 150);
                logo.setAlignment(Image.ALIGN_CENTER);
                doc.add(logo);
            }
        } catch (Exception e) {
            System.out.println("⚠️ Logo no cargado: " + e.getMessage());
        }

        // ===== Estilos =====
        Font titulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, Color.BLUE);
        Font subtitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.DARK_GRAY);
        Font texto = FontFactory.getFont(FontFactory.HELVETICA, 12, Color.BLACK);
        Font campo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.BLACK);

        // ===== Encabezado =====
        Paragraph header = new Paragraph("Hospital La Bendición", titulo);
        header.setAlignment(Element.ALIGN_CENTER);
        doc.add(header);

        Paragraph sub = new Paragraph("Confirmación de Cita Médica\n\n", subtitulo);
        sub.setAlignment(Element.ALIGN_CENTER);
        doc.add(sub);

        // ===== Línea divisoria =====
        LineSeparator separator = new LineSeparator();
        separator.setLineColor(Color.LIGHT_GRAY);
        doc.add(new Chunk(separator));
        doc.add(Chunk.NEWLINE);

        // ===== Contenido =====
        doc.add(new Paragraph("🧑 Paciente: ", campo));
        doc.add(new Paragraph(cita.getNombrecompletoPaciente(), texto));
        doc.add(Chunk.NEWLINE);

        doc.add(new Paragraph("🧾 NIT: ", campo));
        doc.add(new Paragraph(String.valueOf(cita.getNit()), texto));
        doc.add(Chunk.NEWLINE);

        doc.add(new Paragraph("👨‍⚕️ Doctor: ", campo));
        doc.add(new Paragraph(cita.getNombrecompletoDoctor(), texto));
        doc.add(Chunk.NEWLINE);

        doc.add(new Paragraph("📅 Fecha de cita: ", campo));
        doc.add(new Paragraph(cita.getFechacita().format(DateTimeFormatter.ofPattern("dd/MM/yyyy 'a las' HH:mm")), texto));
        doc.add(Chunk.NEWLINE);

        doc.add(new Paragraph("💬 Motivo de consulta: ", campo));
        doc.add(new Paragraph(cita.getMotivoconsulta(), texto));
        doc.add(Chunk.NEWLINE);

        // ===== Nota final =====
        doc.add(Chunk.NEWLINE);
        Paragraph nota = new Paragraph("📌 Por favor, llegue con al menos 10 minutos de anticipación.\nGracias por confiar en Hospital La Bendición.", texto);
        nota.setAlignment(Element.ALIGN_JUSTIFIED);
        doc.add(nota);

        doc.add(Chunk.NEWLINE);
        doc.add(new Paragraph("— Este es un mensaje automático, no responder directamente —", texto));

        doc.close();
        return out.toByteArray();
    }
}
