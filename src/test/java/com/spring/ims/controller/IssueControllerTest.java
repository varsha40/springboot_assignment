package com.spring.ims.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.spring.ims.controllers.IssueController;
import com.spring.ims.dto.IssueDto;
import com.spring.ims.enums.Severity;
import com.spring.ims.models.ERole;
import com.spring.ims.models.Issue;
import com.spring.ims.models.Role;
import com.spring.ims.models.User;
import com.spring.ims.services.IssueService;

@WebMvcTest(IssueController.class)
@ExtendWith(SpringExtension.class)
public class IssueControllerTest {
    
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IssueService issueService;

    private static ObjectMapper objectMapper;

    @InjectMocks
	private IssueController issueController;

    @BeforeEach
	public void setUp() {
		objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
	}
    
    @Test
    @WithMockUser(username = "varsha20", roles = {"MODERATOR","USER"})
    public void testAddIssueEndpointWithModeratorRole() throws Exception {
        // Create the issue to add
        IssueDto issueDto = new IssueDto();
        issueDto.setTitle("Test Issue");
        issueDto.setDescription("This is a test issue");
        issueDto.setResponsible("Test User");
        issueDto.setStatus("Open");

        
        Issue issue = new Issue();
        issue.setId(1L);
        issue.setTitle("Test Issue");
        issue.setDescription("This is a test issue");
        issue.setResponsible("Test User");
        issue.setStatus("Open");
        issue.setSeverity(Severity.LOW);
        
        // Convert the issue to JSON format
        String json = new ObjectMapper().writeValueAsString(issueDto);
        
        when(issueService.addIssue(any(IssueDto.class))).thenReturn(issue);

        // Perform the POST request with the custom user
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/issue/")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(json)
                .with(csrf())
                .with(user("varsha20").roles("MODERATOR","USER")))
                .andExpect(status().isOk())
                .andReturn();

        // Check the response body
        String responseBody = mvcResult.getResponse().getContentAsString();
        Issue addedIssue = new ObjectMapper().readValue(responseBody, Issue.class);
        assertNotNull(addedIssue);
        assertEquals(issueDto.getTitle(), addedIssue.getTitle());
        assertEquals(issueDto.getDescription(), addedIssue.getDescription());
        assertEquals(issueDto.getResponsible(), addedIssue.getResponsible());
        assertEquals(issueDto.getStatus(), addedIssue.getStatus());
        assertNotNull(addedIssue.getId());
    }
    
    @Test
    @WithMockUser(username = "varsha20", roles = {"MODERATOR","USER"})
    public void testUpdateIssueEndpointWithModeratorRole() throws Exception {
    	
    	Long issueId = 1L;
    	
        // Create the issue to add
        IssueDto issueDto = new IssueDto();
        issueDto.setTitle("Test Issue");
        issueDto.setDescription("This is a test issue");
        issueDto.setResponsible("Test User");
        issueDto.setStatus("Open");
        
        Issue updatedIssue = new Issue();
        updatedIssue.setId(issueId);
        updatedIssue.setTitle(issueDto.getTitle());
        updatedIssue.setResponsible(issueDto.getResponsible());
        updatedIssue.setStatus(issueDto.getStatus());
        updatedIssue.setDescription(issueDto.getDescription());
        
        when(issueService.updateIssue(any(Long.class), any(IssueDto.class))).thenReturn(updatedIssue);
        
        // Convert the issue to JSON format
        String json = new ObjectMapper().writeValueAsString(issueDto);
        
        when(issueService.updateIssue(any(Long.class), any(IssueDto.class))).thenReturn(updatedIssue);

        // Perform the POST request with the custom user
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put("/api/issue/{issueId}", 1L)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(json)
                .with(csrf())
                .with(user("varsha20").roles("MODERATOR","USER")))
                .andExpect(status().isOk())
                .andReturn();

        // Check the response body
        String responseBody = mvcResult.getResponse().getContentAsString();
        Issue updatedIssueResult = new ObjectMapper().readValue(responseBody, Issue.class);
        assertNotNull(updatedIssueResult);
        assertEquals(issueDto.getTitle(), updatedIssueResult.getTitle());
        assertEquals(issueDto.getDescription(), updatedIssueResult.getDescription());
        assertEquals(issueDto.getResponsible(), updatedIssueResult.getResponsible());
        assertEquals(issueDto.getStatus(), updatedIssueResult.getStatus());
    }
    
    @Test
    @WithMockUser(username = "varsha20", roles = {"MODERATOR","USER","ADMIN"})
    public void getIssueByIdTest() throws Exception {
    	
    	 Issue issue = new Issue();
         issue.setId(1L);
         issue.setTitle("Test Issue");
         issue.setDescription("This is a test issue");
         issue.setResponsible("Test User");
         issue.setStatus("Open");
         issue.setSeverity(Severity.LOW);
         
         // Mock the service method call
         when(issueService.getIssueById(any(Long.class))).thenReturn(issue);
         
         // Perform the POST request with the custom user
         MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/issue/{issueId}", 1L)
                 .contentType(MediaType.APPLICATION_JSON_VALUE)
                 .with(csrf())
                 .with(user("varsha20").roles("MODERATOR","USER","ADMIN")))
                 .andExpect(status().isOk())
                 .andReturn();
         // Check the response body
         String responseBody = mvcResult.getResponse().getContentAsString();
         Issue issueResult = new ObjectMapper().readValue(responseBody, Issue.class);
         assertNotNull(issueResult);
    }
    
    @Test
    @WithMockUser(username = "varsha20", roles = {"ADMIN"})
    public void getAllIssuesTest() throws Exception {
    	
    	
    	 Issue issue1 = new Issue();
         issue1.setId(1L);
         issue1.setTitle("Test Issue");
         issue1.setDescription("This is a test issue");
         issue1.setSeverity(Severity.LOW);
         issue1.setResponsible("Developer");
         issue1.setUser(new User());
         
         
         Issue issue2 = new Issue();
         issue2.setId(2L);
         issue2.setTitle("Test Issue");
         issue2.setDescription("This is a test issue");
         issue2.setSeverity(Severity.LOW);
         issue2.setResponsible("Developer");
         issue2.setUser(new User());
         
         List<Issue> issues = new ArrayList<>();
         issues.add(issue1);
         issues.add(issue2);
         
         // Mock the service method call
         when(issueService.getAllIssues()).thenReturn(issues);
         
         // Perform the POST request with the custom user
         MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/issue/")
                 .contentType(MediaType.APPLICATION_JSON_VALUE)
                 .with(csrf())
                 .with(user("varsha20").roles("ADMIN")))
                 .andExpect(status().isOk())
                 .andReturn();
         
         // Check the response body
         String responseBody = mvcResult.getResponse().getContentAsString();
         List<Issue> issueResult = new ObjectMapper().readValue(responseBody, List.class);
         assertEquals(issues.size(), issueResult.size());
    }
    
    @Test
    @WithMockUser(username = "varsha20", roles = {"ADMIN"})
    public void testDeleteIssue() throws Exception {
    	
    	Long issueId = 1L;
    	
        // Mock the service method call
        doNothing().when(issueService).deleteIssueById(issueId);

        // Make a DELETE request to the api/issue/{issueId} endpoint
        mockMvc.perform(delete("/api/issue/{issueId}", issueId)
        		  .contentType(MediaType.APPLICATION_JSON_VALUE)
                  .with(csrf())
                  .with(user("varsha20").roles("ADMIN")))
                  .andExpect(status().isOk())
                  .andReturn();
    }

}
