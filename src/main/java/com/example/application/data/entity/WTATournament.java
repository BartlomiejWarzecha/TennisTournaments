package com.example.application.data.entity;

import com.example.application.data.AbstractEntity;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.Nullable;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotBlank;
import org.hibernate.annotations.Formula;

@Entity
public class Tournament extends AbstractEntity {
    @NotBlank
    private String name;

    public Tournament(){

    }

    public Tournament(String name) {
        this.name = name;
    }

    @Formula("(select count(c.id) from User c where c.tournament_id = id)")
    private int employeeCount;

    @OneToMany(mappedBy = "tournament")
    @Nullable
    private List<User> employees = new LinkedList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<User> getEmployees() {
        return employees;
    }

    public void setEmployees(List<User> employees) {
        this.employees = employees;
    }

    public int getEmployeeCount(){
        return employeeCount;
    }

}
