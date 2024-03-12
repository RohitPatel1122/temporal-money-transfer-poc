package moneytransferapp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
@JsonIgnoreProperties(ignoreUnknown = true)
public class Account {
  @JsonProperty("id")
  private String id ;
  @JsonProperty("balance")
  private String  balance;

  @Override
  public String toString() {
    return "Account{" +
            "id='" + id + '\'' +
            ", balance='" + balance + '\'' +
            '}';
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getBalance() {
    return balance;
  }

  public void setBalance(String balance) {
    this.balance = balance;
  }
}
