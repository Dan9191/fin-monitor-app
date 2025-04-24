package com.example.fin_monitor_app.reports;

import com.example.fin_monitor_app.entity.FinTransaction;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class HttpReportPdf extends HttpReport {
    public HttpReportPdf() {
        super("pdf");
    }

    @Override
    public void downloadTransactionDetailsReport(List<FinTransaction> transactions, HttpServletResponse response) throws IOException {
        response.reset();
        response.setContentType("application/pdf");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"");

        try (ServletOutputStream outputStream = response.getOutputStream()) {
            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdfDoc = new PdfDocument(writer);

            // Установка шрифта с поддержкой кириллицы
            PdfFont font = PdfFontFactory.createFont("fonts\\arial.ttf");

            // Установка альбомной ориентации (A4 в горизонтальном положении)
            PageSize pageSize = PageSize.A4.rotate();
            Document document = new Document(pdfDoc, pageSize);
            document.setFont(font);

            // Заголовок отчёта
            Paragraph header = new Paragraph("Финансовый отчёт")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(18)
                    .setBold();
            document.add(header);

            // Таблица с данными (6 колонок)
            Table table = new Table(UnitValue.createPercentArray(new float[]{15, 15, 10, 10, 30, 20}))
                    .setWidth(UnitValue.createPercentValue(100));

            // Заголовки колонок
            String[] headers = {"Дата", "Категория", "Тип", "Сумма", "Описание", "Кошелек"};
            for (String text : headers) {
                Cell cell = new Cell()
                        .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                        .setBold()
                        .setFont(font)
                        .setTextAlignment(TextAlignment.CENTER)
                        .add(new Paragraph(text));
                table.addHeaderCell(cell);
            }

            // Заполнение данными
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
            for (FinTransaction transaction : transactions) {
                table.addCell(new Cell().add(new Paragraph(transaction.getCreateDate().format(dateFormatter)).setFont(font)));
                table.addCell(new Cell().add(new Paragraph(transaction.getCategory().getName()).setFont(font)));
                table.addCell(new Cell().add(new Paragraph(transaction.getTransactionType().getName()).setFont(font)));
                table.addCell(new Cell().add(new Paragraph(String.format("%.2f", transaction.getSum().doubleValue())).setFont(font)));
                table.addCell(new Cell().add(new Paragraph(transaction.getCommentary() != null ? transaction.getCommentary() : "").setFont(font)));
                table.addCell(new Cell().add(new Paragraph(transaction.getBankAccount().getAccountNumber()).setFont(font)));
            }

            document.add(table);
            document.close();
        } catch (Exception e) {
            throw new IOException("Ошибка при генерации PDF", e);
        }
    }
}