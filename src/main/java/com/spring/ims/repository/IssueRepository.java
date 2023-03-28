package com.spring.ims.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.spring.ims.models.Issue;

@Repository
public interface IssueRepository extends JpaRepository<Issue, Long>{

}
