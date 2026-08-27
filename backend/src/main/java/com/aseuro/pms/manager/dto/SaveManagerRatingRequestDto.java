package com.aseuro.pms.manager.dto;

import com.aseuro.pms.entity.User;
import com.aseuro.pms.entity.Employee;
import com.aseuro.pms.entity.Department;
import com.aseuro.pms.entity.Designation;
import com.aseuro.pms.entity.Team;
import com.aseuro.pms.entity.RecordStatus;
import com.aseuro.pms.entity.UserRole;
import com.aseuro.pms.repository.EmployeeRepository;
import com.aseuro.pms.repository.UserRepository;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.ArrayList;
import java.util.List;

public class SaveManagerRatingRequestDto {

    private String generalComments;

    @NotEmpty(message = "Ratings list cannot be empty")
    @Valid
    private List<KpiRatingInputDto> ratings = new ArrayList<>();

    public SaveManagerRatingRequestDto() {}

    public String getGeneralComments() { return generalComments; }
    public void setGeneralComments(String generalComments) { this.generalComments = generalComments; }

    public List<KpiRatingInputDto> getRatings() { return ratings; }
    public void setRatings(List<KpiRatingInputDto> ratings) { this.ratings = ratings; }
}

