package org.hotel.backend.service;


import org.hotel.backend.domain.AppUser;
import org.hotel.backend.domain.BookingExtra;
import org.hotel.backend.domain.BookingRoomUser;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.util.List;

import static org.hotel.backend.service.PdfGeneratorService.createBillPdf;


@Service
@Transactional
public class EmailService {

    private final JavaMailSender javaMailSender;

    private final ExchangeService exchangeService;

    public EmailService(JavaMailSender javaMailSender, ExchangeService exchangeService) {
        this.javaMailSender = javaMailSender;
        this.exchangeService = exchangeService;
    }

    @Async
    public void sendEmail(String to, String subject, String body) throws MessagingException, UnsupportedEncodingException {

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
        mimeMessageHelper.setFrom("projecthotel.roxfort@gmail.com", "ProjectRoxfort");
        mimeMessageHelper.setTo(to);
        mimeMessageHelper.setSubject(subject);
        mimeMessageHelper.setText(body, true);
        javaMailSender.send(mimeMessage);
    }

    @Async
    public void sendEmailWithAttachment(BookingRoomUser bookingRoomUser, List<BookingExtra> bookingExtraList)
            throws MessagingException, IOException {

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        String mailContent = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Hotel Roxfort - Welcome!</title>\n" +
                "    <style>\n" +
                "        body {\n" +
                "            font-family: 'Arial', sans-serif;\n" +
                "            background-color: #f2f2f2;\n" +
                "            margin: 0;\n" +
                "            padding: 0;\n" +
                "        }\n" +
                "\n" +
                "        .container {\n" +
                "            max-width: 600px;\n" +
                "            margin: 20px auto;\n" +
                "            background-color: #fff;\n" +
                "            border-radius: 8px;\n" +
                "            overflow: hidden;\n" +
                "        }\n" +
                "\n" +
                "        .header {\n" +
                "            background-color: #342929;\n" +
                "            color: #fff;\n" +
                "            text-align: center;\n" +
                "            padding: 20px;\n" +
                "        }\n" +
                "\n" +
                "        .logo {\n" +
                "            max-width: 100px;\n" +
                "            height: auto;\n" +
                "        }\n" +
                "\n" +
                "        h1 {\n" +
                "            margin: 0;\n" +
                "            font-size: 24px;\n" +
                "        }\n" +
                "\n" +
                "        .content {\n" +
                "            padding: 20px;\n" +
                "        }\n" +
                "\n" +
                "        .welcome-text {\n" +
                "            font-size: 16px;\n" +
                "            line-height: 1.5;\n" +
                "            margin-bottom: 15px;\n" +
                "        }\n" +
                "\n" +
                "        .verification-link {\n" +
                "            display: inline-block;\n" +
                "            padding: 10px 20px;\n" +
                "            background-color: #342929;\n" +
                "            color: #fff !important;\n" +
                "            text-decoration: none;\n" +
                "            border-radius: 5px;\n" +
                "        }\n" +
                "\n" +
                "        .verification-link:hover {\n" +
                "            background-color: #2fa64d;\n" +
                "        }\n" +
                "\n" +
                "        .footer {\n" +
                "            text-align: center;\n" +
                "            padding: 10px;\n" +
                "            background-color: #f2f2f2;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "\n" +
                "<body>\n" +
                "    <div class=\"container\">\n" +
                "        <div class=\"header\">\n" +
                "            <h1>Hotel Roxfort</h1>\n" +
                "        </div>\n" +
                "        <div class=\"content\">\n" +
                "            <p class=\"welcome-text\">Dear " + " <strong> " + bookingRoomUser.getAppUser().getFirstName() + " " + bookingRoomUser.getAppUser().getLastName() + "</strong>,</p>\n" +
                "            <p class=\"welcome-text\">We look forward to welcoming you back in the future. <br>It was our pleasure to have you as our guest at Hotel Roxfort.</p>\n" +
                "            <p class=\"welcome-text\">We are pleased to provide you with the bill for your recent accommodation.</p>\n" +
                "        </div>\n" +
                "        <div class=\"footer\">\n" +
                "            &copy; 2024 Hotel Roxfort. All rights reserved.\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</body>\n" +
                "\n" +
                "</html>";
       /* String mailContent = "<p>Dear Guest,</p>" +
                "<p>We hope this email finds you well. It was our pleasure to have you as our guest at Hotel Roxfort,</p>" +
                "<p>and we trust that you had a delightful stay with us.</p>" +
                "<p>As part of our commitment to providing excellent service,</p>" +
                "<p>we are pleased to provide you with the bill for your recent accommodation.</p>" +
                "<p>There you will find a detailed breakdown of the charges incurred during your stay.</p>" +
                "<p></p>" +
                "<p>Thank you for choosing Hotel Roxfort for your stay.</p>" +
                "<p>We look forward to welcoming you back in the future.</p>" +
                "<p>Best Regards,</p>" +
                "<p>Hotel Roxfort</p>";

        */

        // Beállítjuk a címzettet, tárgyat és szöveget
        AppUser appUser = bookingRoomUser.getAppUser();
        helper.setTo(appUser.getEmail());
        helper.setSubject("Bill for Your Recent Stay at Hotel Roxfort");
        helper.setText(mailContent, true);
        // Hozzáadjuk a PDF-et csatolmányként
        helper.addAttachment("bill_" + appUser.getFirstName() + appUser.getLastName() + LocalDate.now() + ".pdf",
                createBillPdf(bookingRoomUser, bookingExtraList, exchangeService),
                "application/pdf");

        // Elküldjük az e-mailt
        javaMailSender.send(message);
    }
}