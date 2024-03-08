package moneytransferapp;

import com.fasterxml.jackson.annotation.JsonProperty;


public class Response {
  @JsonProperty("accountId")
  private String accountId ;
  @JsonProperty("message")
  private String message;
  @JsonProperty("transactionId")
  private String transactionId;

  @Override
  public String toString() {
    return "Response{" +
            "accountId='" + accountId + '\'' +
            ", message='" + message + '\'' +
            ", transactionId='" + transactionId + '\'' +
            '}';
  }

  public String getTransactionId() {
    return transactionId;
  }

  public void setTransactionId(String transactionId) {
    this.transactionId = transactionId;
  }

  public String getAccountId() {
    return accountId;
  }

  public void setAccountId(String accountId) {
    this.accountId = accountId;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
