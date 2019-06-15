package com.perago.techtest;

import java.io.Serializable;

/**
 * The object representing a diff.
 * Implement this class as you see fit. 
 *
 */
public class Diff<T extends Serializable> {

    private String diffMessage = "";

    public String getDiffMessage() {
        return diffMessage;
    }

    public void setDiffMessage(String diffMessage) {
        this.diffMessage = diffMessage;
    }

    @Override
    public String toString() {
        return "Diff{" +
                "diffMessage='" + diffMessage + '\'' +
                '}';
    }
}
