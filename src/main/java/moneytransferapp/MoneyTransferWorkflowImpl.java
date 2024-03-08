package moneytransferapp;

import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.failure.ActivityFailure;
import io.temporal.workflow.Workflow;
import moneytransferapp.exceptions.AccountNotFoundException;
import moneytransferapp.exceptions.InsufficientBalance;
import moneytransferapp.exceptions.MoneyTransferWorkFlowException;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

// @@@SNIPSTART money-transfer-project-template-java-workflow-implementation
public class MoneyTransferWorkflowImpl implements MoneyTransferWorkflow {
  private static final String WITHDRAW = "Withdraw";
  private static final String DEPOSIT = "Deposit";
  private static final String REVERT = "revert";
  // RetryOptions specify how to automatically handle retries when Activities fail.
  private final RetryOptions retryoptions = RetryOptions.newBuilder()
          .setInitialInterval(Duration.ofSeconds(1))
          .setMaximumInterval(Duration.ofSeconds(100))
          .setBackoffCoefficient(2)
          .setMaximumAttempts(3)
          .build();
  private final ActivityOptions defaultActivityOptions = ActivityOptions.newBuilder()
          // Timeout options specify when to automatically timeout Activities if the process is taking too long.
          .setStartToCloseTimeout(Duration.ofSeconds(5))
          // Optionally provide customized RetryOptions.
          // Temporal retries failures by default, this is simply an example.
          .setRetryOptions(retryoptions)
          .build();

  // ActivityStubs enable calls to methods as if the Activity object is local, but actually perform an RPC.
  private final Map<String, ActivityOptions> perActivityMethodOptions = new HashMap<String, ActivityOptions>() {{
    put(WITHDRAW, ActivityOptions.newBuilder().setRetryOptions(RetryOptions.newBuilder()
            .setMaximumAttempts(2)
            .setDoNotRetry(AccountNotFoundException.class.getName(), InsufficientBalance.class.getName())
            .build()).build());
    put(DEPOSIT, ActivityOptions.newBuilder()
            .setStartToCloseTimeout(Duration.ofSeconds(5))
            .setRetryOptions(RetryOptions.newBuilder()
                    .setMaximumAttempts(2)
                    .setDoNotRetry(AccountNotFoundException.class.getName())
                    .build()).build());
    put(REVERT, ActivityOptions.newBuilder()
            .setStartToCloseTimeout(Duration.ofSeconds(5))
            .setRetryOptions(RetryOptions.newBuilder()
                    .setMaximumAttempts(2)
                    .setDoNotRetry(AccountNotFoundException.class.getName())
                    .build()).build());
  }};
  //options set at per activity will override default ones
  private final AccountActivity account =
          Workflow.newActivityStub(AccountActivity.class, defaultActivityOptions, perActivityMethodOptions);

  // The transfer method is the entry point to the Workflow.
  // Activity method executions can be orchestrated here or from within other Activity methods.
  @Override
  public void transfer(String fromAccountId, String toAccountId, String referenceId, int amount) {
    account.withdraw(fromAccountId, referenceId, amount);
    try {
      account.deposit(toAccountId, referenceId, amount);
    } catch (ActivityFailure activityFailure) {
      System.err.println("Error while deposit:" + activityFailure.getCause().getMessage());
      //TODO: revert in case of specifc activity failure case
      account.revertTransfer(fromAccountId, referenceId, amount);
      //will cause the work flow to fail, with given reason
      //we have added this as setFailWorkflowExceptionTypes in worker
      throw new MoneyTransferWorkFlowException(activityFailure.getCause().getMessage());
    }
  }
}
// @@@SNIPEND
