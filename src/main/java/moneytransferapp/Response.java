package moneytransferapp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Response {
  @JsonProperty("account")
  private Account account ;
  @JsonProperty("message")
  private String message;
  @JsonProperty("transactionId")
  private String transactionId;

  @Override
  public String toString() {
    return "Response{" +
            "account=" + account +
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

  public Account getAccount() {
    return account;
  }

  public void setAccount(Account account) {
    this.account = account;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
