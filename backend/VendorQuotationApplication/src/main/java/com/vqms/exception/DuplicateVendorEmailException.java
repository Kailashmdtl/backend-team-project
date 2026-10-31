
package com.vqms.exception;

public class DuplicateVendorEmailException extends RuntimeException {

    public DuplicateVendorEmailException(String message) {
        super(message);
    }
}
