package com.slidepilot.pptgenerator.controller;

import com.slidepilot.pptgenerator.model.PresentationRequest;
import com.slidepilot.pptgenerator.service.PresentationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.IOException;

@Controller
public class PresentationController {

    @Autowired
    private PresentationService presentationService;

    @GetMapping("/")
    public String showForm(Model model) {
        model.addAttribute("presentationRequest", new PresentationRequest());
        return "index";
    }

    @PostMapping("/generate")
    public ResponseEntity<byte[]> generatePresentation(@ModelAttribute PresentationRequest request) throws IOException {
        byte[] presentationBytes = presentationService.generatePresentation(request);
        
        String filename = request.getTitle().replaceAll("[^a-zA-Z0-9 ]", "").replace(" ", "_") + ".pptx";
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.presentationml.presentation"))
                .body(presentationBytes);
    }
}
