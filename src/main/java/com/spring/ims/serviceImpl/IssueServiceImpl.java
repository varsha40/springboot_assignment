package com.spring.ims.serviceImpl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.spring.ims.IConstants.IConstants;
import com.spring.ims.dto.IssueDto;
import com.spring.ims.enums.Severity;
import com.spring.ims.exception.InvalidInputException;
import com.spring.ims.exception.ResourceNotFoundException;
import com.spring.ims.models.Issue;
import com.spring.ims.models.User;
import com.spring.ims.repository.IssueRepository;
import com.spring.ims.repository.UserRepository;
import com.spring.ims.security.services.UserDetailsImpl;
import com.spring.ims.services.IssueService;

@Service
public class IssueServiceImpl implements IssueService {

	@Autowired
	private IssueRepository issueRepository;

	@Autowired
	private UserRepository userRepository;
	
	
	/**
	 * Add new issue to the DB
	 * 
	 * @RequestBody issueDto
	 * 
	 * @return {@link ResponseEntity<Issue>}
	 * 
	 * @throws InvalidInputException
	 * 
	 */
	@Override
	public Issue addIssue(IssueDto issueDto) throws InvalidInputException {
		
		// Performs empty and null checks
		if(issueDto == null) {
			throw new InvalidInputException(IConstants.REQUEST_NULL_OR_EMPTY);
		}
		if(!validateString(issueDto.getTitle())) {
			throw new InvalidInputException(IConstants.TITLE_NULL_OR_EMPTY);
		}
		if(!validateString(issueDto.getDescription())) {
			throw new InvalidInputException(IConstants.DESCRIPTION_NULL_OR_EMPTY);
		}
		if(!validateString(issueDto.getStatus())) {
			throw new InvalidInputException(IConstants.STATUS_NULL_OR_EMPTY);
		}
		if(!validateString(issueDto.getResponsible())) {
			throw new InvalidInputException(IConstants.RESPONSIBLE_NULL_OR_EMPTY);
		}
		
		Issue issue = new Issue();

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String username = ((UserDetailsImpl) auth.getPrincipal()).getUsername();
		Optional<User> userOpt = userRepository.findByUsername(username);

		if(!userOpt.isPresent()){

			throw new ResourceNotFoundException(IConstants.USER_NOT_FOUND);
		}

		issue.setUser(userOpt.get());
		
		// Saves issue data to the DB
		return issueRepository.save(setIssueValue(issueDto, issue));	
	}
	
	
	/**
	 * This API updates an existing issue.
	 * 
	 * @PathVariabe issueId
	 * @RequestBody issueDto
	 * 
	 * @return {@link ResponseEntity<Issue>}
	 * 
	 * @throws InvalidInputException
	 */
	@Override
	public Issue updateIssue(Long issueId, IssueDto issueDto) throws InvalidInputException, ResourceNotFoundException{
		
		// Checks whether issue Id is empty or null.
		if (issueId == null) {
			throw new InvalidInputException(IConstants.ISSUE_ID_NULL_OR_EMPTY);
		}
		
		// Performs empty and null checks
		if (issueDto == null) {
			throw new InvalidInputException(IConstants.REQUEST_NULL_OR_EMPTY);
		}
		if (!validateString(issueDto.getTitle())) {
			throw new InvalidInputException(IConstants.TITLE_NULL_OR_EMPTY);
		}
		if (!validateString(issueDto.getDescription())) {
			throw new InvalidInputException(IConstants.DESCRIPTION_NULL_OR_EMPTY);
		}
		if (!validateString(issueDto.getStatus())) {
			throw new InvalidInputException(IConstants.STATUS_NULL_OR_EMPTY);
		}
		if (!validateString(issueDto.getResponsible())) {
			throw new InvalidInputException(IConstants.RESPONSIBLE_NULL_OR_EMPTY);
		}
		
		// Fetches issue by Issue Id
		Optional<Issue> optionalIssue = issueRepository.findById(issueId);

		if (!optionalIssue.isPresent()) {
			throw new ResourceNotFoundException(IConstants.ISSUE_NOT_FOUND);
		}
		
		
		// Saves updated issue data to the DB
		return issueRepository.save(setIssueValue(issueDto, optionalIssue.get()));

	}
	
	/**
	 * This API fetches the list of issues
	 * 
	 * @return {@link List<Issue>}
	 * 
	 */
	@Override
	public List<Issue> getAllIssues() {
		
		// Fetches list of issues
		return issueRepository.findAll();
	}
	
	/**
	 * Fetches issue by issue Id
	 * 
	 * @PathVariable issueId
	 * 
	 * @return {link {ResponseEntity<Issue>}}
	 * 
	 * @throws Exception
	 * 
	 */
	@Override
	public Issue getIssueById(Long issueId) throws InvalidInputException, ResourceNotFoundException {
		
		// Checks whether issue Id is empty or null.
		if(issueId == null) {
			throw new InvalidInputException(IConstants.ISSUE_ID_NULL_OR_EMPTY);
		}
		
		// Fetches issue by Issue Id
		Optional<Issue> optionalIssue = issueRepository.findById(issueId);
		
		// Checks whether issue is present or not
		if(!optionalIssue.isPresent()) {
			
			throw new ResourceNotFoundException(IConstants.ISSUE_NOT_FOUND);
		}
		
		return optionalIssue.get();
	}
	
	/**
	 * Deletes issue By issue Id
	 * 
	 * @PathVriable issueId
	 * 
	 * @return
	 * 
	 * @throws Exception
	 */
	@Override
	public void deleteIssueById(Long issueId) throws InvalidInputException{
		
		// Checks whether issue Id is empty or null
		if(issueId == null) {
			throw new InvalidInputException(IConstants.ISSUE_ID_NULL_OR_EMPTY);
		}
		
		// Deletes issue by Issue Id
		issueRepository.deleteById(issueId);
	}
	
	/**
	 * This method checks whether a string is empty or not
	 * 
	 * @param text
	 * 
	 * @return boolean
	 */
	private boolean validateString(String text) {
		
		return (text != null && !text.isEmpty());
	}
	
	/**
	 * Sets issue Info
	 * 
	 * @param issueDto
	 * @param issue
	 * 
	 * @return Issue
	 */
	private Issue setIssueValue(IssueDto issueDto, Issue issue) {
		
		// Sets issue Data
		issue.setTitle(issueDto.getTitle());
		issue.setDescription(issueDto.getDescription());
		issue.setStatus(issueDto.getStatus());
		issue.setResponsible(issueDto.getResponsible());
		
		if(!validateString(issueDto.getSeverity())){
			issue.setSeverity(Severity.LOW);
		}
		else {
			
			// Checks the severity type and sets the value
			switch(issueDto.getSeverity().toUpperCase()) {
			
			case "LOW":
				issue.setSeverity(Severity.LOW);
				break;
			case "MEDIUM":
				issue.setSeverity(Severity.MEDIUM);
				break;
			case "MAJOR":
				issue.setSeverity(Severity.MAJOR);
				break;
			case "CRITICAL":
				issue.setSeverity(Severity.CRITICAL);
				break;
			default:
				issue.setSeverity(Severity.LOW);
				break;
			}
		}
		
		return issue;
	}

}
