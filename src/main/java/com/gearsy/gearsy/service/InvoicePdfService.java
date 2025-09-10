package com.gearsy.gearsy.service;

import com.gearsy.gearsy.entity.OrderDetails;
import com.gearsy.gearsy.entity.Orders;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.stream.Stream;

@Service
public class InvoicePdfService {

    public ByteArrayInputStream generateInvoicePdf(Orders order, List<OrderDetails> orderDetails) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph title = new Paragraph("Invoice", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            document.add(new Paragraph("Order ID: " + order.getOrderId()));
            document.add(new Paragraph("Customer: " + order.getUser().getName()));
            document.add(new Paragraph("Date: " + order.getOrderDate()));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setWidths(new int[]{4, 1, 2, 2});

            // Table Header
            Stream.of("Product", "Quantity", "Price", "Total").forEach(headerTitle -> {
                PdfPCell header = new PdfPCell();
                Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
                header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                header.setHorizontalAlignment(Element.ALIGN_CENTER);
                header.setPhrase(new Phrase(headerTitle, headFont));
                table.addCell(header);
            });

            // Table Rows
            for (OrderDetails detail : orderDetails) {
                table.addCell(detail.getProduct().getName());
                table.addCell(String.valueOf(detail.getQuantity()));
                table.addCell(String.format("%,.0f đ", detail.getUnitPrice().doubleValue()));
                double total = detail.getUnitPrice().doubleValue() * detail.getQuantity();
                table.addCell(String.format("%,.0f đ", total));
            }

            document.add(table);

            document.add(new Paragraph(" "));
            document.add(new Paragraph("Total: " + String.format("%,.0f đ", order.getTotalAmount().doubleValue())));

            document.close();

        } catch (DocumentException ex) {
            ex.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }
}
