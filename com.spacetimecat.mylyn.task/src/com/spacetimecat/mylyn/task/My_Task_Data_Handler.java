package com.spacetimecat.mylyn.task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskMapping;
import org.eclipse.mylyn.tasks.core.RepositoryResponse;
import org.eclipse.mylyn.tasks.core.RepositoryStatus;
import org.eclipse.mylyn.tasks.core.RepositoryResponse.ResponseKind;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskDataHandler;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMetaData;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskMapper;

import com.spacetimecat.eclipse.commons.My_Closeable;

public final class My_Task_Data_Handler extends AbstractTaskDataHandler {

    @Override
    public boolean initializeTaskData (TaskRepository repository, TaskData data, ITaskMapping init,
    IProgressMonitor monitor) {
        try (My_Closeable _100 = My_Closeable.begin_task(monitor, "MyTaskDataHandler.initializeTaskData", 1)) {
            initialize(data);
            monitor.worked(1);
            return true;
        }
    }

    private TaskData create_task_data (TaskRepository repository, String taskId) {
        TaskAttributeMapper mapper = getAttributeMapper(repository);
        TaskData data = new TaskData(mapper, My_Connector.KIND, repository.getRepositoryUrl(), taskId);
        initialize(data);
        return data;
    }

    // -------------------- TaskData attributes

    private static final String ATTR_FILENAME = "fileName";

    private static void initialize (TaskData data) {
        data.setVersion("0");
        TaskAttribute root = data.getRoot();
        {
            TaskAttribute attr = root.createAttribute(TaskAttribute.SUMMARY);
            {
                TaskAttributeMetaData meta = attr.getMetaData();
                meta.setType(TaskAttribute.TYPE_SHORT_TEXT);
                meta.setKind(TaskAttribute.KIND_DEFAULT);
                meta.setLabel("Summary");
            }
        }
        {
            TaskAttribute attr = root.createAttribute(TaskAttribute.DESCRIPTION);
            {
                TaskAttributeMetaData meta = attr.getMetaData();
                meta.setType(TaskAttribute.TYPE_LONG_RICH_TEXT);
                meta.setKind(TaskAttribute.KIND_DESCRIPTION);
                meta.setLabel("Description");
            }
        }
        {
            TaskAttribute attr = root.createAttribute(ATTR_FILENAME);
            {
                TaskAttributeMetaData meta = attr.getMetaData();
                meta.setType(TaskAttribute.TYPE_SHORT_TEXT);
                meta.setKind(TaskAttribute.KIND_DEFAULT);
                meta.setLabel("File Name");
            }
        }
    }

    private static String get_attribute_value (TaskData data, String key) {
        return data.getRoot().getAttribute(key).getValue();
    }

    private static void set_attribute_value (TaskData data, String key, String value) {
        data.getRoot().getAttribute(key).setValue(value);
    }

    public TaskData get_task_data (TaskRepository repository, String task_id, IProgressMonitor monitor) throws CoreException {
        try (My_Closeable _100 = My_Closeable.begin_task(monitor, "getTaskData " + task_id, 1)) {
            TaskData data = create_task_data(repository, task_id);

            set_attribute_value(data, TaskAttribute.SUMMARY, task_id);

            try {
                Path path = resolve(repository, task_id);
                String string = read_string_from_file(path);
                set_attribute_value(data, TaskAttribute.DESCRIPTION, string);
            } catch (IOException e) {
                throw new CoreException(
                    new RepositoryStatus(repository, IStatus.ERROR, My_Plugin.get_plugin_id()
                        , My_Plugin.ERROR_UNKNOWN, e.getMessage(), e));
            }

            monitor.worked(1);
            return data;
        }
    }

    private static Path resolve (TaskRepository repository, String task_id) throws CoreException {
        String repo_url = repository.getRepositoryUrl();
        Path repo_path = Paths.get(repo_url);
        Path task_path = Paths.get(repo_url, task_id);
        if (!task_path.startsWith(repo_path)) {
            throw My_Plugin.make_CoreException(repository, "Invalid task id: " + task_id);
        }
        return task_path;
    }

    private static String read_string_from_file (Path path) throws IOException {
        byte[] bytes = Files.readAllBytes(path);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    @Override
    public RepositoryResponse postTaskData (TaskRepository repository, TaskData data,
    Set<TaskAttribute> oldAttributes, IProgressMonitor monitor) throws CoreException {
        try (My_Closeable _100 = My_Closeable.begin_task(monitor, "MyTaskDataHandler.postTaskData", 1)) {
            final String task_id = data.getTaskId();
            if (task_id == null) {
                throw new CoreException(My_Plugin.make_error(repository, "Missing task id"));
            }
            final String description = get_attribute_value(data, TaskAttribute.DESCRIPTION);
            final Path task_path = resolve(repository, task_id);
            write_string_to_file(description, task_path);
            monitor.worked(1);
            return new RepositoryResponse(ResponseKind.TASK_CREATED, task_id);
        } catch (IOException e) {
            throw My_Plugin.make_CoreException(repository, "");
        }
    }

    private static void write_string_to_file (String string, Path path) throws IOException {
        byte[] bytes = string.getBytes(StandardCharsets.UTF_8);
        Files.write(path, bytes, StandardOpenOption.CREATE_NEW);
    }

    public void update_task_from_task_data (TaskRepository repository, ITask task, TaskData data) {
        TaskMapper mapper = new TaskMapper(data);
        mapper.applyTo(task);
    }

    @Override
    public TaskAttributeMapper getAttributeMapper (TaskRepository repository) {
        return new TaskAttributeMapper(repository);
    }

}
