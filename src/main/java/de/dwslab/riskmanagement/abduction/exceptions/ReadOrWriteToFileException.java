package de.dwslab.riskmanagement.abduction.exceptions;

public class ReadOrWriteToFileException extends Exception {

    /**
     * Exceptions which occurs when reading or writing a text file fails.
     */
    private static final long serialVersionUID = -4390221337485101364L;

    public ReadOrWriteToFileException(String msg) {
        super(msg);
    }

}
