package com.spring.ims.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.spring.ims.dto.IssueDto;
import com.spring.ims.enums.Severity;
import com.spring.ims.exception.InvalidInputException;
import com.spring.ims.exception.ResourceNotFoundException;
import com.spring.ims.models.ERole;
import com.spring.ims.models.Issue;
import com.spring.ims.models.Role;
import com.spring.ims.models.User;
import com.spring.ims.repository.IssueRepository;
import com.spring.ims.repository.UserRepository;
import com.spring.ims.security.services.UserDetailsImpl;
import com.spring.ims.serviceImpl.IssueServiceImpl;

public class IssueServiceImplTest {

    @Mock
    private IssueRepository issueRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private IssueServiceImpl issueService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAddIssue() throws Exception {
        // Given
        IssueDto issueDto = new IssueDto();
        issueDto.setTitle("Test Issue");
        issueDto.setDescription("This is a test issue");
        issueDto.setResponsible("testUser");
        issueDto.setStatus("Open");
        
        Issue issue = new Issue();
        issue.setId(1L);
        issue.setTitle("Test Issue");
        issue.setDescription("This is a test issue");
        issue.setSeverity(Severity.LOW);
        issue.setResponsible("testUser");
        
        User user = new User();
        user.setId(1L);
        user.setUsername("testUser");
        user.setEmail("varshac.bbs@gmail.com");
        
        Role role = new Role();
        role.setId(1);
        role.setName(ERole.ROLE_USER);
        Set<Role> set = new HashSet<> ();
        set.add(role);
        user.setRoles(set);
        
        issue.setUser(user);
       

        when(authentication.getPrincipal()).thenReturn(new UserDetailsImpl().build(user));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(userRepository.findByUsername(any(String.class))).thenReturn(Optional.of(user));
        when(issueRepository.save(any(Issue.class))).thenReturn(issue);
        
        // When
        issue = issueService.addIssue(issueDto);

        // Then
        Assertions.assertNotNull(issue);
        assertEquals("Test Issue", issue.getTitle());
    }
    
    @Test
    public void testupdateIssue() throws Exception {
        // Given
        IssueDto issueDto = new IssueDto();
        issueDto.setTitle("Updated Test Issue");
        issueDto.setDescription("Updated description");
        issueDto.setResponsible("Updated Responsible");
        issueDto.setStatus("Closed");
        issueDto.setSeverity("Major");
        
        Issue issue = new Issue();
        issue.setId(1L);
        issue.setTitle("Test Issue");
        issue.setDescription("This is a test issue");
        issue.setSeverity(Severity.LOW);
        issue.setResponsible("Developer");
        User user = new User();
        user.setId(1L);
        user.setUsername("testUser");
        user.setEmail("varshac.bbs@gmail.com");
        
        Role role = new Role();
        role.setId(1);
        role.setName(ERole.ROLE_USER);
        Set<Role> set = new HashSet<> ();
        set.add(role);
        user.setRoles(set);
        
        issue.setUser(user);
        
        Issue updatedIssue = new Issue();
        updatedIssue.setTitle(issueDto.getTitle());
        updatedIssue.setResponsible(issueDto.getResponsible());
        updatedIssue.setStatus(issueDto.getStatus());
        updatedIssue.setDescription(issueDto.getDescription());
        updatedIssue.setSeverity(issue.getSeverity());
        updatedIssue.setUser(issue.getUser());
        
        when(issueRepository.findById(any(Long.class))).thenReturn(Optional.of(issue));
    
        when(issueRepository.save(any(Issue.class))).thenReturn(updatedIssue);
        
        // When
        Issue updatedIssueInfo = issueService.updateIssue(1L, issueDto);

        // Then
        Assertions.assertNotNull(updatedIssueInfo);
        assertEquals(issueDto.getTitle(), updatedIssueInfo.getTitle());
    }
    @Test
    public void testAddIssueWithNullIssueDto() {
        // Given
        IssueDto issueDto = null;

        // When and Then
        Assertions.assertThrows(InvalidInputException.class, () -> {
            issueService.addIssue(issueDto);
        });
    }

    @Test
    public void testAddIssueWithNullTitle() {
        // Given
        IssueDto issueDto = new IssueDto();
        issueDto.setDescription("This is a test issue");
        issueDto.setResponsible("testUser");
        issueDto.setStatus("Open");

        // When and Then
        Assertions.assertThrows(InvalidInputException.class, () -> {
            issueService.addIssue(issueDto);
        });
    }

    @Test
    public void testAddIssueWithNullDescription() {
        // Given
        IssueDto issueDto = new IssueDto();
        issueDto.setTitle("Test Issue");
        issueDto.setResponsible("testUser");
        issueDto.setStatus("Open");

        // When and Then
        Assertions.assertThrows(InvalidInputException.class, () -> {
            issueService.addIssue(issueDto);
        });
    }

    @Test
    public void testAddIssueWithNullStatus() {
        // Given
        IssueDto issueDto = new IssueDto();
        issueDto.setTitle("Test Issue");
        issueDto.setDescription("This is a test issue");
        issueDto.setResponsible("testUser");

        // When and Then
        Assertions.assertThrows(InvalidInputException.class, () -> {
            issueService.addIssue(issueDto);
        });
    }
    
    @Test
    public void testAddIssueWithNullResponsible() {
        // Given
        IssueDto issueDto = new IssueDto();
        issueDto.setTitle("Test Issue");
        issueDto.setDescription("This is a test issue");
        issueDto.setStatus("Open");

        // When and Then
        Assertions.assertThrows(InvalidInputException.class, () -> {
            issueService.addIssue(issueDto);
        });
    }
    
    @Test
    public void getAllIssuesTest() {
    	
    	 Issue issue1 = new Issue();
         issue1.setId(1L);
         issue1.setTitle("Test Issue");
         issue1.setDescription("This is a test issue");
         issue1.setSeverity(Severity.LOW);
         issue1.setResponsible("Developer");
         User user1 = new User();
         user1.setId(1L);
         user1.setUsername("testUser1");
         user1.setEmail("varshac.bbs@gmail.com");
         
         Role role1 = new Role();
         role1.setId(1);
         role1.setName(ERole.ROLE_USER);
         Set<Role> set1 = new HashSet<> ();
         set1.add(role1);
         user1.setRoles(set1);
         
         issue1.setUser(user1);
         
         
         Issue issue2 = new Issue();
         issue2.setId(2L);
         issue2.setTitle("Test Issue");
         issue2.setDescription("This is a test issue");
         issue2.setSeverity(Severity.LOW);
         issue2.setResponsible("Developer");
         User user2 = new User();
         user2.setId(1L);
         user2.setUsername("testUser2");
         user2.setEmail("varshac.kpt@gmail.com");
         
         Role role2 = new Role();
         role2.setId(2);
         role2.setName(ERole.ROLE_MODERATOR);
         Set<Role> set2 = new HashSet<> ();
         set2.add(role1);
         user2.setRoles(set2);
         
         issue1.setUser(user2);
         
         List<Issue> issues = new ArrayList<>();
         issues.add(issue1);
         issues.add(issue2);
         
         when(issueRepository.findAll()).thenReturn(issues);
         
         List<Issue> issuesList = issueService.getAllIssues();
         assertEquals(issues.size(), issuesList.size());
    	
    }
    
    @Test
    public void getIssueByIdTest() throws ResourceNotFoundException{
    	
    	Issue issue = new Issue();
        issue.setId(1L);
        issue.setTitle("Test Issue");
        issue.setDescription("This is a test issue");
        issue.setSeverity(Severity.LOW);
        issue.setResponsible("Developer");
        User user = new User();
        user.setId(1L);
        user.setUsername("testUser");
        user.setEmail("varshac.bbs@gmail.com");
        
        Role role = new Role();
        role.setId(1);
        role.setName(ERole.ROLE_USER);
        Set<Role> set = new HashSet<> ();
        set.add(role);
        user.setRoles(set);
        
        issue.setUser(user);
        
        // Success case
        when(issueRepository.findById(any(Long.class))).thenReturn(Optional.of(issue));
        
        Issue resultIssue = issueService.getIssueById(1L);
        assertEquals(1L, resultIssue.getId());
        
        
        // Failure case
        when(issueRepository.findById(any(Long.class))).thenReturn(Optional.empty());
        
        // When and Then
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
        	issueService.getIssueById(1L);
        });
    }
    
    
    @Test
    public void deleteIssueByIdTest() throws ResourceNotFoundException {
        Long issueId = 1L;

        // Mocking the repository call
        doNothing().when(issueRepository).deleteById(issueId);

        // Calling the method to be tested
        issueService.deleteIssueById(issueId);

        // Verifying that the deleteById method of the repository was called exactly once with the specified issueId
        verify(issueRepository, times(1)).deleteById(issueId);
    }

    @Test
    public void testDeleteIssueById_NullIssueIdTest() throws InvalidInputException {
    	
        Long issueId = null;
        
        Assertions.assertThrows(InvalidInputException.class, () -> {
        	issueService.getIssueById(issueId);
        });
        
    }
}
