package karki.com.karki.controllers;


import karki.com.karki.entity.Project;
import karki.com.karki.repository.ProjectRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/projects")
public class ProjectController {
    private final ProjectRepository projectRepository;

    public ProjectController(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @GetMapping
    public List<Project> findAll() {
        return projectRepository.findAll();
    }

    @PostMapping
    public Project save(@RequestBody Project project) {
        return projectRepository.save(project);
    }
}
