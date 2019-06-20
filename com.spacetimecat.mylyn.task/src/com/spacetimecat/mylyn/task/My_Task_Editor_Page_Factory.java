package com.spacetimecat.mylyn.task;

import org.eclipse.mylyn.commons.ui.CommonImages;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPageFactory;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditorInput;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.forms.editor.IFormPage;

public final class My_Task_Editor_Page_Factory extends AbstractTaskEditorPageFactory {

    @Override
    public boolean canCreatePageFor (TaskEditorInput input) {
        return My_Connector.KIND.equals(input.getTask().getConnectorKind());
    }

    @Override
    public IFormPage createPage (TaskEditor parentEditor) {
        return new My_Task_Editor_Page(parentEditor);
    }

    @Override
    public Image getPageImage () {
        return CommonImages.getImage(TasksUiImages.REPOSITORY_SMALL);
    }

    @Override
    public String getPageText () {
        return "Erik";
    }

    @Override
    public int getPriority () {
        return PRIORITY_TASK;
    }

}
