package com.travel_agent.dto;

import lombok.Data;

@Data
public class CompanyUpdateDTO {
    private String companyName;
    private String accommodationName;
    private String address;
    private String mapLink;
    private String city;
    private String thumbnail;
}
