package com.example;


public class test1 {

    public static void main(String[] args) {
        String rawContent = "1. India is the seventh-largest country by land area and the second-most populous country in the world.*\n2. It is located in South Asia, sharing borders with Pakistan, China, Nepal, Bhutan, Bangladesh, and Myanmar.*\n3. India has a diverse culture with over 22 officially recognized languages and hundreds of dialects spoken across the country.*\n4. The country is known for its rich history, including ancient civilizations like the Indus Valley and significant empires such as the Maurya and Gupta dynasties.*\n5. India gained independence from British rule on August 15, 1947, and is the world's largest democracy.*\n6. The Indian economy is one of the fastest-growing in the world, with major sectors including information technology, agriculture, manufacturing, and services.*\n7. India is famous for its diverse cuisine, ranging from spicy curries and biryanis to sweets like jalebi and gulab jamun.*\n8. Major landmarks include the Taj Mahal, a UNESCO World Heritage Site and one of the New Seven Wonders of the World.*\n9. India is a leader in space research, with its space agency ISRO successfully launching numerous satellites and missions, including the Mars Orbiter Mission.*\n10. The country celebrates a variety of festivals such as Diwali, Holi, Eid, and Christmas, reflecting its multicultural and multi-religious society.*";

        // Split the string on the regex that matches the start of each point
        String[] splitPoints = rawContent.split("(?=\\d+\\. )");

        // Clean and assign
        for (int i = 0; i < splitPoints.length && i < 10; i++) {
            splitPoints[i] = splitPoints[i].replaceAll("^\\d+\\.\\s*", "")  // Remove leading number
                                           .replaceAll("\\*\\s*$", "")       // Remove trailing *
                                           .trim();
            System.out.println("Point " + (i + 1) + ": " + splitPoints[i]);
        }

        // // Optionally assign to variables
        // String point1 = splitPoints.length > 0 ? splitPoints[0] : "";
        // String point2 = splitPoints.length > 1 ? splitPoints[1] : "";
        // String point3 = splitPoints.length > 2 ? splitPoints[2] : "";
        // String point4 = splitPoints.length > 3 ? splitPoints[3] : "";
        // String point5 = splitPoints.length > 4 ? splitPoints[4] : "";
        // String point6 = splitPoints.length > 5 ? splitPoints[5] : "";
        // String point7 = splitPoints.length > 6 ? splitPoints[6] : "";
        // String point8 = splitPoints.length > 7 ? splitPoints[7] : "";
        // String point9 = splitPoints.length > 8 ? splitPoints[8] : "";
        // String point10 = splitPoints.length > 9 ? splitPoints[9] : "";
    }
}
