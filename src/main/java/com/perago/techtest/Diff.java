package com.perago.techtest;

import java.io.Serializable;
import java.util.Map;

/**
 * The object representing a diff.
 * Implement this class as you see fit. 
 *
 */
public class Diff<T extends Serializable> {

    /**
     * This field stores message to be used to represent object modification (Created, Updated, or Deleted)
     */
    private String reflectionMessage;

    private String diffMessage = "";

    public String getReflectionMessage() {
        return reflectionMessage;
    }

    public void setReflectionMessage(String reflectionMessage) {
        this.reflectionMessage = reflectionMessage;
    }

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
