package com.perago.techtest.impl;

import com.perago.techtest.*;
import org.apache.commons.lang3.builder.EqualsBuilder;

import java.io.Serializable;
import java.lang.reflect.Field;

public class DiffEngineImpl implements DiffEngine {

    private static final String CREATE = "Create: ";
    private static final String UPDATE = "Update: ";
    private static final String DELETE = "Delete: ";
    final Diff diff = new Diff();
    String diffMessage = "";

    public <T extends Serializable> T apply(T original, Diff<?> diff) throws DiffException {
        return null;
    }

    public <T extends Serializable> Diff<T> calculate(T original, T modified) throws DiffException {
        if (!EqualsBuilder.reflectionEquals(original, modified)) {
            Field[] fields;

            if (original == null && modified != null) {
                fields = modified.getClass().getDeclaredFields();
                getDiffForNullAndModifiedObjects(modified, fields, diff);
            } else if (original != null && modified == null) {
                diff.setDiffMessage(diff.getDiffMessage().concat("\n" + DELETE + original.getClass().getSimpleName()));
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
                                /*final Person modifiedPerson = (Person) modifiedField;
                                if (modifiedPerson.getClass().getSimpleName().equals(Person.class.getSimpleName())) {
                                    diffMessage += "\n" + CREATE + ((Person) modifiedField).getFirstName() + " as \"" + modifiedPerson.getFirstName() + "\"";
                                    diffMessage += "\n" + CREATE + fieldName + " as \"" + modifiedPerson.getSurname() + "\"";
                                    diffMessage += "\n" + CREATE + fieldName + " as \"" + modifiedPerson.getFriend() + "\"";
                                    diffMessage += "\n" + CREATE + fieldName + " as \"" + modifiedPerson.getPet() + "\"";
                                    diffMessage += "\n" + CREATE + fieldName + " as \"" + modifiedPerson.getFirstName() + "\"";
                                }*/
                                if (fieldName == "friend") {
                                    diffMessage = diff.getDiffMessage();
                                }
                                diffMessage = "\n" + UPDATE + fieldName + " from \"" + originalField + "\" to \"" + modifiedField + "\"";
                                if (!diff.getDiffMessage().contains(diffMessage)) {
                                    buildDiffDisplay(diff, diffMessage);
                                }

                                if (field.getType().getSimpleName().equals(Person.class.getSimpleName())) {
                                    final Person originalFriend = originalField == null ? null : ((Person) originalField).getFriend();
                                    final Person modifiedFriend = modifiedField == null ? null : ((Person) modifiedField).getFriend();
                                    if (originalField == null && modifiedField != null) {
                                        if (((Person) modifiedField).getFriend() == null) return diff;
                                        calculate(null, modifiedFriend);
                                    } else {
                                        calculate(originalFriend, modifiedFriend);
                                    }
                                } if (field.getType().getSimpleName().equals(Pet.class.getSimpleName())) {
                                    if (originalField == null && modifiedField == null) return diff;
                                    calculate(original, modified);
                                }
                            }
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
                System.out.println(diff.getDiffMessage());
                return diff;
            }
            return diff;
        }
        return null;
    }

    private <T extends Serializable> void getDiffForNullAndModifiedObjects(T modified, Field[] fields, Diff<T> diff) {
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                final Object objectValue = field.get(modified);
                final String fieldName = field.getName();
                final boolean skipSerialVersionUID = skipSerialVersionUID(fields, fieldName);
                if (skipSerialVersionUID) continue;
                if (diff.getDiffMessage().equals("")) {
                    diff.setDiffMessage(diff.getDiffMessage().concat("\n" + CREATE + modified.getClass().getSimpleName()));
                }

                diff.setDiffMessage(diff.getDiffMessage().concat("\n" + CREATE + fieldName + " as \""
                                + objectValue + "\""));

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean skipSerialVersionUID(final Field[] fields, final String fieldName) {
        final String serialVersionUID = fields[0].getName();
        return serialVersionUID.equals(fieldName);
    }

    private void buildDiffDisplay(final Diff diff, final String message) {
        diff.setDiffMessage(diff.getDiffMessage().concat(message));
    }

    public static void main(String[] args) throws DiffException {

        Person johnFriendFriend = new Person();
        johnFriendFriend.setFirstName("JohnFriendFriend");
        johnFriendFriend.setSurname("Jones");
        johnFriendFriend.setFriend(null);
        johnFriendFriend.setPet(null);
        johnFriendFriend.setNickNames(null);

        Person johnFriend = new Person();
        johnFriend.setFirstName("JohnFriend");
        johnFriend.setSurname("Jones");
        johnFriend.setFriend(null);
        johnFriend.setPet(null);
        johnFriend.setNickNames(null);

        Person fred = new Person();
        fred.setFirstName("Fred");
        fred.setSurname("Smith");
        fred.setFriend(null);
        fred.setPet(null);
        fred.setNickNames(null);

        Person john = new Person();
        john.setFirstName("Fred");
        john.setSurname("Smith");
        john.setFriend(johnFriend);
        john.setPet(null);
        john.setNickNames(null);

//        Person fred = null;
//        Person john = null;

        DiffEngine diffEngine = new DiffEngineImpl();
        final Diff<Person> diff = diffEngine.calculate(fred, john);
        System.out.println(diff);
    }
}
