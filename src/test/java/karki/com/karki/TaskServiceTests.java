package karki.com.karki;

import karki.com.karki.entity.Project;
import karki.com.karki.entity.Task;
import karki.com.karki.entity.TaskType;
import karki.com.karki.entity.User;
import karki.com.karki.repository.TaskRepository;
import karki.com.karki.services.TaskService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class TaskServiceTests {
    private TaskService taskService;
    private TaskRepository taskRepository;

    @BeforeEach
    public void setup() {
        taskRepository = Mockito.mock(TaskRepository.class);
        taskService = new TaskService(taskRepository);
    }

    @Test
    @DisplayName("Should return all tasks")
    void testGetAllTasks() {
        Task task1 = new Task();
        task1.setTitle("T1");
        Task task2 = new Task();
        task2.setTitle("T2");

        Mockito.when(taskRepository.findAll()).thenReturn(Arrays.asList(task1, task2));
        List<Task> tasks = taskService.getAllTasks();
        Assertions.assertEquals(2, tasks.size());
        Mockito.verify(taskRepository, Mockito.times(1)).findAll();
    }

    @Test
    @DisplayName("Should return task by id")
    void testGetTaskById() {
        Long id = 1L;
        Task task1 = new Task();
        task1.setId(id);
        task1.setTitle("T1");
        Mockito.when(taskRepository.findById(id)).thenReturn(Optional.of(task1));

        Task taskFound = taskService.findTaskById(id);
        Assertions.assertEquals(task1, taskFound);
        Mockito.verify(taskRepository, Mockito.times(1)).findById(id);
    }

    @Test
    @DisplayName("Should throw when task not found by id")
    void testGetTaskByIdNotFound() {
        Long id = 99L;
        Mockito.when(taskRepository.findById(id)).thenReturn(Optional.empty());

        Assertions.assertThrows(RuntimeException.class, () -> taskService.findTaskById(id));
        Mockito.verify(taskRepository, Mockito.times(1)).findById(id);
    }

    @Test
    @DisplayName("Should create a new task")
    void testCreateTask() {
        Task task = new Task();
        task.setTitle("New Task");

        Mockito.when(taskRepository.save(task)).thenReturn(task);

        Task created = taskService.createTask(task);

        Assertions.assertEquals("New Task", created.getTitle());
        Mockito.verify(taskRepository, Mockito.times(1)).save(task);
    }

    @Test
    @DisplayName("Should update existing task")
    void testUpdateTask() {
        Long id = 1L;
        Task existing = new Task();
        existing.setId(id);
        existing.setTitle("OldTitle");
        existing.setDescription("OldDesc");
        existing.setTaskType(TaskType.BUG);

        Task update = new Task();
        update.setTitle("NewTitle");
        update.setDescription("NewDesc");
        update.setTaskType(TaskType.FEATURE);
        update.setProject(new Project());
        update.setUser(new User());

        Mockito.when(taskRepository.findById(id)).thenReturn(Optional.of(existing));
        Mockito.when(taskRepository.save(existing)).thenReturn(existing);

        Task result = taskService.updateTask(id, update);

        Assertions.assertEquals("NewTitle", result.getTitle());
        Assertions.assertEquals("NewDesc", result.getDescription());
        Assertions.assertEquals(TaskType.FEATURE, result.getTaskType());
        Mockito.verify(taskRepository, Mockito.times(1)).findById(id);
        Mockito.verify(taskRepository, Mockito.times(1)).save(existing);
    }

    @Test
    @DisplayName("Should throw when updating non-existing task")
    void testUpdateTaskNotFound() {
        Long id = 99L;
        Task update = new Task();
        update.setTitle("NewTitle");

        Mockito.when(taskRepository.findById(id)).thenReturn(Optional.empty());

        Assertions.assertThrows(RuntimeException.class, () -> taskService.updateTask(id, update));
        Mockito.verify(taskRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    @DisplayName("Should delete existing task")
    void testDeleteTask() {
        Long id = 1L;
        Mockito.when(taskRepository.existsById(id)).thenReturn(true);

        taskService.deleteTask(id);

        Mockito.verify(taskRepository, Mockito.times(1)).existsById(id);
        Mockito.verify(taskRepository, Mockito.times(1)).deleteById(id);
    }

    @Test
    @DisplayName("Should throw when deleting non-existing task")
    void testDeleteTaskNotFound() {
        Long id = 99L;
        Mockito.when(taskRepository.existsById(id)).thenReturn(false);

        Assertions.assertThrows(RuntimeException.class, () -> taskService.deleteTask(id));
        Mockito.verify(taskRepository, Mockito.never()).deleteById(Mockito.anyLong());
    }
}