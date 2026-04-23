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

    @PostMapping
    @Operation(summary = "Utwórz nowy projekt", description = "Dodaje nowy projekt do bazy danych")
    public Project save(@Parameter(description = "Dane nowego projektu") @RequestBody Project project) {
        return projectService.createProject(project);
    }
}
