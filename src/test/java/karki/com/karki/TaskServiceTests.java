package karki.com.karki;

import karki.com.karki.entity.Task;
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
        Task task2 = new Task();
        task2.setTitle("T2");
        Mockito.when(taskRepository.findById(id)).thenReturn(Optional.of(task1));

        Task taskFound = taskService.findTaskById(id);
        Assertions.assertEquals(task1, taskFound);
        Mockito.verify(taskRepository, Mockito.times(1)).findById(id);
    }
}
