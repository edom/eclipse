package com.spacetimecat.mylyn.task;

import java.io.File;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.RepositoryStatus;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskDataHandler;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataCollector;
import org.eclipse.mylyn.tasks.core.sync.ISynchronizationSession;

import com.spacetimecat.eclipse.commons.My_Closeable;

public final class My_Connector extends AbstractRepositoryConnector {

    public static final String KIND = "com.spacetimecat.mylyn.task.repository";

    private final My_Task_Data_Handler task_data_handler = new My_Task_Data_Handler();

    /**
     * If this method returns true,
     * this class must override {@link #getTaskDataHandler()}?
     */
    @Override
    public boolean canCreateNewTask (TaskRepository repository) {
        return true;
    }

    @Override
    public boolean canCreateTaskFromKey (TaskRepository repository) {
        return true;
    }

    @Override
    public String getConnectorKind () {
        return KIND;
    }

    @Override
    public String getLabel () {
        return "Erik-style GitHub-friendly Folder";
    }

    @Override
    public TaskData getTaskData (TaskRepository repository, String taskId, IProgressMonitor monitor) throws CoreException {
        return task_data_handler.get_task_data(repository, taskId, monitor);
    }

    @Override
    public void updateTaskFromTaskData (TaskRepository repository, ITask task, TaskData data) {
        task_data_handler.update_task_from_task_data(repository, task, data);
    }

    @Override
    public String getRepositoryUrlFromTaskUrl (String taskUrl) {
        System.out.println("getRepositoryUrlFromTaskUrl");
        throw new UnsupportedOperationException();
    }

    @Override
    public String getTaskIdFromTaskUrl (String taskUrl) {
        System.out.println("getTaskIdFromTaskUrl");
        throw new UnsupportedOperationException();
    }

    @Override
    public String getTaskUrl (String repositoryUrl, String taskIdOrKey) {
        System.out.println("getTaskUrl");
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasTaskChanged (TaskRepository repository, ITask task, TaskData data) {
        return true;
    }

    @Override
    public IStatus performQuery (TaskRepository repository, IRepositoryQuery query, TaskDataCollector collector,
    ISynchronizationSession session, IProgressMonitor monitor) {
        final String url = repository.getRepositoryUrl();
        try (My_Closeable _100 = My_Closeable.begin_task(monitor, "Querying " + url, 1)) {
            final File root = new File(url);
            final File[] children = root.listFiles((dir, name) -> name.endsWith(".md"));
            if (children == null) {
                return RepositoryStatus.createStatus(repository, IStatus.ERROR, My_Plugin.get_plugin_id(),
                    "Cannot open folder: " + url);
            }
            try {
                for (File child : children) {
                    String taskId = child.getName();
                    TaskData data = getTaskData(repository, taskId, monitor);
                    collector.accept(data);
                }
            } catch (CoreException e) {
                return e.getStatus();
            }
            return Status.OK_STATUS;
        }
    }

    @Override
    public void updateRepositoryConfiguration (TaskRepository repository, IProgressMonitor monitor)
        throws CoreException {
        try (My_Closeable _100 = My_Closeable.begin_task(monitor, "updateRepositoryConfiguration", 1)) {
        }
    }

    @Override
    public AbstractTaskDataHandler getTaskDataHandler () {
        return task_data_handler;
    }

}
