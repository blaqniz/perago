package com.perago.techtest.impl;

import com.perago.techtest.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;

import java.io.Serializable;
import java.lang.reflect.Field;

public class DiffEngineImpl implements DiffEngine {

    private static final String CREATE = "Create: ";
    private static final String UPDATE = "Update: ";
    private static final String DELETE = "Delete: ";
    private static final String FRIEND = "friend";
    final Diff diff = new Diff();
    private String diffMessage = "";
    private int counter = 1;

    public <T extends Serializable> T apply(T original, Diff<?> diff) throws DiffException {
        return null;
    }

    public <T extends Serializable> Diff<T> calculate(T original, T modified) throws DiffException {
        if (!EqualsBuilder.reflectionEquals(original, modified)) {
            if (original == null && modified != null) {
                final Field[] fields = modified.getClass().getDeclaredFields();
                getDiffForNullAndModifiedObjects(modified, fields, diff);
            } else if (original != null && modified == null) {
                deleteObject(diff, original);
                return diff;
            } else {
                final Field[] originalFields = original.getClass().getDeclaredFields();
                final Field[] modifiedFields = modified.getClass().getDeclaredFields();
                final Field[][] declaredFields = {originalFields, modifiedFields};
                diff.setDiffMessage(UPDATE + original.getClass().getSimpleName());
                for (Field[] dFields : declaredFields) {
                    for (Field field : dFields) {
                        final String fieldName = field.getName();
                        final boolean skipSerialVersionUID = skipSerialVersionUID(dFields, fieldName);
                        if (skipSerialVersionUID) continue;
                        try {
                            field.setAccessible(true);
                            Object originalField = field.get(original);
                            Object modifiedField = field.get(modified);

                            if (originalField != modifiedField) {
                                if (fieldName == FRIEND) {
                                    diffMessage += "\n" + UPDATE + fieldName;
                                    diff.setDiffMessage(diff.getDiffMessage() + "\n" + UPDATE + fieldName);
                                    if (originalField == null) {
                                        return calculate(null, (T) modifiedField);
                                    } else {
                                        return calculate((T) originalField, null);
                                    }
                                } else {
                                    updateObject(diff, fieldName, originalField, modifiedField);
                                }
                                if (!diff.getDiffMessage().contains(diffMessage)) {
                                    buildDiffDisplay(diff, diffMessage);
                                }

                                if (modifiedField instanceof Person) {
                                    final Person originalFriend = originalField == null ? null : ((Person) originalField).getFriend();
                                    final Person modifiedFriend = modifiedField == null ? null : ((Person) modifiedField).getFriend();
                                    if (originalField == null && modifiedField != null) {
                                        if (((Person) modifiedField).getFriend() == null) return diff;
                                        calculate(null, modifiedFriend);
                                    } else {
                                        calculate(originalFriend, modifiedFriend);
                                    }
                                } else if (modifiedField instanceof Pet) {
                                    if (originalField == null && modifiedField == null) return diff;
                                    calculate(original, modified);
                                }
                            }
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return diff;
            }
            return diff;
        }
        return null;
    }

    private <T extends Serializable> void getDiffForNullAndModifiedObjects(T modified, Field[] fields, Diff<T> diff) throws DiffException {
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                final Object objectValue = field.get(modified);
                final String fieldName = field.getName();
                final boolean skipSerialVersionUID = skipSerialVersionUID(fields, fieldName);
                if (skipSerialVersionUID) continue;
                final String diffMessage = diff.getDiffMessage();
                if (StringUtils.isEmpty(diffMessage)) {
                    createObject(diff, modified);
                }

                setModifiedChildObject(modified, diff);

                if (objectValue instanceof Person) {
                    calculate(null, (T) objectValue);
                } else {
                    diff.setDiffMessage(diff.getDiffMessage().concat("\n" + CREATE + fieldName + " as \"" + objectValue + "\""));
                }

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private <T extends Serializable> void setModifiedChildObject(T modified, Diff<T> diff) {
        if (diff.getDiffMessage().contains(UPDATE) && counter > 0) {
            diff.setDiffMessage(diff.getDiffMessage().concat("\n" + CREATE + modified.getClass().getSimpleName()));
            counter--;
        }
    }

    private boolean skipSerialVersionUID(final Field[] fields, final String fieldName) {
        final String serialVersionUID = fields[0].getName();
        return serialVersionUID.equals(fieldName);
    }

    private void buildDiffDisplay(final Diff diff, final String message) {
        diff.setDiffMessage(diff.getDiffMessage().concat(message));
    }

    private void createObject(Diff diff, Object modified) {
        diff.setDiffMessage(diff.getDiffMessage().concat("\n" + CREATE + modified.getClass().getSimpleName()));
    }

    private void updateObject(Diff diff, String fieldName, Object originalField, Object modifiedField) {
        if (!diff.getDiffMessage().contains("\n" + UPDATE + fieldName + " from \"" + originalField + "\" to \"" + modifiedField + "\"")) {
            diff.setDiffMessage(diff.getDiffMessage().concat("\n" + UPDATE + fieldName + " from \"" + originalField + "\" to \"" + modifiedField + "\""));
        }
    }

    private void deleteObject(Diff diff, Object original) {
        diff.setDiffMessage(diff.getDiffMessage().concat("\n" + DELETE + original.getClass().getSimpleName()));
    }
}
