
package com.travel_agent.models.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "company")
public class CompanyEntity extends AccountEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (name = "company_id")
    private Integer companyId;

    @Column(name = "company_name")
    private String companyName;
}
