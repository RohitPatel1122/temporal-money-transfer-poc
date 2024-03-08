package moneytransferapp;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface CheckBalanceWorkFlow {
  @WorkflowMethod
  String checkBalance(String accountId);
}
