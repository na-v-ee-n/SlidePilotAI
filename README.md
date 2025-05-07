# SlidePilotAI

SlidePilot is a sophisticated presentation automation system built with Spring Boot that leverages modern software engineering principles and an external AI agent to transform raw input into structured and visually appealing PowerPoint presentations.

## Project Overview

This project provides a web-based interface for users to input a topic, presenter name, and conclusion. Upon submission, the application interacts with an external AI agent to generate relevant content for the presentation slides. This content is then programmatically inserted into a predefined PowerPoint template structure using the Apache POI library. The generated presentation (in `.pptx` format) is then available for download by the user.

The architecture follows a standard Spring Boot MVC pattern, with dedicated components for handling web requests, business logic, and data interaction (though data persistence is minimal in this specific implementation). The core functionality revolves around the `PresentationService`, which orchestrates the AI agent call and the PowerPoint generation process.

## Technologies Used

*   **Spring Boot:** Provides the foundation for building the web application, including dependency injection, auto-configuration, and an embedded server.
*   **Apache POI:** A powerful Java API for manipulating various file formats based on Microsoft's Office Open XML (OOXML) standards, specifically used here for creating and modifying `.pptx` files.
*   **Thymeleaf:** A server-side Java template engine used for rendering the HTML front-end.
*   **Jackson:** A suite of data-processing tools for Java (and the JVM) for handling JSON data, used here for serializing and deserializing the request and response to/from the AI agent.
*   **Lombok:** A library that helps reduce boilerplate code for Java objects (e.g., getters, setters, constructors).
*   **Maven:** The build automation tool used to manage project dependencies and the build lifecycle.
*   **External AI Agent:** An external service (accessed via REST API) responsible for generating presentation content based on the user's input topic.

## Project Structure

The project follows a standard Maven project structure. Key directories and files include:

*   `src/main/java/com/slidepilot/pptgenerator/`: Contains the main application code.
    *   `PptGeneratorApplication.java`: The main entry point for the Spring Boot application.
    *   `config/`: Contains configuration classes.
        *   `AppConfig.java`: Configures essential beans like `RestTemplate` and `ObjectMapper`.
    *   `controller/`: Contains the web controller.
        *   `PresentationController.java`: Handles incoming HTTP requests and orchestrates the presentation generation process.
    *   `model/`: Contains data model classes.
        *   `PresentationRequest.java`: Represents the user's input for generating a presentation.
    *   `service/`: Contains the core business logic.
        *   `PresentationService.java`: Encapsulates the logic for calling the AI agent and generating the PowerPoint presentation.
*   `src/main/resources/`: Contains static resources, templates, and configuration files.
    *   `application.properties`: Spring Boot configuration file.
    *   `static/`: Contains static assets like CSS, JavaScript, and images.
        *   `images/`: Stores images used in the presentation templates.
    *   `templates/`: Contains Thymeleaf templates for rendering HTML views.
        *   `index.html`: The main form for user input.
*   `pom.xml`: The Maven project object model file, defining dependencies, build plugins, and project metadata.

## Core Functionality Details

### Entry Point (`PptGeneratorApplication.java`)

This class is the standard Spring Boot entry point. The `main` method uses `SpringApplication.run()` to start the embedded Tomcat server and initialize the application context. The `@SpringBootApplication` annotation combines `@Configuration`, `@EnableAutoConfiguration`, and `@ComponentScan`, enabling Spring's core features and scanning for components within the `com.slidepilot.pptgenerator` package and its sub-packages.

### Configuration (`AppConfig.java`)

The `AppConfig` class is annotated with `@Configuration`, indicating that it provides bean definitions.

*   `restTemplate()`: Defines a `RestTemplate` bean, which is a synchronous client for performing HTTP requests. This is used by the `PresentationService` to interact with the external AI agent API.
*   `objectMapper()`: Defines an `ObjectMapper` bean from the Jackson library. This is used for converting Java objects to and from JSON format, essential for handling the request and response bodies when communicating with the AI agent.

### Controller (`PresentationController.java`)

This class is annotated with `@Controller`, marking it as a web request handler.

*   `showForm(Model model)`: Handles GET requests to the root path (`/`). It prepares the `index.html` template by adding a `PresentationRequest` object to the model, which is used to bind form data.
*   `generatePresentation(@ModelAttribute PresentationRequest request)`: Handles POST requests to the `/generate` path. It receives the user's input bound to a `PresentationRequest` object. It then delegates the presentation generation logic to the `PresentationService`. The generated PowerPoint file (as a byte array) is returned as a `ResponseEntity` with appropriate headers (`Content-Disposition` for file download and `Content-Type` for the .pptx format).

### Model (`PresentationRequest.java`)

This simple POJO (Plain Old Java Object) represents the data captured from the user input form. The `@Data` annotation from Lombok automatically generates getters, setters, `equals()`, `hashCode()`, and `toString()` methods.

### Service (`PresentationService.java`)

This is the core business logic component, annotated with `@Service`.

*   `generatePresentation(PresentationRequest request)`: This is the main method responsible for the entire presentation generation process.
    *   It retrieves the title, presenter name, and conclusion from the `PresentationRequest`.
    *   **AI Agent Interaction:** It calls the `callAiAgent()` method to interact with the external AI agent. **Note:** The API key and user ID are currently hardcoded. **For production environments, these sensitive credentials should be managed securely using environment variables, configuration server, or other secure methods.**
    *   **Content Parsing:** The raw response from the AI agent is then processed by the `dividePoints()` method to extract the bullet points for the presentation slides.
    *   **PowerPoint Creation:** It uses `Apache POI` (`XMLSlideShow`) to create a new PowerPoint presentation.
    *   **Slide Generation:** It calls dedicated private methods (`createTitleSlide`, `createSecondSlide`, `createThirdSlide`, `createThankYouSlide`) to construct each slide with specific layouts and content.
    *   **Output:** The generated presentation is written to a `ByteArrayOutputStream` and returned as a byte array.
*   `callAiAgent(String userTopic, String apiKey, String userId)`: This private method handles the communication with the external AI agent.
    *   It uses the configured `RestTemplate` to send a POST request to the specified AI agent URL.
    *   It sets the required `HttpHeaders`, including `Content-Type` as `application/json` and custom headers for authentication (`x-user-id` and `x-authentication`).
    *   It constructs the request body as a JSON object containing the `user_topic`.
    *   It handles potential `HttpClientErrorException.Unauthorized` exceptions for authentication failures and other general exceptions during the API call.
    *   It prints the raw response from the AI agent to the console for debugging purposes.
*   `dividePoints(String apiResponse)`: This method parses the raw string response from the AI agent. It splits the string based on a regular expression that assumes each point starts with a number followed by a period. It then cleans up the extracted points (removing leading numbers and trailing asterisks) and assigns them to instance variables (`strBullet1` to `strBullet10`) for use in slide generation.
*   `createTitleSlide(XMLSlideShow ppt, String title, String presenterName)`: Creates the title slide with a background image, white left panel, title, presenter name, date, and the company logo. It uses `Apache POI` classes like `XSLFSlide`, `XSLFPictureData`, `XSLFPictureShape`, `XSLFAutoShape`, and `XSLFTextBox` to add and format elements.
*   `createSecondSlide(XMLSlideShow ppt, String title)`: Creates the second slide with a white background, colored lines at the top, a title on the left, and bullet points on the right.
*   `createThirdSlide(XMLSlideShow ppt, String title)`: Creates the third slide with a white background, a green line at the top, a side image, and bullet points on the right.
*   `createThankYouSlide(XMLSlideShow ppt, String conclusion)`: Creates the thank you slide with a black background, colored lines at the top, a "Thank you" message, the conclusion text, a handshake image, and the company logo.
*   `getResourceBytes(String resourcePath)`: A helper method to load resources (like images) from the classpath as a byte array using Spring's `ClassPathResource`.

## How Slides are Created (Apache POI)

The presentation generation relies heavily on the Apache POI library, specifically the XSLF (XML Slide Show Format) component for working with `.pptx` files. The process involves:

1.  **Creating a new presentation:** `new XMLSlideShow()` initializes an empty presentation.
2.  **Setting slide size:** `ppt.setPageSize()` sets the dimensions of the slides.
3.  **Creating slides:** `ppt.createSlide()` adds a new blank slide to the presentation.
4.  **Adding Shapes:** `XSLFSlide` objects are populated with various shapes:
    *   `XSLFAutoShape`: Used for adding basic shapes like rectangles and lines, and for setting background colors.
    *   `XSLFTextBox`: Used for adding text content. `addNewTextParagraph()` and `addNewTextRun()` are used to structure and format the text within the text box.
    *   `XSLFPictureShape`: Used for adding images. Images are first added to the presentation's picture data using `ppt.addPicture()`, which returns an `XSLFPictureData` object. This object is then used to create an `XSLFPictureShape` on the slide.
5.  **Setting Properties:** Shapes are configured by setting their properties like:
    *   `setAnchor()`: Defines the position and size of the shape on the slide using a `java.awt.Rectangle`.
    *   `setFillColor()`: Sets the background color of a shape.
    *   `setLineColor()`: Sets the color of a shape's border or a line.
    *   `setLineWidth()`: Sets the thickness of a line.
    *   `setText()`: Sets the text content of a text run.
    *   `setFontFamily()`, `setFontSize()`, `setFontColor()`: Sets the font properties for text runs.
    *   `setTextAlign()`: Sets the alignment of text within a paragraph.
    *   `setBullet()`: Enables bullet points for a text paragraph.
6.  **Saving the presentation:** `ppt.write(out)` writes the presentation content to an `OutputStream` (in this case, a `ByteArrayOutputStream`).

## How the AI Agent is Called

The application interacts with an external AI agent via a REST API call. The `callAiAgent` method in `PresentationService` is responsible for this.

1.  **Endpoint:** The AI agent is accessed via a POST request to the URL specified in the `callAiAgent` method (`https://p2ubkjgiucy7bb5tzy5z53czmq0umqtx.lambda-url.us-east-1.on.aws/agent/greybot/execute`).
2.  **Headers:** Essential headers are set:
    *   `Content-Type: application/json`: Indicates that the request body is in JSON format.
    *   `x-user-id`: A custom header for user identification (currently hardcoded).
    *   `x-authentication: api-key <apiKey>`: A custom header for API key authentication (currently hardcoded).
3.  **Request Body:** The request body is a JSON object with an `input` field containing the `user_topic` (the presentation title provided by the user).
4.  **Execution:** `restTemplate.postForEntity()` sends the POST request and receives the response as a `ResponseEntity`.
5.  **Response Handling:** The raw string response from the AI agent is returned by the method. Error handling is included for authentication failures (`HttpClientErrorException.Unauthorized`) and other exceptions.

## Building and Running

### Prerequisites

*   Java Development Kit (JDK) 8 or higher
*   Maven

### Building the Project

Navigate to the project's root directory in your terminal and run the following Maven command:


