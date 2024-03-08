package moneytransferapp.exceptions;

public class InsufficientBalance  extends RuntimeException{
  public InsufficientBalance() {
  }

  public InsufficientBalance(String message) {
    super(message);
  }
}
