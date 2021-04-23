package com.example.waste_disposal_classification.classifier;

/**
 * Class for result of classification
 */

public class Recognition{
    private final  String id;
    private  final String title;
    private final Float confidence;

    public Recognition(String id, String title, Float confidence) {
        this.id = id;
        this.title = title;
        this.confidence = confidence;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Float getConfidence() {
        return confidence;
    }

    @Override
    public String toString() {
        return "Recognition{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", confidence=" + confidence +
                '}';
    }
}

