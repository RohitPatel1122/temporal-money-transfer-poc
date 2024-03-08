package moneytransferapp;

// @@@SNIPSTART money-transfer-project-template-java-activity-interface

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface AccountActivity {

    @ActivityMethod
    void deposit(String accountId, String referenceId, int amount);

    @ActivityMethod
    void withdraw(String accountId, String referenceId, int amount);

    @ActivityMethod(name = "revert")
    void revertTransfer(String accountId, String referenceId, int amount);

    @ActivityMethod
    String checkBalance(String accountId);
}
// @@@SNIPEND
