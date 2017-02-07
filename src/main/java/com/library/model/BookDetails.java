package com.library.model;


import java.util.AbstractMap;
import java.util.LinkedList;
import java.util.List;

/**
 * domain object
 */
public class BookDetails {

    private List<AbstractMap.SimpleEntry<String, String>> details;

    public BookDetails() {
        List<AbstractMap.SimpleEntry<String, String>> details = new LinkedList<>();
    }

    public BookDetails(List<AbstractMap.SimpleEntry<String, String>> details) {
        this.details = details;
    }

    public List<AbstractMap.SimpleEntry<String, String>> getDetails() {
        return details;
    }

    public void setDetails(List<AbstractMap.SimpleEntry<String, String>> details) {
        this.details = details;
    }

    @Override
    public String toString() {
        return "BookDetails{" +
                "details=" + details +
                '}';
    }
}
