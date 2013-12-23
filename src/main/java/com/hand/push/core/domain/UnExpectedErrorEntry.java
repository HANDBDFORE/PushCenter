package com.hand.push.core.domain;

/**
 * Created with IntelliJ IDEA.
 * User: emerson
 * Date: 12/23/13
 * Time: 11:34 AM
 */
public class UnExpectedErrorEntry extends ErrorEntry {
    private final Class who;


    public UnExpectedErrorEntry(Throwable causedBy, Class who) {
        super(causedBy);
        this.who = who;
    }

    public Class getWho() {
        return who;
    }

    public static UnExpectedErrorEntry error(Throwable causedBy, Class who) {
        return new UnExpectedErrorEntry(causedBy, who);
    }


    @Override
    public String toString() {
        return "UnExpectedErrorEntry{" +
                "who=" + who +
                '}';
    }
}
