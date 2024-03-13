package moneytransferapp;

import io.temporal.api.enums.v1.WorkflowExecutionStatus;
import io.temporal.api.filter.v1.StatusFilter;
import io.temporal.api.filter.v1.WorkflowTypeFilter;
import io.temporal.api.workflowservice.v1.ListClosedWorkflowExecutionsRequest;
import io.temporal.api.workflowservice.v1.ListClosedWorkflowExecutionsResponse;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.workflow.Workflow;

public class WorkFlowStatus {
  public static void main(String[] args){

  // WorkflowServiceStubs is a gRPC stubs wrapper that talks to the local Docker instance of the Temporal server.

  WorkflowServiceStubs service = WorkflowServiceStubs.newLocalServiceStubs();
  ListClosedWorkflowExecutionsRequest request = ListClosedWorkflowExecutionsRequest.newBuilder()
          .setTypeFilter(WorkflowTypeFilter.newBuilder().setName("MoneyTransferWorkflow").build())
          .setNamespace("default")
          .setStatusFilter(StatusFilter.newBuilder()
                  .setStatus(WorkflowExecutionStatus.WORKFLOW_EXECUTION_STATUS_TIMED_OUT)
                  .build())
                .build();

    ListClosedWorkflowExecutionsResponse listClosedWorkflowExecutionsResponse = service.blockingStub().listClosedWorkflowExecutions(request);
    listClosedWorkflowExecutionsResponse.getExecutionsList().stream().forEach(workflowExecutionInfo -> {
              workflowExecutionInfo.getStatus();
            }
      );

  }

}
