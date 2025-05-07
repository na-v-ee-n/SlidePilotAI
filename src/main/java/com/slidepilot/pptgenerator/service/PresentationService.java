package com.slidepilot.pptgenerator.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.slidepilot.pptgenerator.model.PresentationRequest;
import org.apache.poi.sl.usermodel.PictureData;
import org.apache.poi.sl.usermodel.ShapeType;
import org.apache.poi.sl.usermodel.TextParagraph;
import org.apache.poi.xslf.usermodel.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class PresentationService {
    private static final String HEADING_FONT = "Fira Sans (Headings)";
    private static final String BODY_FONT = "Fira Sans (Body)";

    // Slide 2 Content
    public String strHeading2 = "What's new?";
    public String strBullet1;
    public String strBullet2;
    public String strBullet3;
    public String strBullet4;
    public String strBullet5;

    // Slide 3 Content
    public String strBullet6;
    public String strBullet7;
    public String strBullet8;
    public String strBullet9;

    // Add this field to your PresentationService class
    private String aiResponseRaw;

    // Add this method to your PresentationService class
    private String callAiAgent(String userTopic, String apiKey, String userId) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            // Set up headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Ensure proper header formatting
            headers.set("x-user-id", userId.trim());
            headers.set("x-authentication", "api-key " + apiKey.trim());

            // Log headers for debugging (remove in production)
            System.out.println("Request Headers:");
            System.out.println("x-user-id: " + userId.trim());
            System.out.println("x-authentication: api-key " + apiKey.trim());

            // Set up request body
            Map<String, Object> input = new HashMap<>();
            input.put("user_topic", userTopic);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("input", input);


            // Log request body for debugging
            ObjectMapper mapper = new ObjectMapper();
            System.out.println("Request Body: " + mapper.writeValueAsString(requestBody));

            // Create request entity
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

            // Make the API call
            ResponseEntity<String> response = restTemplate.postForEntity(
                    "https://p2ubkjgiucy7bb5tzy5z53czmq0umqtx.lambda-url.us-east-1.on.aws/agent/greybot/execute",
                    requestEntity,
                    String.class);

            // Parse response
            // JsonNode root = mapper.readTree(response.getBody());

            // Print raw response to console
            String rawResponse = response.getBody();
            System.out.println("AI Agent Raw Response:");
            System.out.println(rawResponse);

            return rawResponse;

        } catch (org.springframework.web.client.HttpClientErrorException.Unauthorized e) {
            System.err.println("Authentication failed with API. Please check your user ID and API key.");
            e.printStackTrace();
            return "Error: Authentication failed. Please check your API credentials.";
        } catch (Exception e) {
            System.err.println("Error calling AI agent: " + e.getMessage());
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    public String strBullet10;

    public byte[] generatePresentation(PresentationRequest request) throws IOException {
        String title = request.getTitle();
        String presenterName = request.getPresenterName();
        String conclusion = request.getConclusion();

        // Get API credentials from environment variables or configuration
        // This avoids hardcoding sensitive credentials in your code
        String apiKey = "C11D3F8C63ED7119A121990A:89b8e60adee214a35f0a99f0b2a68b15";
        String userId = "C11D3F8C63ED7119A121990A";

        // Fallback to default values for development only (remove in production)
        if (apiKey == null || apiKey.isEmpty()) {
            System.out.println("Warning: Using default API key. Set AI_AGENT_API_KEY environment variable.");
        }

        if (userId == null || userId.isEmpty()) {
            System.out.println("Warning: Using default user ID. Set AI_AGENT_USER_ID environment variable.");
        }

        // Call AI agent and store response
        this.aiResponseRaw = callAiAgent(title, apiKey, userId);
        dividePoints(aiResponseRaw);

        try (XMLSlideShow ppt = new XMLSlideShow()) {
            // Set slide size
            ppt.setPageSize(new Dimension(1280, 720));

            createTitleSlide(ppt, title, presenterName);
            createSecondSlide(ppt, title);
            createThirdSlide(ppt, title);
            createThankYouSlide(ppt, conclusion);

            // Save presentation to byte array
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ppt.write(out);
            return out.toByteArray();
        }
    }

    private void createSecondSlide(XMLSlideShow ppt, String title) throws IOException {

        XSLFSlide whatsNewSlide = ppt.createSlide();

        // Add white background
        XSLFAutoShape whiteBG = whatsNewSlide.createAutoShape();
        whiteBG.setShapeType(ShapeType.RECT);
        whiteBG.setAnchor(new Rectangle(0, 0, 1280, 720));
        whiteBG.setFillColor(Color.WHITE);

        // Add green line at the top left
        XSLFAutoShape greenLine = whatsNewSlide.createAutoShape();
        greenLine.setShapeType(ShapeType.LINE);
        greenLine.setAnchor(new Rectangle(40, 40, 420, 2));
        greenLine.setLineColor(new Color(133, 188, 32));// Green
        greenLine.setLineWidth(5.0);

        // Add blue line at the top right
        XSLFAutoShape blueLine = whatsNewSlide.createAutoShape();
        blueLine.setShapeType(ShapeType.LINE);
        blueLine.setAnchor(new Rectangle(505, 40, 735, 2));
        blueLine.setLineColor(new Color(0, 122, 195)); // Blue
        blueLine.setLineWidth(5.0);

        // Add "What's new?" title on the left
        XSLFTextBox titleBox = whatsNewSlide.createTextBox();
        titleBox.setAnchor(new Rectangle(40, 250, 350, 60));
        XSLFTextParagraph titlePara = titleBox.addNewTextParagraph();
        XSLFTextRun titleRun = titlePara.addNewTextRun();

        titleRun.setText(strHeading2);
        titleRun.setFontFamily(HEADING_FONT);
        titleRun.setFontSize(44.0);
        titleRun.setFontColor(Color.BLACK);

        // Add bullet points on the right
        XSLFTextBox bulletBox = whatsNewSlide.createTextBox();
        bulletBox.setAnchor(new Rectangle(505, 85, 735, 600));

        // Bullet point 1
        XSLFTextParagraph bulletPara1 = bulletBox.addNewTextParagraph();
        bulletPara1.setIndentLevel(0);
        bulletPara1.setBullet(true);
        XSLFTextRun bulletRun1 = bulletPara1.addNewTextRun();

        bulletRun1.setText(strBullet1);
        bulletRun1.setFontFamily(BODY_FONT);
        bulletRun1.setFontSize(22.0);

        // Bullet point 2
        XSLFTextParagraph bulletPara2 = bulletBox.addNewTextParagraph();
        bulletPara2.setIndentLevel(0);
        bulletPara2.setBullet(true);
        XSLFTextRun bulletRun2 = bulletPara2.addNewTextRun();
        bulletRun2.setText(strBullet2);
        bulletRun2.setFontFamily(BODY_FONT);
        bulletRun2.setFontSize(22.0);

        // Bullet point 3
        XSLFTextParagraph bulletPara3 = bulletBox.addNewTextParagraph();
        bulletPara3.setIndentLevel(0);
        bulletPara3.setBullet(true);
        XSLFTextRun bulletRun3 = bulletPara3.addNewTextRun();
        bulletRun3.setText(strBullet3);
        bulletRun3.setFontFamily(BODY_FONT);
        bulletRun3.setFontSize(22.0);

        // Bullet point 4
        XSLFTextParagraph bulletPara4 = bulletBox.addNewTextParagraph();
        bulletPara4.setIndentLevel(0);
        bulletPara4.setBullet(true);
        XSLFTextRun bulletRun4 = bulletPara4.addNewTextRun();
        bulletRun4.setText(strBullet4);
        bulletRun4.setFontFamily(BODY_FONT);
        bulletRun4.setFontSize(22.0);

        // Bullet point 5
        XSLFTextParagraph bulletPara5 = bulletBox.addNewTextParagraph();
        bulletPara5.setIndentLevel(0);
        bulletPara5.setBullet(true);
        XSLFTextRun bulletRun5 = bulletPara5.addNewTextRun();
        bulletRun5.setText(strBullet5);
        bulletRun5.setFontFamily(BODY_FONT);
        bulletRun5.setFontSize(22.0);

        // Add page number at bottom left
        XSLFTextBox pageNumBox = whatsNewSlide.createTextBox();
        pageNumBox.setAnchor(new Rectangle(100, 670, 50, 30));
        XSLFTextRun pageNumRun = pageNumBox.addNewTextParagraph().addNewTextRun();
        pageNumRun.setText("2");
        pageNumRun.setFontFamily(BODY_FONT);
        pageNumRun.setFontSize(14.0);
        pageNumRun.setFontColor(Color.BLACK);

        // Add "How to create a PowerPoint presentation Basic" at bottom right
        XSLFTextBox bottomTextBox = whatsNewSlide.createTextBox();
        bottomTextBox.setAnchor(new Rectangle(505, 670, 735, 30));
        XSLFTextParagraph bottomPara = bottomTextBox.addNewTextParagraph();
        bottomPara.setTextAlign(TextParagraph.TextAlign.RIGHT);
        XSLFTextRun bottomRun = bottomPara.addNewTextRun();
        bottomRun.setText(title);
        bottomRun.setFontFamily(BODY_FONT);
        bottomRun.setFontSize(14.0);
        bottomRun.setFontColor(Color.BLACK);
    }

    private void createTitleSlide(XMLSlideShow ppt, String title, String presenterName) throws IOException {
        XSLFSlide slide = ppt.createSlide();

        // Set background image
        byte[] bgBytes = getResourceBytes("static/images/background.png");
        XSLFPictureData bgPicData = ppt.addPicture(bgBytes, PictureData.PictureType.PNG);
        XSLFPictureShape bgShape = slide.createPicture(bgPicData);
        bgShape.setAnchor(new Rectangle(0, 0, 1280, 720));

        // White Left Panel
        XSLFAutoShape whiteBox = slide.createAutoShape();
        whiteBox.setShapeType(ShapeType.RECT);
        whiteBox.setAnchor(new Rectangle(30, 30, 380, 660));
        whiteBox.setFillColor(Color.WHITE);
        whiteBox.setLineColor(Color.WHITE);

        // Blue Line (Top Border)
        XSLFAutoShape blueLine = slide.createAutoShape();
        blueLine.setShapeType(ShapeType.LINE);
        blueLine.setAnchor(new Rectangle(50, 50, 320, 2));
        blueLine.setLineColor(new Color(0, 112, 195));
        blueLine.setLineWidth(5.0);

        // Title Text
        XSLFTextBox titleBox = slide.createTextBox();
        titleBox.setAnchor(new Rectangle(50, 70, 320, 300));
        XSLFTextParagraph titlePara = titleBox.addNewTextParagraph();
        titlePara.setTextAlign(TextParagraph.TextAlign.LEFT);
        XSLFTextRun titleRun = titlePara.addNewTextRun();
        titleRun.setText(title);
        titleRun.setFontFamily(HEADING_FONT);
        titleRun.setFontSize(39.0);
        titleRun.setFontColor(Color.BLACK);

        // Presenter Name Text Box
        XSLFTextBox nameBox = slide.createTextBox();
        nameBox.setAnchor(new Rectangle(50, 400, 320, 70));
        XSLFTextRun nameRun = nameBox.addNewTextParagraph().addNewTextRun();
        nameRun.setText(presenterName);
        nameRun.setFontFamily(BODY_FONT);
        nameRun.setFontSize(26.0);

        // Date Text
        XSLFTextBox dateBox = slide.createTextBox();
        dateBox.setAnchor(new Rectangle(50, 500, 320, 30));
        XSLFTextRun dateRun = dateBox.addNewTextParagraph().addNewTextRun();
        dateRun.setText(new SimpleDateFormat("MMMM yyyy").format(new Date()));
        dateRun.setFontFamily(BODY_FONT);
        dateRun.setFontSize(26.0);

        // Add logo image
        byte[] logoBytes = getResourceBytes("static/images/wolters_kluwer_logo_black.png");
        XSLFPictureData logoPicData = ppt.addPicture(logoBytes, PictureData.PictureType.PNG);
        XSLFPictureShape logoShape = slide.createPicture(logoPicData);

        // Get the original image dimensions to preserve aspect ratio
        Dimension origSize = logoShape.getPictureData().getImageDimension();
        double originalWidth = origSize.getWidth();
        double originalHeight = origSize.getHeight();

        // Set maximum width while preserving aspect ratio
        double maxWidth = 250.0;
        double scaleFactor = maxWidth / originalWidth;
        double newHeight = originalHeight * scaleFactor;

        // Position the logo at bottom-left with correct proportions
        logoShape.setAnchor(new Rectangle(40, 600, (int) maxWidth, (int) newHeight));
    }

    private void createThankYouSlide(XMLSlideShow ppt, String conclusion) throws IOException {
        XSLFSlide thankYouSlide = ppt.createSlide();

        // Set full black background
        XSLFAutoShape blackBG = thankYouSlide.createAutoShape();
        blackBG.setShapeType(ShapeType.RECT);
        blackBG.setAnchor(new Rectangle(0, 0, 1280, 720));
        blackBG.setFillColor(Color.BLACK);
        blackBG.setLineColor(Color.BLACK);

        // Red Line (Top Border)
        XSLFAutoShape redLine = thankYouSlide.createAutoShape();
        redLine.setShapeType(ShapeType.LINE);
        redLine.setAnchor(new Rectangle(50, 50, 220, 2));
        redLine.setLineColor(new Color(229, 32, 46));
        redLine.setLineWidth(5.0);

        // Green Line (Top Border)
        XSLFAutoShape greenLine = thankYouSlide.createAutoShape();
        greenLine.setShapeType(ShapeType.LINE);
        greenLine.setAnchor(new Rectangle(320, 50, 910, 2));
        greenLine.setLineColor(new Color(133, 188, 32));
        greenLine.setLineWidth(5.0);

        // "Thank you" text
        XSLFTextBox thanksBox = thankYouSlide.createTextBox();
        thanksBox.setAnchor(new Rectangle(50, 80, 700, 60));
        XSLFTextRun thanksRun = thanksBox.addNewTextParagraph().addNewTextRun();
        thanksRun.setText("Thank you");
        thanksRun.setFontFamily(HEADING_FONT);
        thanksRun.setFontSize(44.0);
        thanksRun.setFontColor(Color.WHITE);

        // Info text with conclusion
        XSLFTextBox infoBox = thankYouSlide.createTextBox();
        infoBox.setAnchor(new Rectangle(50, 150, 800, 120));
        XSLFTextParagraph infoPara = infoBox.addNewTextParagraph();
        XSLFTextRun infoRun = infoPara.addNewTextRun();
        infoRun.setText(conclusion);
        infoRun.setFontColor(Color.WHITE);
        infoRun.setFontSize(24.0);

        // Insert handshake image
        byte[] handshakeBytes = getResourceBytes("static/images/hands-shake-large-dark.png");
        XSLFPictureData handshakePic = ppt.addPicture(handshakeBytes, PictureData.PictureType.PNG);
        XSLFPictureShape handshakeShape = thankYouSlide.createPicture(handshakePic);
        handshakeShape.setAnchor(new Rectangle(950, 120, 250, 250));

        // Insert logo at bottom-left (same as first slide)
        byte[] logoBytes = getResourceBytes("static/images/wolters_kluwer_logo_white.png");
        XSLFPictureData logoPicData = ppt.addPicture(logoBytes, PictureData.PictureType.PNG);
        XSLFPictureShape logoShape = thankYouSlide.createPicture(logoPicData);

        // Get the original image dimensions to preserve aspect ratio
        Dimension origSize = logoShape.getPictureData().getImageDimension();
        double originalWidth = origSize.getWidth();
        double originalHeight = origSize.getHeight();

        // Set maximum width while preserving aspect ratio
        double maxWidth = 250.0;
        double scaleFactor = maxWidth / originalWidth;
        double newHeight = originalHeight * scaleFactor;

        // Position the logo at bottom-left with correct proportions
        logoShape.setAnchor(new Rectangle(40, 600, (int) maxWidth, (int) newHeight));
    }

    private void createThirdSlide(XMLSlideShow ppt, String title) throws IOException {

        XSLFSlide thirdSlide = ppt.createSlide();

        // Add white background
        XSLFAutoShape whiteBG = thirdSlide.createAutoShape();
        whiteBG.setShapeType(ShapeType.RECT);
        whiteBG.setAnchor(new Rectangle(0, 0, 1280, 720));
        whiteBG.setFillColor(Color.WHITE);

        // Add green line at the top left
        XSLFAutoShape greenLine = thirdSlide.createAutoShape();
        greenLine.setShapeType(ShapeType.LINE);
        greenLine.setAnchor(new Rectangle(40, 40, 1100, 2));
        greenLine.setLineColor(new Color(133, 188, 32));// Green
        greenLine.setLineWidth(5.0);

        // Insert Side image
        byte[] handshakeBytes = getResourceBytes("static/images/side_icon.png");
        XSLFPictureData handshakePic = ppt.addPicture(handshakeBytes, PictureData.PictureType.PNG);
        XSLFPictureShape handshakeShape = thirdSlide.createPicture(handshakePic);
        handshakeShape.setAnchor(new Rectangle(50, 200, 250, 250));

        // Add bullet points on the right
        XSLFTextBox bulletBox = thirdSlide.createTextBox();
        bulletBox.setAnchor(new Rectangle(505, 85, 735, 600));

        // Bullet point 1
        XSLFTextParagraph bulletPara1 = bulletBox.addNewTextParagraph();
        bulletPara1.setIndentLevel(0);
        bulletPara1.setBullet(true);
        XSLFTextRun bulletRun1 = bulletPara1.addNewTextRun();

        bulletRun1.setText(strBullet6);
        bulletRun1.setFontFamily(BODY_FONT);
        bulletRun1.setFontSize(22.0);

        // Bullet point 2
        XSLFTextParagraph bulletPara2 = bulletBox.addNewTextParagraph();
        bulletPara2.setIndentLevel(0);
        bulletPara2.setBullet(true);
        XSLFTextRun bulletRun2 = bulletPara2.addNewTextRun();
        bulletRun2.setText(strBullet7);
        bulletRun2.setFontFamily(BODY_FONT);
        bulletRun2.setFontSize(22.0);

        // Bullet point 3
        XSLFTextParagraph bulletPara3 = bulletBox.addNewTextParagraph();
        bulletPara3.setIndentLevel(1);
        bulletPara3.setBullet(true);
        XSLFTextRun bulletRun3 = bulletPara3.addNewTextRun();
        bulletRun3.setText(strBullet8);
        bulletRun3.setFontFamily(BODY_FONT);
        bulletRun3.setFontSize(22.0);

        // Bullet point 4
        XSLFTextParagraph bulletPara4 = bulletBox.addNewTextParagraph();
        bulletPara4.setIndentLevel(0);
        bulletPara4.setBullet(true);
        XSLFTextRun bulletRun4 = bulletPara4.addNewTextRun();
        bulletRun4.setText(strBullet9);
        bulletRun4.setFontFamily(BODY_FONT);
        bulletRun4.setFontSize(22.0);

        // Bullet point 5
        XSLFTextParagraph bulletPara5 = bulletBox.addNewTextParagraph();
        bulletPara5.setIndentLevel(0);
        bulletPara5.setBullet(true);
        XSLFTextRun bulletRun5 = bulletPara5.addNewTextRun();
        bulletRun5.setText(strBullet10);
        bulletRun5.setFontFamily(BODY_FONT);
        bulletRun5.setFontSize(22.0);

        // Add page number at bottom left
        XSLFTextBox pageNumBox = thirdSlide.createTextBox();
        pageNumBox.setAnchor(new Rectangle(100, 670, 50, 30));
        XSLFTextRun pageNumRun = pageNumBox.addNewTextParagraph().addNewTextRun();
        pageNumRun.setText("2");
        pageNumRun.setFontFamily(BODY_FONT);
        pageNumRun.setFontSize(14.0);
        pageNumRun.setFontColor(Color.BLACK);

        // Add "How to create a PowerPoint presentation Basic" at bottom right
        XSLFTextBox bottomTextBox = thirdSlide.createTextBox();
        bottomTextBox.setAnchor(new Rectangle(505, 670, 735, 30));
        XSLFTextParagraph bottomPara = bottomTextBox.addNewTextParagraph();
        bottomPara.setTextAlign(TextParagraph.TextAlign.RIGHT);
        XSLFTextRun bottomRun = bottomPara.addNewTextRun();
        bottomRun.setText(title);
        bottomRun.setFontFamily(BODY_FONT);
        bottomRun.setFontSize(14.0);
        bottomRun.setFontColor(Color.BLACK);

    }

    public void dividePoints(String apiResponse) {
         // Split the string on the regex that matches the start of each point
         String[] splitPoints = apiResponse.split("(?=\\d+\\. )");

         // Clean and assign
         for (int i = 0; i < splitPoints.length && i < 10; i++) {
             splitPoints[i] = splitPoints[i].replaceAll("^\\d+\\.\\s*", "")  // Remove leading number
                                            .replaceAll("\\*\\s*$", "")       // Remove trailing *
                                            .trim();
             System.out.println("Point " + (i + 1) + ": " + splitPoints[i]);
         }

        // // Optionally assign to variables
        strBullet1 = splitPoints.length > 0 ? splitPoints[0] : "";
        strBullet2 = splitPoints.length > 1 ? splitPoints[1] : "";
        strBullet3 = splitPoints.length > 2 ? splitPoints[2] : "";
        strBullet4 = splitPoints.length > 3 ? splitPoints[3] : "";
        strBullet5 = splitPoints.length > 4 ? splitPoints[4] : "";
        strBullet6 = splitPoints.length > 5 ? splitPoints[5] : "";
        strBullet7 = splitPoints.length > 6 ? splitPoints[6] : "";
        strBullet8 = splitPoints.length > 7 ? splitPoints[7] : "";
        strBullet9 = splitPoints.length > 8 ? splitPoints[8] : "";
        strBullet10 = splitPoints.length > 9 ? splitPoints[9] : "";

    }

    private byte[] getResourceBytes(String resourcePath) throws IOException {
        return new ClassPathResource(resourcePath).getInputStream().readAllBytes();
    }

}
