package moneytransferapp;

import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Workflow;

import java.time.Duration;

public class CheckBalanceWorkFlowImpl implements CheckBalanceWorkFlow{
  private final RetryOptions retryoptions = RetryOptions.newBuilder()
          .setInitialInterval(Duration.ofSeconds(1))
          .setMaximumInterval(Duration.ofSeconds(100))
          .setBackoffCoefficient(2)
          .setMaximumAttempts(5)
          .setDoNotRetry()
          .build();


  private final ActivityOptions defaultActivityOptions = ActivityOptions.newBuilder()
          // Timeout options specify when to automatically timeout Activities if the process is taking too long.
          .setStartToCloseTimeout(Duration.ofSeconds(5))
          // Optionally provide customized RetryOptions.
          // Temporal retries failures by default, this is simply an example.
            .setRetryOptions(retryoptions)
          .build();

  private final AccountActivity account = Workflow.newActivityStub(AccountActivity.class, defaultActivityOptions);


  @Override
  public String  checkBalance(String accountId) {
    return account.checkBalance(accountId);
  }
}
