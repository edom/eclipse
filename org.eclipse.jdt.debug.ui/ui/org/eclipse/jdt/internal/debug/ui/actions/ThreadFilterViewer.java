package org.eclipse.jdt.internal.debug.ui.actions;

/*
 * (c) Copyright IBM Corp. 2002.
 * All Rights Reserved.
 */ 

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.IDebugElement;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.internal.ui.DelegatingModelPresentation;
import org.eclipse.jdt.debug.core.IJavaBreakpoint;
import org.eclipse.jdt.debug.core.IJavaDebugTarget;
import org.eclipse.jdt.debug.core.IJavaThread;
import org.eclipse.jdt.internal.debug.ui.JDIDebugUIPlugin;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

/**
 * A thread filter viewer is a field editor which
 * allows the user to configure thread filters for a given
 * breakpoint.
 */
public class ThreadFilterViewer extends FieldEditor {
	
	private IJavaBreakpoint fBreakpoint;
	private Button fUseThreadFilters;
	private CheckboxTreeViewer fThreadViewer;
	private Composite fOuter;
	private ThreadFilterContentProvider fContentProvider;
	private CheckHandler fCheckHandler;
	
	private static String MAIN= "main"; //$NON-NLS-1$
	
	public ThreadFilterViewer(Composite parent, IJavaBreakpoint breakpoint) {
		fBreakpoint= breakpoint;
		fContentProvider= new ThreadFilterContentProvider();
		fCheckHandler= new CheckHandler();
		init(JavaBreakpointPreferenceStore.THREAD_FILTER, ActionMessages.getString("ThreadFilterViewer.Thread_filtering_1")); //$NON-NLS-1$
		createControl(parent);
	}
	
	/**
	 * Create and initialize the thread filter tree viewer.
	 */
	protected void createThreadViewer() {
		GridData data= new GridData();
		data.horizontalAlignment= GridData.FILL;
		data.grabExcessHorizontalSpace= true;
		data.heightHint= 100;

		fThreadViewer= new CheckboxTreeViewer(fOuter, SWT.BORDER);
		fThreadViewer.addCheckStateListener(fCheckHandler);
		fThreadViewer.getTree().setLayoutData(data);
		fThreadViewer.setContentProvider(fContentProvider);
		fThreadViewer.setLabelProvider(new DelegatingModelPresentation());
		fThreadViewer.setInput(DebugPlugin.getDefault().getLaunchManager());
		setInitialCheckedState();
	}
	
	/**
	 * Sets the initial checked state of the tree viewer.
	 * The initial state should reflect the current state
	 * of the breakpoint. If the breakpoint has a thread
	 * filter in a given thread, that thread should be
	 * checked.
	 */
	protected void setInitialCheckedState() {
		try {
			IDebugTarget[] targets= getDebugTargets();
			IJavaDebugTarget target;
			for (int i= 0, numTargets= targets.length; i < numTargets; i++) {
				if (!(targets[i] instanceof IJavaDebugTarget)) {
					continue;
				}
				target= (IJavaDebugTarget)targets[i];
				IJavaThread filteredThread= fBreakpoint.getThreadFilter(target);
				if (filteredThread != null) {
					fCheckHandler.checkThread(filteredThread, true);
				}
			}
		} catch (CoreException exception) {
			JDIDebugUIPlugin.logError(exception);
		}
	}

	/**
	 * Returns the debug targets that appear in the tree
	 */
	protected IDebugTarget[] getDebugTargets() {
		Object input= fThreadViewer.getInput();
		if (!(input instanceof ILaunchManager)) {
			return new IJavaDebugTarget[0];
		}
		ILaunchManager launchManager= (ILaunchManager)input;
		return launchManager.getDebugTargets();
	}

	/**
	 * @see FieldEditor#adjustForNumColumns(int)
	 */
	protected void adjustForNumColumns(int numColumns) {
		((GridData) fOuter.getLayoutData()).horizontalSpan = numColumns;
	}

	/**
	 * @see FieldEditor#doFillIntoGrid(Composite, int)
	 */
	protected void doFillIntoGrid(Composite parent, int numColumns) {
		fOuter= new Composite(parent, SWT.NONE);

		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.numColumns = numColumns;
		fOuter.setLayout(layout);
		
		GridData data= new GridData();
		data.verticalAlignment = GridData.FILL;
		data.horizontalAlignment = GridData.FILL;
		fOuter.setLayoutData(data);
		
		data = new GridData();
		data.horizontalSpan = numColumns;
		getLabelControl(fOuter).setLayoutData(data);
		createThreadViewer();
	}

	/**
	 * @see FieldEditor#doLoad()
	 */
	protected void doLoad() {
	}

	/**
	 * @see FieldEditor#doLoadDefault()
	 */
	protected void doLoadDefault() {
	}

	/**
	 * @see FieldEditor#doStore()
	 */
	protected void doStore() {
		IDebugTarget[] targets= getDebugTargets();
		IDebugTarget debugTarget;
		IJavaDebugTarget target;
		IThread[] threads;
		IJavaThread thread;
		for (int i= 0, numTargets= targets.length; i < numTargets; i++) {
			debugTarget= targets[i];
			if (debugTarget instanceof IJavaDebugTarget) {
				target= (IJavaDebugTarget)debugTarget;
				try {
					if (fThreadViewer.getChecked(target)) {
						threads= target.getThreads();
						for (int j=0, numThreads= threads.length; j < numThreads; j++) {
							thread= (IJavaThread)threads[j];
							if (fThreadViewer.getChecked(thread)) {
								// thread selected for filtering
								fBreakpoint.setThreadFilter(thread);
								break; // Can only set one filtered thread.
							}
						}
					} else {
						fBreakpoint.removeThreadFilter(target);
					}
				} catch (CoreException exception) {
					JDIDebugUIPlugin.logError(exception);
				}
			}
		}
	}

	/**
	 * @see FieldEditor#getNumberOfControls()
	 */
	public int getNumberOfControls() {
		return 1;
	}
	
	class CheckHandler implements ICheckStateListener {	
		public void checkStateChanged(CheckStateChangedEvent event) {
			Object element= event.getElement();
			if (element instanceof IDebugTarget) {
				checkTarget((IDebugTarget)element, event.getChecked());
			} else if (element instanceof IThread) {
				checkThread((IThread)element, event.getChecked());
			}
			verifyCheckedState();
		}
		
		/**
		 * Check or uncheck a debug target in the tree viewer.
		 * When a debug target is checked, attempt to
		 * check one of the target's threads by default.
		 * When a debug target is unchecked, uncheck all
		 * its threads.
		 */
		protected void checkTarget(IDebugTarget target, boolean checked) {
			fThreadViewer.setChecked(target, checked);
			if (checked) {
				fThreadViewer.expandToLevel(target, TreeViewer.ALL_LEVELS);
				IThread[] threads;
				try {
					threads= target.getThreads();
				} catch (DebugException exception) {
					JDIDebugUIPlugin.logError(exception);
					return;
				}
				IThread thread;
				boolean checkedThread= false;
				// Try to check the "main" thread by default
				for (int i= 0, numThreads= threads.length; i < numThreads; i++) {
					thread= threads[i];
					String name= null;
					try {
						name= thread.getName();
					} catch (DebugException exception) {
						JDIDebugUIPlugin.logError(exception);
					}
					if (MAIN.equals(name)) {
						checkedThread= fThreadViewer.setChecked(thread, true);
					}
				}
				// If the main thread couldn't be checked, check the first
				// available thread
				if (!checkedThread) {
					for (int i= 0, numThreads= threads.length; i < numThreads; i++) {
						if (fThreadViewer.setChecked(threads[i], true)) {
							break;
						}
					}
				}
			} else { // Unchecked
				IThread[] threads;
				try {
					threads= target.getThreads();
				} catch (DebugException exception) {
					JDIDebugUIPlugin.logError(exception);
					return;
				}
				for (int i= 0, numThreads= threads.length; i < numThreads; i++) {
					fThreadViewer.setChecked(threads[i], false);
				}
			}
		}
	
		/**
		 * Check or uncheck a thread.
		 * When a thread is checked, make sure its debug
		 * target is also checked.
		 * When a thread is unchecked, uncheck its debug
		 * target.
		 */
		protected void checkThread(IThread thread, boolean checked) {	
			fThreadViewer.setChecked(thread, checked);
			IDebugTarget target= (thread).getDebugTarget();
			if (checked) {
				// When a thread is checked, make sure the target
				// is checked and all other threads are
				// unchecked (simulate radio button behavior)
				if (!fThreadViewer.getChecked(target)) {					
					fThreadViewer.setChecked(target, true);
				}
				IThread[] threads;
				try {
					threads= target.getThreads();
				} catch (DebugException exception) {
					JDIDebugUIPlugin.logError(exception);
					return;
				}
				for (int i= 0, numThreads= threads.length; i < numThreads; i++) {
					if (threads[i] != thread) {
						// Uncheck all threads other than the selected thread
						fThreadViewer.setChecked(threads[i], false);
					}
				}
			} else {
				// When a thread is unchecked, uncheck the target
				fThreadViewer.setChecked(target, false);
			}
		}
	
		/**
		 * Verify the state of the tree viewer.
		 * If the user selects a debug target, they must select
		 * a thread.
		 */
		protected void verifyCheckedState() {
			IDebugTarget[] targets= getDebugTargets();
			IDebugTarget target;
			IThread[] threads;
			boolean checkedThread;
			for (int i= 0, numTargets= targets.length; i < numTargets; i++) {
				target= targets[i];
				if (!fThreadViewer.getChecked(target)) {
					continue;
				}
				try {
					threads= target.getThreads();
				} catch (DebugException exception) {
					JDIDebugUIPlugin.logError(exception);
					continue;
				}
				checkedThread= false;
				for (int j= 0, numThreads= threads.length; j < numThreads; j++) {
					if (fThreadViewer.getChecked(threads[j])) {
						checkedThread= true;
						break;
					}
				}
				if (checkedThread) {
					clearErrorMessage();
				} else {
					showErrorMessage(ActionMessages.getString("ThreadFilterViewer.Must_select_a_thread_in_selected_targets_2")); //$NON-NLS-1$
				}
			}
		}
		
	}
	
	class ThreadFilterContentProvider implements ITreeContentProvider {
		
		/**
		 * @see ITreeContentProvider#getChildren(Object)
		 */
		public Object[] getChildren(Object parent) {
			if (parent instanceof IJavaDebugTarget) {
				try {
					return ((IJavaDebugTarget)parent).getThreads();
				} catch (DebugException e) {
					JDIDebugUIPlugin.log(e.getStatus());
				}
			}		
			if (parent instanceof ILaunchManager) {
				List children= new ArrayList();
				ILaunch[] launches= ((ILaunchManager) parent).getLaunches();
				IDebugTarget[] targets;
				IJavaDebugTarget target;
				for (int i= 0, numLaunches= launches.length; i < numLaunches; i++) {
					targets= launches[i].getDebugTargets();
					for (int j= 0, numTargets= targets.length; j < numTargets; j++) {
						if (!(targets[j] instanceof IJavaDebugTarget)) {
							continue;
						}
						target= (IJavaDebugTarget)targets[j];
						if (!target.isDisconnected() && !target.isTerminated()) {
							children.add(target);
						}
					}
				}
				return children.toArray();
			}
			return new Object[0];
		}

		/**
		 * @see ITreeContentProvider#getParent(Object)
		 */
		public Object getParent(Object element) {
			if (element instanceof IThread) {
				return ((IThread)element).getDebugTarget();
			}
			if (element instanceof IDebugTarget) {
				return ((IDebugElement)element).getLaunch();
			}
			if (element instanceof ILaunch) {
				return DebugPlugin.getDefault().getLaunchManager();
			}
			return null;
		}

		/**
		 * @see ITreeContentProvider#hasChildren(Object)
		 */
		public boolean hasChildren(Object element) {
			if (element instanceof IStackFrame) {
				return false;
			}
			if (element instanceof IDebugElement) {
				return getChildren(element).length > 0;
			} 
			if (element instanceof ILaunch) {
				return true;
			}
			if (element instanceof ILaunchManager) {
				return ((ILaunchManager) element).getLaunches().length > 0;
			}
			return false;
		}

		/**
		 * @see IStructuredContentProvider#getElements(Object)
		 */
		public Object[] getElements(Object inputElement) {
			return getChildren(inputElement);
		}

		/**
		 * @see IContentProvider#dispose()
		 */
		public void dispose() {
		}

		/**
		 * @see IContentProvider#inputChanged(Viewer, Object, Object)
		 */
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
	}

}
