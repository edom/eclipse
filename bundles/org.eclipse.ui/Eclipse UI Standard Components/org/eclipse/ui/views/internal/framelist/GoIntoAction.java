package org.eclipse.ui.views.internal.framelist;

/**
 * Generic "Go Into" action which switches the viewer's input
 * to be the currently selected container.
 * Enabled only when the current selection is a single container.
 */
public class GoIntoAction extends FrameAction {
public GoIntoAction(FrameList frameList) {
	super(frameList);
	setText("Go &Into");
	setToolTipText("Go Into");
	update();
}
Frame getSelectionFrame(int flags) {
	return getFrameList().getSource().getFrame(IFrameSource.SELECTION_FRAME, flags);
}
/**
 * Calls <code>gotoFrame</code> on the frame list with a frame
 * representing the currently selected container.
 */
public void run() {
	Frame selectionFrame = getSelectionFrame(IFrameSource.FULL_CONTEXT);
	if (selectionFrame != null) {
		getFrameList().gotoFrame(selectionFrame);
	}
}
public void update() {
	super.update();
	Frame selectionFrame = getSelectionFrame(0);
	setEnabled(selectionFrame != null);
}
}
