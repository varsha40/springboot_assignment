package com.spring.ims.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spring.ims.dto.IssueDto;
import com.spring.ims.exception.InvalidInputException;
import com.spring.ims.exception.ResourceNotFoundException;
import com.spring.ims.models.Issue;
import com.spring.ims.services.IssueService;

@RestController
@RequestMapping("/api/issue")
public class IssueController {
	
	@Autowired
	private IssueService issueService;
	
	/**
	 * Add new issue to the DB
	 * 
	 * @RequestBody issueDto
	 * 
	 * @return {@link ResponseEntity<Issue>}
	 * 
	 * @throws InvalidInputException
	 */
	
	@PostMapping(value = "/", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
//	@RequestMapping(value = "/", method = RequestMethod.POST)
	@PreAuthorize("hasRole('USER') or hasRole('MODERATOR')")
	public ResponseEntity<Issue> addIssue(@RequestBody IssueDto issueDto) throws InvalidInputException {
		
		// Creates a new issue 
		Issue issue = issueService.addIssue(issueDto);
		
		// Checks whether created issue is null or not
		if(issue != null) {
			return new ResponseEntity<>(issue, HttpStatus.OK);
		}
		
		// If null, returns error response
		return new ResponseEntity<>(issue, HttpStatus.INTERNAL_SERVER_ERROR);
		
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
	@PutMapping("/{issueId}")
	@PreAuthorize("hasRole('USER') or hasRole('MODERATOR')")
	public ResponseEntity<Issue> updateIssue(@PathVariable Long issueId, @RequestBody IssueDto issueDto) throws InvalidInputException{
			
			// Updates an existing issue
			Issue issue = issueService.updateIssue(issueId, issueDto);
			
			// Checks whether issue is updated or not
			if(issue != null) {
				return new ResponseEntity<>(issue, HttpStatus.OK);
			}
			
			// If null, returns error response
			return new ResponseEntity<>(issue, HttpStatus.INTERNAL_SERVER_ERROR);
			
	}
	/**
	 * Fetches issue by issue Id
	 * 
	 * @PathVariable issueId
	 * 
	 * @return {link {ResponseEntity<Issue>}}
	 * 
	 * @throws Exception
	 */
	@GetMapping("/{issueId}")
	@PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
	public ResponseEntity<Issue> getIssue(@PathVariable Long issueId) throws InvalidInputException, ResourceNotFoundException{
			
			// Fetch issue by issue Id
			Issue issue = issueService.getIssueById(issueId);
			
			// Returns fetched issue
			return new ResponseEntity<>(issue, HttpStatus.OK);
			
	}
	
	/**
	 * This API fetches the list of issues
	 * 
	 * @return {@link List<Issue>}
	 */
	@GetMapping
	@PreAuthorize("hasRole('ADMIN')")
	public List<Issue> getAllIssues(){
		
		// Fetch list of issues
		return issueService.getAllIssues();
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
	@DeleteMapping("/{issueId}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Issue> deleteIssue(@PathVariable Long issueId) throws Exception{
			
			// Deletes issue by Issue Id
			issueService.deleteIssueById(issueId);
	
			// Returns Ok status
			return new ResponseEntity<>(HttpStatus.OK);
				
	}
}
