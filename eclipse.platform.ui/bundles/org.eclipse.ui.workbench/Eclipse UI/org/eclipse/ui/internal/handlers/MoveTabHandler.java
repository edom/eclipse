package org.eclipse.ui.internal.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.internal.WorkbenchPage;

/**
 * @since 3.5
 */
public final class MoveTabHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchPart part = HandlerUtil.getActivePart(event);
		if (part != null) {
			WorkbenchPage page = (WorkbenchPage) part.getSite().getPage();
			int change = computeDirection(event);
			page.movePartBy(part, change);
		}
		return null;
	}

	private static int computeDirection(ExecutionEvent event) {
		// This id-matching feels somewhat unsavory.
		switch (event.getCommand().getId()) {
		case "org.eclipse.ui.navigate.moveTabLeft": //$NON-NLS-1$
			return -1;
		case "org.eclipse.ui.navigate.moveTabRight": //$NON-NLS-1$
			return 1;
		default:
			return 0;
		}
	}

}
