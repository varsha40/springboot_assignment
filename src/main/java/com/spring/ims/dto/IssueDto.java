package com.spring.ims.dto;

import lombok.Getter;
import lombok.Setter;

//@NoArgsConstructor
//@AllArgsConstructor
@Getter
@Setter
public class IssueDto {
	
	private String title;
	private String responsible;
	private String description;
	private String status;
	private String severity;
	
}
