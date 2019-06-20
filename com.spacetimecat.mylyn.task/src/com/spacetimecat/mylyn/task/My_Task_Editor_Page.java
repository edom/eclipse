package com.spacetimecat.mylyn.task;

import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPage;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;

public final class My_Task_Editor_Page extends AbstractTaskEditorPage {

    public My_Task_Editor_Page (TaskEditor editor) {
        super(editor, My_Connector.KIND);
    }

}
