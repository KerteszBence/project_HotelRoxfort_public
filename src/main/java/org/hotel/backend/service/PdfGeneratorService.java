package org.hotel.backend.service;


import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.hotel.backend.domain.BookingExtra;
import org.hotel.backend.domain.BookingRoomUser;
import org.hotel.backend.dto.ExchangeRatesHufEur;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Transactional
public class PdfGeneratorService {
    private static Integer billNumber = 1;

    public static InputStreamSource createBillPdf(BookingRoomUser bookingRoomUser, List<BookingExtra> bookingExtraList, ExchangeService exchangeService) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {

                float pageWidth = page.getMediaBox().getWidth();
                float pageHeight = page.getMediaBox().getHeight();
                PDFont font = PDType1Font.HELVETICA_BOLD;
                float fontSize = 10;
                float margin = 40;
                float marginRight = 200;
                float lineHeight = 14;

                String imageUrl = "https://res.cloudinary.com/de8m1v9be/image/upload/s--XuYDw1Pa--/v1705709418/%22logo%22/urpgpcym4xscccsow9nl.jpg";
                PDImageXObject pdImage = createImageFromURL(imageUrl, document);
                float imageWidth = pdImage.getWidth();
                float imageHeight = pdImage.getHeight();
                contentStream.drawImage(pdImage, (pageWidth - imageWidth) / 2, (pageHeight - imageHeight - margin));

                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, fontSize);
                contentStream.newLineAtOffset(page.getMediaBox().getWidth() - marginRight, 530);

                LocalDateTime checkIn = bookingRoomUser.getInDate();
                LocalDateTime checkOut = bookingRoomUser.getOutDate();
                String checkOutDate = checkOut.toLocalDate().toString();
                contentStream.showText("Bill No.: " + checkOut.getYear() + "/" + billNumber);
                billNumber++;
                contentStream.newLineAtOffset(0, -lineHeight);
                contentStream.showText("Date: " + checkOutDate);
                contentStream.newLineAtOffset(0, -lineHeight);
                Long roomNumber = bookingRoomUser.getRoom().getRoomNumber();
                contentStream.showText("Room number: " + roomNumber);
                contentStream.newLineAtOffset(0, -lineHeight);
                int numberOfGuests = bookingRoomUser.getRoom().getCapacity();
                contentStream.showText("Number of guests: " + numberOfGuests);
                contentStream.endText();

                writeTextToPdf(contentStream, fontSize, margin, 550, "BILL to");

                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, fontSize);
                contentStream.newLineAtOffset(margin, 530);
                String name = bookingRoomUser.getAppUser().getFirstName() + " " + bookingRoomUser.getAppUser().getLastName();
                contentStream.showText(name);
                contentStream.newLineAtOffset(0, -lineHeight);
                String email = bookingRoomUser.getAppUser().getEmail();
                contentStream.showText("E-mail: " + email);
                contentStream.newLineAtOffset(0, -lineHeight);
                String checkInDate = checkIn.toLocalDate().toString();
                contentStream.showText("Check in date: " + checkInDate);
                contentStream.newLineAtOffset(0, -lineHeight);
                contentStream.showText("Check out date: " + checkOutDate);
                contentStream.newLineAtOffset(0, -lineHeight);

                contentStream.endText();

                float yStart1 = 450;
                float xEnd = pageWidth - margin;
                float yEnd = yStart1;
                drawLine(contentStream, margin, yStart1, xEnd, yEnd);

                writeTextToPdf(contentStream, fontSize, margin, 435, "Items");
                writeTextToPdf(contentStream, fontSize, margin, 400, "Hotel Nights");
                writeTextToPdf(contentStream, fontSize, margin, 380, "Discount");
                int yCoordinate = 360;
                int yRow = yCoordinate;
                int blankLine = 20;
                for (int i = 0; i < bookingExtraList.size(); i++) {
                    writeTextToPdf(contentStream, fontSize, margin, yRow, bookingExtraList.get(i).getExtra().getExtraDescription());
                    yRow -= blankLine;
                }

                float xCol2 = 230;
                float xCol3 = 320;
                float xCol4 = 440;
                String quantityText = "Quantity";
                float quantityTextWidth = PDType1Font.HELVETICA_BOLD.getStringWidth(quantityText) / 1000 * fontSize;
                writeTextToPdf(contentStream, fontSize, (xCol3 - xCol2 - quantityTextWidth) / 2 + xCol2, 435, quantityText);
                long numberOfNights = calculateNights(checkIn, checkOut);
                String numberOfNightsText = Long.toString(numberOfNights);
                float numberOfNightsTextWidth = PDType1Font.HELVETICA_BOLD.getStringWidth(numberOfNightsText) / 1000 * fontSize;
                writeTextToPdf(contentStream, fontSize, (xCol3 - xCol2 - numberOfNightsTextWidth) / 2 + xCol2, 400, numberOfNightsText);
                Integer discount = 0;
                String discountText = discount + " %";
                float discountTextWidth = PDType1Font.HELVETICA_BOLD.getStringWidth(discountText) / 1000 * fontSize;
                writeTextToPdf(contentStream, fontSize, (xCol3 - xCol2 - discountTextWidth) / 2 + xCol2, 380, discountText);
                yRow = yCoordinate;
                for (int i = 0; i < bookingExtraList.size(); i++) {
                    String dataText = bookingExtraList.get(i).getQuantity().toString();
                    float dataTextWidth = PDType1Font.HELVETICA_BOLD.getStringWidth(dataText) / 1000 * fontSize;
                    writeTextToPdf(contentStream, fontSize, (xCol3 - xCol2 - dataTextWidth) / 2 + xCol2, yRow, dataText);
                    yRow -= blankLine;
                }

                //float xCol3 = 370;
                String priceUsdText = "Price (USD)";
                float priceUsdTextWidth = PDType1Font.HELVETICA_BOLD.getStringWidth(priceUsdText) / 1000 * fontSize;
                writeTextToPdf(contentStream, fontSize, (xCol4 - xCol3 - priceUsdTextWidth) / 2 + xCol3, 435, priceUsdText);
                double pricePerNight = Math.round(bookingRoomUser.getRoom().getPricePerNight() * 100.0) / 100.0;
                String pricePerNightText = Double.toString(pricePerNight);
                float pricePerNightTextWidth = PDType1Font.HELVETICA_BOLD.getStringWidth(pricePerNightText) / 1000 * fontSize;
                writeTextToPdf(contentStream, fontSize, (xCol4 - xCol3 - pricePerNightTextWidth) / 2 + xCol3, 400, pricePerNightText);
                String discountValueText = Double.toString(0.00);
                float discountValueTextWidth = PDType1Font.HELVETICA_BOLD.getStringWidth(discountValueText) / 1000 * fontSize;
                writeTextToPdf(contentStream, fontSize, (xCol4 - xCol3 - discountValueTextWidth) / 2 + xCol3, 380, discountValueText);
                yRow = yCoordinate;
                for (int i = 0; i < bookingExtraList.size(); i++) {
                    String dataText = bookingExtraList.get(i).getQuantity().toString();
                    float dataTextWidth = PDType1Font.HELVETICA_BOLD.getStringWidth(dataText) / 1000 * fontSize;
                    writeTextToPdf(contentStream, fontSize, (xCol4 - xCol3 - dataTextWidth) / 2 + xCol3, yRow, Double.toString(bookingExtraList.get(i).getExtra().getPrice()));
                    yRow -= blankLine;
                }

                //float xCol4 = 490;
                String amountText = "Amount (USD)";
                float amountTextWidth = calculateTextWidth(font, fontSize, amountText);
                writeTextToPdf(contentStream, fontSize, (pageWidth - margin - xCol4 - amountTextWidth) / 2 + xCol4, 435, amountText);
                double roomAmount = Math.round((numberOfNights * pricePerNight) * 100.0) / 100.0;
                String roomAmountText = Double.toString(roomAmount);
                float roomAmountTextWidth = calculateTextWidth(font, fontSize, roomAmountText);
                writeTextToPdf(contentStream, fontSize, (pageWidth - margin - xCol4 - roomAmountTextWidth) / 2 + xCol4, 400, roomAmountText);
                String discountAmountText = Double.toString(0.0);
                float discountAmountTextWith = calculateTextWidth(font, fontSize, discountAmountText);
                writeTextToPdf(contentStream, fontSize, (pageWidth - margin - xCol4 - discountAmountTextWith) / 2 + xCol4, 380, discountAmountText);
                yRow = yCoordinate;
                for (int i = 0; i < bookingExtraList.size(); i++) {
                    int quantity = bookingExtraList.get(i).getQuantity();
                    double price = bookingExtraList.get(i).getExtra().getPrice();
                    double subAmount = Math.round((quantity * price) * 100.0) / 100.0;
                    String subAmountText = Double.toString(subAmount);
                    float subAmountTextWidth = calculateTextWidth(font, fontSize, subAmountText);
                    writeTextToPdf(contentStream, fontSize, (pageWidth - margin - xCol4 - subAmountTextWidth) / 2 + xCol4, yRow, subAmountText);
                    yRow -= blankLine;
                }

                drawLine(contentStream, margin, yStart1 - 25, xEnd, yEnd - 25);

                float yStart2 = 220;
                float yEnd2 = yStart2;
                drawLine(contentStream, margin, yStart2, xEnd, yEnd2);

                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, fontSize);
                contentStream.newLineAtOffset(pageWidth - marginRight, 190);
                double sumAmount = 0.0;
                for (int i = 0; i < bookingExtraList.size(); i++) {
                    int quantity = bookingExtraList.get(i).getQuantity();
                    double price = bookingExtraList.get(i).getExtra().getPrice();
                    double amount = quantity * price;
                    sumAmount += amount;
                }
                double subtotal = Math.round((roomAmount + sumAmount) * 100.0) / 100.0;
                String formattedSubtotal = formatNumber(subtotal);
                contentStream.showText("Subtotal: " + formattedSubtotal);
                contentStream.newLineAtOffset(0, -lineHeight);
                double tax = Math.round((subtotal * 0.1) * 100.0) / 100.0;
                String formattedTax = formatNumber(tax);
                contentStream.showText("Magical Tax: " + formattedTax);
                contentStream.newLineAtOffset(0, -lineHeight);
                double totalAmount = Math.round((subtotal + tax) * 100.0) / 100.0;
                String formattedTotalAmount = formatNumber(totalAmount);
                contentStream.showText("Total Amount (USD): " + formattedTotalAmount);
                contentStream.newLineAtOffset(0, -lineHeight);
                ExchangeRatesHufEur exchangeRatesHufEur = exchangeService.getHufEurRates();
                double eurRate = exchangeRatesHufEur.getEurRate();
                double hufRate = exchangeRatesHufEur.getHufRate();
                double totalAmountEur = Math.round((totalAmount * eurRate) * 100.0) / 100.0;
                String formattedTotalAmountEur = formatNumber(totalAmountEur);
                contentStream.showText("Total Amount (EUR): " + formattedTotalAmountEur);
                contentStream.newLineAtOffset(0, -lineHeight);
                long roundedTotalAmountHuf = Math.round(totalAmount * hufRate);
                String formattedTotalAmountHuf = formatRoundedNumber(roundedTotalAmountHuf);
                contentStream.showText("Total Amount (HUF): " + formattedTotalAmountHuf);
                contentStream.newLineAtOffset(0, -lineHeight);
                double totalAmountGalleon = Math.round((totalAmount * 0.6) * 100.0) / 100.0;
                String formattedTotalAmountGalleon = formatNumber(totalAmountGalleon);
                contentStream.showText("Total Amount (Galleon): " + formattedTotalAmountGalleon);
                contentStream.endText();

                writeTextToPdf(contentStream, 10, margin, 80, "Thank you for choosing Hotel Roxfort for your magical stay. ");
                writeTextToPdf(contentStream, 10, margin, 60, "We look forward to welcoming you back in the future.");
                writeTextToPdf(contentStream, 10, margin, 40, "The Hotel ROXFORT at Budapest.");
            }
            document.save(outputStream);
        }
        return new ByteArrayResource(outputStream.toByteArray());
    }

    private static void drawLine(PDPageContentStream contentStream, float xStart, float yStart1, float xEnd, float yEnd) throws IOException {
        contentStream.moveTo(xStart, yStart1);
        contentStream.lineTo(xEnd, yEnd);
        contentStream.stroke();
    }

    private static void writeTextToPdf(PDPageContentStream contentStream, float fontSize, float xCoordinate, int yCoordinate, String text) throws IOException {
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, fontSize);
        contentStream.newLineAtOffset(xCoordinate, yCoordinate);
        contentStream.showText(text);
        contentStream.endText();
    }

    public static long calculateNights(LocalDateTime checkIn, LocalDateTime checkOut) {
        return ChronoUnit.DAYS.between(checkIn, checkOut);
    }

    private static PDImageXObject createImageFromURL(String imageUrl, PDDocument document) throws IOException {
        URL url = new URL(imageUrl);
        BufferedImage image = ImageIO.read(url);
        return LosslessFactory.createFromImage(document, image);
    }

    private static float calculateTextWidth(PDFont font, float fontSize, String text) throws IOException {
        return font.getStringWidth(text) / 1000 * fontSize;
    }

    private static String formatNumber(double number) {
        DecimalFormat decimalFormat = new DecimalFormat("#,###.##");
        return decimalFormat.format(number);
    }

    private static String formatRoundedNumber(double number) {
        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        return decimalFormat.format(number);
    }
}