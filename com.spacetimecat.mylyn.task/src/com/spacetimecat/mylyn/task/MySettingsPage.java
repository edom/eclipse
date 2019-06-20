package com.spacetimecat.mylyn.task;

import java.nio.file.Files;
import java.nio.file.Paths;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.wizards.AbstractTaskRepositoryPage;
import org.eclipse.mylyn.tasks.ui.wizards.ITaskRepositoryPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Text;

import com.spacetimecat.eclipse.commons.FUI;

public final class MySettingsPage extends WizardPage implements ITaskRepositoryPage {

    private final TaskRepository repository;

    private Text url;

    public MySettingsPage (TaskRepository repository) {
        super(
            "Add Erik-style task repository"
            , "A task is stored as a folder."
            , null
        );
        this.repository = repository;
    }

    @Override
    public String getRepositoryUrl () {
        return url.getText();
    }

    @Override
    public void createControl (Composite parent) {
        // TODO use SWT layout, JFace layout, or Eclipse UI Forms?
        parent.setLayout(new RowLayout());
        FUI.label(parent, "Path to root directory");
        url = new Text(parent, SWT.SINGLE);
        {
            String current = repository.getRepositoryUrl();
            if (current != null) {
                url.setText(current);
            }
        }
        FUI.button(parent, "&Browse...", e -> {
            DirectoryDialog dialog = new DirectoryDialog(parent.getShell());
            String path = dialog.open();
            if (path != null) {
                url.setText(path);
            }
        });
        setControl(parent);
    }

    @Override
    public boolean preFinish (TaskRepository repository) {
        final String url = getRepositoryUrl();
        if (url == null) {
            setErrorMessage("Path is required");
            return false;
        }
        if (!Files.isDirectory(Paths.get(url))) {
            setErrorMessage("Path must refer to a directory");
            return false;
        }
        return true;
    }


    @Override
    public void applyTo (TaskRepository repository) {
        repository.setRepositoryUrl(getRepositoryUrl());
    }


    @Override
    public void performFinish (TaskRepository repository) {
        applyTo(repository);
    }

}
