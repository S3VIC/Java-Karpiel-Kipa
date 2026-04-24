package karki.com.karki;

import karki.com.karki.entity.Project;
import karki.com.karki.repository.ProjectRepository;
import karki.com.karki.services.ProjectService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ProjectServiceTests {
    private ProjectService projectService;
    private ProjectRepository projectRepository;

    @BeforeEach
    public void setup() {
        projectRepository = Mockito.mock(ProjectRepository.class);
        projectService = new ProjectService(projectRepository);
    }

    @Test
    @DisplayName("Should return all projects")
    void testGetAllProjects() {
        Project p1 = new Project();
        p1.setName("P1");
        Project p2 = new Project();
        p2.setName("P2");

        Mockito.when(projectRepository.findAll()).thenReturn(Arrays.asList(p1, p2));

        List<Project> projects = projectService.getAllProjects();

        Assertions.assertEquals(2, projects.size());
        Mockito.verify(projectRepository, Mockito.times(1)).findAll();
    }

    @Test
    @DisplayName("Should create a new project")
    void testCreateProject() {
        Project project = new Project();
        project.setName("New Project");
        project.setDescription("Desc");

        Mockito.when(projectRepository.save(project)).thenReturn(project);

        Project created = projectService.createProject(project);

        Assertions.assertEquals("New Project", created.getName());
        Mockito.verify(projectRepository, Mockito.times(1)).save(project);
    }

    @Test
    @DisplayName("Should return project by id")
    void testFindProjectById() {
        Long id = 1L;
        Project project = new Project();
        project.setId(id);
        project.setName("P1");

        Mockito.when(projectRepository.findById(id)).thenReturn(Optional.of(project));

        Project found = projectService.findProjectById(id);

        Assertions.assertEquals(project, found);
        Mockito.verify(projectRepository, Mockito.times(1)).findById(id);
    }

    @Test
    @DisplayName("Should throw when project not found by id")
    void testFindProjectByIdNotFound() {
        Long id = 99L;
        Mockito.when(projectRepository.findById(id)).thenReturn(Optional.empty());

        Assertions.assertThrows(RuntimeException.class, () -> projectService.findProjectById(id));
        Mockito.verify(projectRepository, Mockito.times(1)).findById(id);
    }

    @Test
    @DisplayName("Should update existing project")
    void testUpdateProject() {
        Long id = 1L;
        Project existing = new Project();
        existing.setId(id);
        existing.setName("OldName");
        existing.setDescription("OldDesc");

        Project update = new Project();
        update.setName("NewName");
        update.setDescription("NewDesc");

        Mockito.when(projectRepository.findById(id)).thenReturn(Optional.of(existing));
        Mockito.when(projectRepository.save(existing)).thenReturn(existing);

        Project result = projectService.updateProject(id, update);

        Assertions.assertEquals("NewName", result.getName());
        Assertions.assertEquals("NewDesc", result.getDescription());
        Mockito.verify(projectRepository, Mockito.times(1)).findById(id);
        Mockito.verify(projectRepository, Mockito.times(1)).save(existing);
    }

    @Test
    @DisplayName("Should throw when updating non-existing project")
    void testUpdateProjectNotFound() {
        Long id = 99L;
        Project update = new Project();
        update.setName("NewName");

        Mockito.when(projectRepository.findById(id)).thenReturn(Optional.empty());

        Assertions.assertThrows(RuntimeException.class, () -> projectService.updateProject(id, update));
        Mockito.verify(projectRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    @DisplayName("Should delete existing project")
    void testDeleteProject() {
        Long id = 1L;
        Mockito.when(projectRepository.existsById(id)).thenReturn(true);

        projectService.deleteProject(id);

        Mockito.verify(projectRepository, Mockito.times(1)).existsById(id);
        Mockito.verify(projectRepository, Mockito.times(1)).deleteById(id);
    }

    @Test
    @DisplayName("Should throw when deleting non-existing project")
    void testDeleteProjectNotFound() {
        Long id = 99L;
        Mockito.when(projectRepository.existsById(id)).thenReturn(false);

        Assertions.assertThrows(RuntimeException.class, () -> projectService.deleteProject(id));
        Mockito.verify(projectRepository, Mockito.never()).deleteById(Mockito.anyLong());
    }
}