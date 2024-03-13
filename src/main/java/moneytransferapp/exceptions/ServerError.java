package moneytransferapp.exceptions;

public class ServerError extends RuntimeException{
  public ServerError() {
  }

  public ServerError(String message) {
    super(message);
  }
}
