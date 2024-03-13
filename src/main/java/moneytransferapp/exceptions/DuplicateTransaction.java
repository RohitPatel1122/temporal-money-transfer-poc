package moneytransferapp.exceptions;

public class DuplicateTransaction extends RuntimeException{
  public DuplicateTransaction() {
  }

  public DuplicateTransaction(String message) {
    super(message);
  }
}
