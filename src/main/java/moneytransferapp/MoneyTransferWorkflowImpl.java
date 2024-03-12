package moneytransferapp;

import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.failure.ActivityFailure;
import io.temporal.failure.ApplicationFailure;
import io.temporal.failure.TimeoutFailure;
import io.temporal.workflow.Saga;
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
    // Configure SAGA to run compensation activities in parallel
    Saga.Options sagaOptions = new Saga.Options.Builder().setParallelCompensation(false).build();
    Saga saga = new Saga(sagaOptions);
    try {
      account.withdraw(fromAccountId, Workflow.getInfo().getFirstExecutionRunId(), amount);
      saga.addCompensation(account::revertTransfer, fromAccountId, Workflow.getInfo().getFirstExecutionRunId(), amount);

    } catch (ActivityFailure activityFailure) {
      System.err.println("Error while withdraw:" + activityFailure.getCause().getMessage());
      //will cause the work flow to fail, with given reason
      //we have added this as setFailWorkflowExceptionTypes in worker
      handleFailure(activityFailure);
    }
    try {
      account.deposit(toAccountId, Workflow.getInfo().getFirstExecutionRunId(), amount);
    } catch (ActivityFailure activityFailure) {
      System.err.println("Error while deposit:" + activityFailure.getCause().getMessage());
      //if account not found, then compensate
      if (((ApplicationFailure)activityFailure.getCause()).getType().equalsIgnoreCase( AccountNotFoundException.class.getName())) {
        handleCompensation(saga, activityFailure);
      }
      //else, for timeout set workflow to timeout.
      handleFailure(activityFailure);
    }
  }

  private void handleCompensation(Saga saga, ActivityFailure activityFailure) {
    try {
      System.out.println("Compensation starting.");
      saga.compensate();
      System.out.println("Compensation done.");
      throw new MoneyTransferWorkFlowException(activityFailure.getCause().getMessage());
    } catch (Saga.CompensationException  | ActivityFailure exception) {
      System.err.println("Error in handleCompensation" + exception.getMessage() + exception.getCause() + exception.getClass());
      //This is hack, possibly better way to do this
      Workflow.sleep(Duration.ofMinutes(3));
    }
  }

  private void handleFailure(ActivityFailure activityFailure) {
    if (activityFailure.getCause().getClass() != TimeoutFailure.class) {
      throw new MoneyTransferWorkFlowException(activityFailure.getCause().getMessage());
    }
    System.out.println("Failed due to timeout errors. We should set to timout status");
    //This is hack, possibly there are better way to do this
    Workflow.sleep(Duration.ofMinutes(3));
  }

}
// @@@SNIPEND
