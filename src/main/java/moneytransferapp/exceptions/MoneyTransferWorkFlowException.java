package moneytransferapp.exceptions;

public class MoneyTransferWorkFlowException extends RuntimeException{
  public MoneyTransferWorkFlowException() {
  }

  public MoneyTransferWorkFlowException(String message) {
    super(message);
  }
}
