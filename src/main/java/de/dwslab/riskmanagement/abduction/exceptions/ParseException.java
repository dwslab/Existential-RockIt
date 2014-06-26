package de.dwslab.riskmanagement.abduction.exceptions;

/**
 * Exceptions which can occur in the Parse Phase of the application.
 */
public class ParseException extends Exception {

    private static final long serialVersionUID = -4390051337485101364L;

    public ParseException(String msg) {
        super("Excpetion while parsing the input data: " + msg);
    }

}
