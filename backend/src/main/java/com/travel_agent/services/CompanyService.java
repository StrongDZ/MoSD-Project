package com.travel_agent.services;

import com.travel_agent.dto.CompanyDTO;
import com.travel_agent.exceptions.ReflectionException;
import com.travel_agent.mappers.CompanyMapper;
import com.travel_agent.models.entity.CompanyEntity;
import com.travel_agent.repositories.CompanyRepository;
import com.travel_agent.utils.ReflectionUtils;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompanyService {
    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;

    // CREATE: Create a new Company
    public CompanyDTO createCompany(CompanyDTO companyDTO) {
        // Tạo CompanyEntity liên kết với Account
        CompanyEntity company = new CompanyEntity();
        company.setUsername(companyDTO.getUsername());
        company.setPassword(companyDTO.getPassword());
        company.setEmail(companyDTO.getEmail());
        company.setCompanyName(companyDTO.getCompanyName());
        company.setRole(companyDTO.getRole());

        companyRepository.save(company);
        return companyMapper.companyToCompanyDTO(company);
    }

    // READ: Find Company by ID
    public CompanyDTO getCompanyById(Integer companyId) {
        CompanyEntity company =  companyRepository.findById(companyId)
                .orElseThrow(() -> new IllegalArgumentException("Company not found with ID: " + companyId));
        return companyMapper.companyToCompanyDTO(company);
    }

    // UPDATE: Update Company
    public CompanyDTO updateCompany(Integer companyId, CompanyDTO companyDTO) throws ReflectionException {
        CompanyEntity company = companyRepository.findById(companyId)
                .orElseThrow(() -> new IllegalArgumentException("Company not found with ID: " + companyId));

        ReflectionUtils.updateEntityFields(company, companyDTO);
        companyRepository.save(company);
        return companyMapper.companyToCompanyDTO(company);
    }

    // DELETE: Delete Company
    public void deleteCompany(Integer companyId) {
        CompanyEntity company = companyRepository.findById(companyId)
                .orElseThrow(() -> new IllegalArgumentException("Company not found with ID: " + companyId));
        companyRepository.delete(company);
    }

    // LIST ALL: Find all companies
    public List<CompanyDTO> getAllCompanies() {
        List<CompanyEntity> companies =  companyRepository.findAll();
        return companyMapper.companiesToCompanyDTOs(companies);
    }

    // Find company by username
    public CompanyDTO findByUsernameOrEmail(String username) {
        CompanyEntity company = companyRepository.findByUsernameOrEmail(username)
                .orElseThrow(() -> new IllegalArgumentException("Company not found with username: " + username));
        return companyMapper.companyToCompanyDTO(company);
    }

    // Find company by username and password
    public CompanyDTO findByUsernameAndPassword(String username, String password) {
        CompanyEntity company = companyRepository.findByUsernameAndPassword(username, password)
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));
        return companyMapper.companyToCompanyDTO(company);
    }
}
