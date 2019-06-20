package com.spacetimecat.mylyn.task;


import org.eclipse.jface.wizard.IWizard;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITaskMapping;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositoryQueryPage;
import org.eclipse.mylyn.tasks.ui.wizards.ITaskRepositoryPage;
import org.eclipse.mylyn.tasks.ui.wizards.NewTaskWizard;
import org.eclipse.mylyn.tasks.ui.wizards.RepositoryQueryWizard;
import org.eclipse.swt.widgets.Composite;

public final class My_Connector_Ui extends AbstractRepositoryConnectorUi {

    @Override
    public String getConnectorKind () {
        return My_Connector.KIND;
    }

    @Override
    public ITaskRepositoryPage getSettingsPage (TaskRepository repository) {
        return new MySettingsPage(repository);
    }

    @Override
    public IWizard getQueryWizard (TaskRepository repository, IRepositoryQuery query) {
        final RepositoryQueryWizard wizard = new RepositoryQueryWizard(repository);
        wizard.addPage(new AbstractRepositoryQueryPage("Done", repository) {

            @Override
            public void createControl (Composite parent) {
                setControl(parent);
            }

            @Override
            public String getQueryTitle () {
                return "Done";
            }

            @Override
            public void applyTo (IRepositoryQuery query) {
            }

        });
        return wizard;
    }

    @Override
    public IWizard getNewTaskWizard (TaskRepository repository, ITaskMapping selection) {
        return new NewTaskWizard(repository, selection);
    }

    @Override
    public boolean hasSearchPage () {
        return false;
    }

}
