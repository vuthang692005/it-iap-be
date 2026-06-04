package com.example.it_iap.entity.Json;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ResumeData {
    @Valid
    private List<Skill> skills = new ArrayList<>();
    @Valid
    private List<Experience> experiences = new ArrayList<>();
    @Valid
    private List<Project> projects = new ArrayList<>();

    @Getter
    @Setter
    public static class Skill {
        @NotBlank(message = "SKILL_NAME_INVALID")
        private String name;
        private String level;
        @Min(value = 0, message = "YEARS_EXPERIENCE_INVALID")
        private int years;
    }

    @Getter
    @Setter
    public static class Experience {
        @NotBlank(message = "EXPERIENCE_POSITION_INVALID")
        private String position;
        private List<String> descriptions;
    }

    @Getter
    @Setter
    public static class Project {
        @NotBlank(message = "PROJECT_NAME_INVALID")
        private String name;
        private String role;
        private List<String> technologies;
        private List<String> descriptions;
    }
}
