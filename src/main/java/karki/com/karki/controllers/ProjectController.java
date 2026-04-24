package karki.com.karki.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import karki.com.karki.entity.Project;
import karki.com.karki.repository.ProjectRepository;
import karki.com.karki.services.ProjectService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/projects")
@Tag(name = "Projects", description = "Zarządzanie projektami")
public class ProjectController {
    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    @Operation(summary = "Pobierz wszystkie projekty", description = "Zwraca listę wszystkich projektów")
    public List<Project> findAll() {
        return projectService.getAllProjects();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Pobierz projekt o podanym id", description = "Zwraca projekt o podanym id lub tworzy " +
            "wyjątek jeśli nie został znaleziony")
    public Project getProjectById(@Parameter(description = "ID projektu do wyszukania") @PathVariable long id) {
        return projectService.findProjectById(id);
    }

    @PostMapping
    @Operation(summary = "Utwórz nowy projekt", description = "Dodaje nowy projekt do bazy danych")
    public Project save(@Parameter(description = "Dane nowego projektu") @RequestBody Project project) {
        return projectService.createProject(project);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Zaktualizuj projekt", description = "Aktualizuje istniejący projekt na podstawie jego ID")
    public Project update(
            @Parameter(description = "ID projektu do aktualizacji") @PathVariable Long id,
            @Parameter(description = "Nowe dane projektu") @RequestBody Project project) {
        return projectService.updateProject(id, project);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Usuń projekt", description = "Usuwa projekt na podstawie jego ID")
    public void delete(@Parameter(description = "ID projektu do usunięcia") @PathVariable Long id) {
        projectService.deleteProject(id);
    }
}
