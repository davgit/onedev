package io.onedev.server.model.support.issueworkflow.action;

import org.hibernate.validator.constraints.NotEmpty;

import io.onedev.server.util.editable.annotation.BranchChoice;
import io.onedev.server.util.editable.annotation.Editable;
import io.onedev.server.util.editable.annotation.OmitName;

@Editable(order=300, name="Pull request is discarded for")
public class DiscardPullRequest implements IssueAction {

	private static final long serialVersionUID = 1L;

	private String targetBranch;

	@Editable
	@BranchChoice
	@OmitName
	@NotEmpty
	public String getTargetBranch() {
		return targetBranch;
	}

	public void setTargetBranch(String targetBranch) {
		this.targetBranch = targetBranch;
	}

	@Override
	public Button getButton() {
		return null;
	}
	
}
