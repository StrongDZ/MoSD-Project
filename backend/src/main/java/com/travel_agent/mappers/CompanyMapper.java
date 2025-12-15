package com.travel_agent.mappers;

import java.util.List;

import org.mapstruct.Mapper;

import com.travel_agent.dto.CompanyDTO;
import com.travel_agent.models.entity.CompanyEntity;

@Mapper(componentModel = "spring")
public interface CompanyMapper {
    CompanyDTO companyToCompanyDTO (CompanyEntity company);
    List<CompanyDTO> companiesToCompanyDTOs (List<CompanyEntity> companies);
}
