package com.example.waste_disposal_classification.classifier;

public class Recognition implements  Comparable {
    private  String name;
    private float confidence;

    public Recognition() {
    }

    public Recognition(String name, float confidence) {
        this.name = name;
        this.confidence = confidence;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getConfidence() {
        return confidence;
    }

    public void setConfidence(float confidence) {
        this.confidence = confidence;
    }

    @Override
    public String toString() {
        return "Recognition{" +
                "name='" + name + '\'' +
                ", confidence=" + confidence +
                '}';
    }

    @Override
    public int compareTo(Object o) {
        return Float.compare(((Recognition) o).confidence, this.confidence);
    }
}
