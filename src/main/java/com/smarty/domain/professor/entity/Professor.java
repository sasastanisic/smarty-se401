package com.smarty.domain.professor.entity;

import com.smarty.domain.account.entity.Account;
import com.smarty.domain.engagement.entity.Engagement;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity(name = "professor")
public class Professor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String surname;

    @Column(nullable = false)
    private int yearsOfExperience;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @OneToMany(mappedBy = "professor", cascade = CascadeType.ALL)
    private List<Engagement> engagements = new ArrayList<>();

    public Professor() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public int getYearsOfExperience() {
        return yearsOfExperience;
    }

    public void setYearsOfExperience(int yearsOfExperience) {
        this.yearsOfExperience = yearsOfExperience;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public List<Engagement> getEngagements() {
        return engagements;
    }

    public void setEngagements(List<Engagement> engagements) {
        this.engagements = engagements;
    }

}
