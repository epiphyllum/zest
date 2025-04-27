package io.renren.zin;

public class BankException extends  RuntimeException {
    public BankException(String error) {
        super(error);
    }
}
