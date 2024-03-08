package moneytransferapp;

import io.temporal.api.common.v1.WorkflowExecution;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;

import java.util.UUID;

public class CheckBalance {
  public static void main(String[]args){
    // WorkflowServiceStubs is a gRPC stubs wrapper that talks to the local Docker instance of the Temporal server.
    WorkflowServiceStubs service = WorkflowServiceStubs.newLocalServiceStubs();
    WorkflowOptions options = WorkflowOptions.newBuilder()
            .setTaskQueue(Shared.MONEY_TRANSFER_TASK_QUEUE)
            // A WorkflowId prevents this it from having duplicate instances, remove it to duplicate.
            .setWorkflowId("money-transfer-workflow")
            .build();
    // WorkflowClient can be used to start, signal, query, cancel, and terminate Workflows.
    WorkflowClient client = WorkflowClient.newInstance(service);
    // WorkflowStubs enable calls to methods as if the Workflow object is local, but actually perform an RPC.
    CheckBalanceWorkFlow workflow = client.newWorkflowStub(CheckBalanceWorkFlow.class, options);
    String referenceId = UUID.randomUUID().toString();
    String fromAccount = "A";
    // Asynchronous execution. This process will exit after making this call.
    WorkflowExecution we = WorkflowClient.start(workflow::checkBalance, fromAccount);
    System.out.printf("\nCheckBalance of account %s is processing\n", fromAccount);
    System.out.printf("\nWorkflowID: %s RunID: %s", we.getWorkflowId(), we.getRunId());
    System.exit(0);
  }
}
