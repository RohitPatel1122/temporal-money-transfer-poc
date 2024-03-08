package moneytransferapp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.temporal.failure.ApplicationFailure;
import moneytransferapp.exceptions.AccountNotFoundException;
import moneytransferapp.exceptions.InsufficientBalance;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

// @@@SNIPSTART money-transfer-project-template-java-activity-implementation
public class AccountActivityImpl implements AccountActivity {

  @Override
  public void withdraw(String accountId, String referenceId, int amount) {

    System.out.printf(
            "\nWithdrawing %d from account %s. ReferenceId: %s\n",
            amount, accountId, referenceId
    );

    HttpClient client = HttpClient.newBuilder()
            .build();
    String txnId = referenceId ;
    HttpRequest httpRequest = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:9000/" + accountId + "/withdraw/" + amount + "?die=false&delay=0"))
            .setHeader("transactionId", txnId)
            .setHeader("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.noBody())
            .build();
    HttpResponse<String> httpResponse = null;
    System.out.println(httpRequest.uri());
    try {
      httpResponse = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

    } catch (IOException | InterruptedException e) {
      System.err.println("Exception in check Balance:" + e.getMessage());
    } catch (Exception e) {
      System.err.println("General Exception Withdraw:" + e.getMessage());

    }
    ObjectMapper mapper = new ObjectMapper();
    Response response = null;
    try {
      response = mapper.readValue(httpResponse.body(), Response.class);
      System.out.println("Response :" + response.toString());
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    //DO not retry/continue in case Account not found

    if (httpResponse.statusCode() == 404) {
      throw  new AccountNotFoundException("account not found");
    }
    if (httpResponse.statusCode() == 400) {
      throw  new InsufficientBalance(response.getMessage());
    }
    System.out.println(txnId + " || Withdraw response status code: " + httpResponse.statusCode()
            + " body:" + httpResponse.body());
  }

  @Override
  public void revertTransfer(String accountId, String referenceId, int amount) {
    System.out.printf(
            "\nReverting fund  %d to account %s. ReferenceId: %s\n",
            amount, accountId, referenceId
    );
    this.deposit(accountId,referenceId,amount);
  }

  @Override
  public String checkBalance(String accountId) {
    System.out.printf(
            "\nCheck Balance from account %s",
            accountId
    );
    HttpClient client = HttpClient.newBuilder()
            .build();
    HttpRequest httpRequest = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:9000/" + accountId + "/balance?delay=2&die=false"))
            .GET()
            .build();
    HttpResponse<String> httpResponse = null;
    try {
      httpResponse = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
    } catch (IOException | InterruptedException e) {
      System.err.println("Exception in check Balance:" + e.getMessage());
    } catch (Exception e) {
      System.err.println("Exception general in check Balance:" + e.getMessage());

    }
    System.out.println("response: " + httpResponse.body() + " -  " + httpResponse.statusCode());
    System.out.printf(
            "\n Balance for account %s  is  %s",
            accountId, httpResponse.body()
    );
    return httpResponse.body();
  }

  @Override
  public void deposit(String accountId, String referenceId, int amount) {

    System.out.printf(
            "\nDepositing %d into account %s. ReferenceId: %s\n",
            amount, accountId, referenceId
    );

    HttpClient client = HttpClient.newBuilder()
            .build();
    String txnId = referenceId ;
    HttpRequest httpRequest = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:9000/" + accountId + "/deposit/" + amount + "?die=false&delay=0"))
            .setHeader("transactionId", txnId)
            .setHeader("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.noBody())
            .build();
    HttpResponse<String> httpResponse = null;
    System.out.println(httpRequest.uri());

    try {
      httpResponse = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
      httpResponse.body();
    } catch (IOException | InterruptedException e) {
      System.err.println("Exception in Deposit:" + e.getMessage());
    } catch (Exception e) {
      System.err.println("Exception Deposit:" + e.getMessage());
    }

    ObjectMapper mapper = new ObjectMapper();
    Response response = null;
    try {
      response = mapper.readValue(httpResponse.body(), Response.class);
      System.out.println("Response :" + response.toString());
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    if (httpResponse.statusCode() == 404) {
      throw  new AccountNotFoundException("account not found");
    }
    if (httpResponse.statusCode() == 400) {
      throw  new InsufficientBalance(response.getMessage());
    }


    System.out.println(txnId + "|| Deposit response status code: " + httpResponse.statusCode()
            + " body:" + httpResponse.body()
    );
    // Uncomment the following line to simulate an Activity error.
    // throw new RuntimeException("simulated");
  }
}
// @@@SNIPEND
