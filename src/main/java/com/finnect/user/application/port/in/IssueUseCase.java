package com.finnect.user.application.port.in;

import com.finnect.user.application.port.in.command.IssueCommand;
import com.finnect.user.state.TokenPairState;

public interface IssueUseCase {

    TokenPairState issue(IssueCommand command);
}
