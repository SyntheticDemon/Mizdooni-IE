package ir.ie.mizdooni.exceptions;

import static ir.ie.mizdooni.defines.Errors.TABLE_ALREADY_EXISTS;

public class TableAlreadyExists extends Exception {
    public TableAlreadyExists() {
        super(TABLE_ALREADY_EXISTS);
    }

}
