package com.perago.techtest;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * The object representing a diff.
 * Implement this class as you see fit. 
 *
 */
public class Diff<T extends Serializable> {

    private String diffMessage = "";

    private Map<String, String> diffMap = new HashMap<String, String>();

    public String getDiffMessage() {
        return diffMessage;
    }

    public void setDiffMessage(String diffMessage) {
        this.diffMessage = diffMessage;
    }

    public Map<String, String> getDiffMap() {
        return diffMap;
    }

    public void setDiffMap(Map<String, String> diffMap) {
        this.diffMap = diffMap;
    }

    @Override
    public String toString() {
        return "Diff{" +
                "diffMessage='" + diffMessage + '\'' +
                '}';
    }
}
