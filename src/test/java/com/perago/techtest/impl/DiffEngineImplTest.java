package com.perago.techtest.impl;

import com.perago.techtest.Diff;
import com.perago.techtest.DiffEngine;
import com.perago.techtest.DiffException;
import com.perago.techtest.Person;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class DiffEngineImplTest {

    private Person johnFriendFriend = new Person();
    private Person johnFriend = new Person();
    private Person fred = new Person();
    private Person john = new Person();
    private Person nullFred = null;
    private Person nullJohn = null;
    private Person fredOriginal = new Person();
    private Person fredDifferentSurname = new Person();

    private DiffEngine diffEngine;

    @Before
    public void init() {
        johnFriendFriend.setFirstName("JohnFriendFriendName");
        johnFriendFriend.setSurname("JohnFriendFriendSurname");
        johnFriendFriend.setFriend(null);
        johnFriendFriend.setPet(null);
        johnFriendFriend.setNickNames(null);

        johnFriend.setFirstName("JohnFriendName");
        johnFriend.setSurname("JohnFriendSurname");
        johnFriend.setFriend(johnFriendFriend);
        johnFriend.setPet(null);
        johnFriend.setNickNames(null);

        fred.setFirstName("Fred");
        fred.setSurname("Smith");
        fred.setFriend(null);
        fred.setPet(null);
        fred.setNickNames(null);

        john.setFirstName("Fred");
        john.setSurname("Smith");
        john.setFriend(johnFriend);
        john.setPet(null);
        john.setNickNames(null);

        fredOriginal.setFirstName("Fred");
        fredOriginal.setSurname("Smith");
        fredOriginal.setFriend(null);
        fredOriginal.setPet(null);
        fredOriginal.setNickNames(null);

        fredDifferentSurname.setFirstName("Fred");
        fredDifferentSurname.setSurname("Stones");
        fredDifferentSurname.setFriend(null);
        fredDifferentSurname.setPet(null);
        fredDifferentSurname.setNickNames(null);

        diffEngine = new DiffEngineImpl();
    }

    @Test
    public void testCreateNewObject(){
        try {
            final Diff<Person> diff = diffEngine.calculate(nullFred, john);
            assertNotNull(diff);
            assertTrue(diff.getDiffMessage().contains(""));
        } catch (DiffException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testDeleteObject(){
        try {
            final Diff<Person> diff = diffEngine.calculate(fred, nullJohn);
            assertNotNull(diff);
            assertTrue(diff.getDiffMessage().equals("\nDelete: Person"));
        } catch (DiffException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testModifyObjectFields(){
        try {
            final Diff<Person> diff = diffEngine.calculate(fredOriginal, fredDifferentSurname);
            assertNotNull(diff);
            assertTrue(diff.getDiffMessage().contains("Update: surname from \"Smith\" to \"Stones\""));
        } catch (DiffException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testUpdateChildObjects(){
        try {
            final Diff<Person> diff = diffEngine.calculate(fred, john);
            assertNotNull(diff);
            assertTrue(diff.getDiffMessage().contains(
                    "Update: Person\n" +
                    "Update: friend\n" +
                    "Create: Person"));
        } catch (DiffException e) {
            e.printStackTrace();
        }
    }
}
