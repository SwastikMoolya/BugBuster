package com.bugbuster.service;

import com.bugbuster.dto.ProjectRequest;
import com.bugbuster.model.Project;
import com.bugbuster.model.User;
import com.bugbuster.repository.ProjectRepository;
import com.bugbuster.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepo;

    @Autowired
    private UserRepository userRepo;

    public Project createProject(ProjectRequest req, String managerId) {
        Project project = new Project();
        project.setName(req.getName());
        project.setDescription(req.getDescription());
        project.setGithubLink(req.getGithubLink());
        project.setGithubToken(req.getGithubToken());
        project.setTeamMembers(req.getTeamMembers());
        project.setCreatedBy(managerId);

        return projectRepo.save(project);
    }

    public Project updateProject(String projectId, ProjectRequest req) {
        Project project = projectRepo.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        project.setName(req.getName());
        project.setDescription(req.getDescription());
        project.setGithubLink(req.getGithubLink());
        project.setGithubToken(req.getGithubToken());
        project.setTeamMembers(req.getTeamMembers());

        return projectRepo.save(project);
    }

    public List<Project> getProjectsByUserId(String userId) {
        List<Project> memberProjects = projectRepo.findByTeamMembersContaining(userId);
        List<Project> createdProjects = projectRepo.findByCreatedBy(userId);

        Map<String, Project> uniqueProjects = new LinkedHashMap<>();

        for (Project project : memberProjects) {
            uniqueProjects.put(project.getId(), project);
        }

        for (Project project : createdProjects) {
            uniqueProjects.put(project.getId(), project);
        }

        return new ArrayList<>(uniqueProjects.values());
    }

    public Project getProjectById(String projectId) {
        return projectRepo.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
    }

    public List<User> getDevelopersByProjectId(String projectId) {
        Project project = projectRepo.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        List<String> teamMembers = project.getTeamMembers();

        return userRepo.findDevelopersInTeam(teamMembers);
    }
}
