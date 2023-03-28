package com.spring.ims.services;
import java.util.List;

import org.springframework.stereotype.Service;

import com.spring.ims.dto.IssueDto;
import com.spring.ims.exception.InvalidInputException;
import com.spring.ims.exception.ResourceNotFoundException;
import com.spring.ims.models.Issue;

@Service
public interface IssueService {

    public Issue addIssue(IssueDto issueDto) throws InvalidInputException;
	
	public Issue updateIssue(Long issueId, IssueDto issueDto) throws InvalidInputException, ResourceNotFoundException;
	
	public List<Issue> getAllIssues();
	
	public Issue getIssueById(Long issueId) throws ResourceNotFoundException;
	
	public void deleteIssueById(Long issueId) throws InvalidInputException;
}
