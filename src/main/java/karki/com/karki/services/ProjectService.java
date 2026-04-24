package karki.com.karki.services;

import karki.com.karki.entity.Project;
import karki.com.karki.repository.ProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Service
public class ProjectService {
    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    public Project createProject(Project project) {
        return projectRepository.save(project);
    }

    public Project findProjectById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Projekt nie został znaleziony: " + id));
    }

    public Project updateProject(Long id, Project project) {
        Project existing = findProjectById(id);
        existing.setName(project.getName());
        existing.setDescription(project.getDescription());
        return projectRepository.save(existing);
    }

    public void deleteProject(Long id) {
        if (!projectRepository.existsById(id)) {
            throw new RuntimeException("Projekt nie został znaleziony: " + id);
        }
        projectRepository.deleteById(id);
    }
}
