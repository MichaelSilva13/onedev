package com.pmease.gitplex.core.gatekeeper;

import java.util.ArrayList;
import java.util.List;

import com.pmease.commons.editable.annotation.Editable;
import com.pmease.commons.editable.annotation.Horizontal;
import com.pmease.gitplex.core.gatekeeper.checkresult.Passed;
import com.pmease.gitplex.core.gatekeeper.checkresult.CheckResult;
import com.pmease.gitplex.core.gatekeeper.checkresult.Failed;
import com.pmease.gitplex.core.gatekeeper.checkresult.Pending;
import com.pmease.gitplex.core.gatekeeper.checkresult.Blocking;

@SuppressWarnings("serial")
@Editable(name="Or Composition", order=200, icon="fa-sitemap", category=GateKeeper.CATEGROY_COMPOSITION,
		description="This gate keeper will be passed if any of the contained gate keepers is passed.")
@Horizontal
public class OrGateKeeper extends AndOrGateKeeper {

	@Override
	protected CheckResult aggregate(Checker checker) {
		List<String> pendingReasons = new ArrayList<String>();
		List<String> rejectReasons = new ArrayList<String>();
		
		for (GateKeeper each: getGateKeepers()) {
			CheckResult result = checker.check(each);
			if (result instanceof Failed) {
				rejectReasons.addAll(result.getReasons());
			} else if (result instanceof Passed) {
				return result;
			} else if (result instanceof Blocking) {
				result.getReasons().addAll(pendingReasons);
				return result;
			} else if (result instanceof Pending) {
				pendingReasons.addAll(result.getReasons());
			}
		}
		
		if (!pendingReasons.isEmpty())
			return pending(pendingReasons);
		else
			return failed(rejectReasons);
	}

}
