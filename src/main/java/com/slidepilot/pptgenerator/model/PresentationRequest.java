package com.slidepilot.pptgenerator.model;

import lombok.Data;

@Data
public class PresentationRequest {
    private String title;
    private String presenterName;
    private String conclusion;
}
