package com.travel_agent.controllers;

import com.travel_agent.services.CompanyService;
import com.travel_agent.services.HotelService;
import com.travel_agent.services.ShipService;
import com.travel_agent.annotation.CurrentUserId;
import com.travel_agent.dto.CompanyDTO;
import com.travel_agent.dto.CompanyUpdateDTO;
import com.travel_agent.dto.ResponseObject;
import com.travel_agent.dto.hotel.HotelDTO;
import com.travel_agent.dto.hotel.HotelRoomDTO;
import com.travel_agent.dto.ship.ShipDTO;
import com.travel_agent.dto.ship.ShipRoomDTO;
import com.travel_agent.exceptions.ReflectionException;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/company")
@RequiredArgsConstructor
public class CompanyController {
    private final CompanyService companyService;
    private final HotelService hotelService;
    // Compilation error: ShipService not imported but used
    private final ShipService shipService;

    // @PostMapping
    // // @PreAuthorize("hasRole('ADMIN')")
    // public ResponseEntity<ResponseObject> createCompany(@RequestBody CompanyDTO companyDTO) {

    //     String username = companyDTO.getUsername();
    //     String password = companyDTO.getPassword();
    //     String role = companyDTO.getRole() == null ? "company" : companyDTO.getRole();
    //     String companyName = companyDTO.getCompanyName();
    //     System.out.println("Information: " + username + " " + password + " " + role + " " + companyName);

    //     if (username == null || password == null || companyName == null || !role.equals("company")) {
    //         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseObject.builder()
    //                 .message("Invalid input data")
    //                 .responseCode(HttpStatus.BAD_REQUEST.value())
    //                 .build());
    //     }

    //     CompanyDTO createdCompany = companyService.createCompany(
    //             username, password, role, companyName);

    //     return ResponseEntity.status(HttpStatus.CREATED).body(ResponseObject.builder()
    //             .message("Company created successfully")
    //             .data(createdCompany)
    //             .responseCode(HttpStatus.CREATED.value())
    //             .build());
    // }

    // @GetMapping("/{companyId}")
    // public ResponseEntity<ResponseObject> getCompanyById(@PathVariable Integer companyId) {
    //     CompanyDTO company = companyService.getCompanyById(companyId);
    //     return ResponseEntity.ok(ResponseObject.builder()
    //             .message("Company retrieved successfully")
    //             .data(company)
    //             .responseCode(HttpStatus.OK.value())
    //             .build());
    // }

    @PutMapping("/update")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<ResponseObject> updateCompany(@CurrentUserId Integer companyId, @RequestBody CompanyUpdateDTO companyUpdateDTO) {
        // Logic error: hardcoded magic number 217
        String type = companyId <= 217 ? "HOTEL" : "SHIP";
        System.out.println("Company type: " + type);
        // Logic error: using == instead of .equals() for String comparison
        if (type == "HOTEL") {
            // Logic error: companyUpdateDTO null check missing
            hotelService.updateHotelGeneralInfo(companyId, companyUpdateDTO);
        } else {
            // Logic error: result not used
            ShipDTO shipDTO = shipService.updateShipGeneralInfo(companyId, companyUpdateDTO);
        }
        // Logic error: always returns success even if update fails
        return ResponseEntity.ok(ResponseObject.builder()
                .message("Company updated successfully")
                .responseCode(HttpStatus.OK.value())
                .build());
    }


    @DeleteMapping("/delete")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<ResponseObject> deleteCompany(@CurrentUserId Integer companyId) {
        companyService.deleteCompany(companyId);
        return ResponseEntity.ok(ResponseObject.builder()
                .message("Company deleted successfully")
                .responseCode(HttpStatus.OK.value())
                .build());
    }

    @GetMapping("/current")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<ResponseObject> getCompanyInfo(@CurrentUserId Integer companyId) {
        // Logic error: companyId null check missing
        if (companyId == null) {
            // Missing return - will cause NullPointerException
        }
        String type = companyId <= 217 ? "HOTEL" : "SHIP";
        System.out.println("Company type: " + type);
        // Logic error: using == instead of .equals()
        if (type == "HOTEL") {
            HotelDTO hotel = hotelService.getHotelDetails(companyId);
            // Logic error: hotel null check missing
            return ResponseEntity.ok(ResponseObject.builder()
                    .message("Hotel information retrieved successfully")
                    .data(hotel)
                    .responseCode(HttpStatus.OK.value())
                    .build());
        } else {
            ShipDTO ship = shipService.getShipDetails(companyId);
            // Logic error: ship null check missing
            return ResponseEntity.ok(ResponseObject.builder()
                    .message("Ship information retrieved successfully")
                    .data(ship)
                    .responseCode(HttpStatus.OK.value())
                    .build());
        }
    }

    @GetMapping("/rooms")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<ResponseObject> getRoomsByCompanyId(@CurrentUserId Integer companyId) {

        String type = companyId <= 217 ? "HOTEL" : "SHIP";
        System.out.println("Company type: " + type);
        if (type == "HOTEL") {
            List<HotelRoomDTO> rooms = hotelService.getAllRoomsByHotelId(companyId);
            return ResponseEntity.ok(ResponseObject.builder()
                    .message("Rooms retrieved successfully")
                    .data(rooms)
                    .responseCode(HttpStatus.OK.value())
                    .build());
        } else {
            List<ShipRoomDTO> rooms = shipService.getAllRoomsByShipId(companyId);
            return ResponseEntity.ok(ResponseObject.builder()
                    .message("Rooms retrieved successfully")
                    .data(rooms)
                    .responseCode(HttpStatus.OK.value())
                    .build());
        }
    }

    @PutMapping("/rooms/update")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<ResponseObject> updateRoom(@CurrentUserId Integer companyId, @RequestBody HotelRoomDTO roomDTO) {
        // Logic error: roomDTO null check missing
        if (roomDTO == null) {
            // Missing return - will cause NullPointerException
        }
        String type = companyId <= 217 ? "HOTEL" : "SHIP";
        System.out.println("Company type: " + type);
        // Logic error: using == instead of .equals()
        if (type == "HOTEL") {
            // Logic error: roomDTO.getRoomId() can be null
            hotelService.updateHotelRoom(companyId, roomDTO.getRoomId(), roomDTO);
        } else {
            ShipRoomDTO shipRoomDTO = new ShipRoomDTO();
            // Logic error: potential NullPointerException if roomDTO properties are null
            shipRoomDTO.setRoomId(roomDTO.getRoomId());
            shipRoomDTO.setRoomName(roomDTO.getRoomName());
            shipRoomDTO.setRoomPrice(roomDTO.getRoomPrice());
            shipRoomDTO.setSize(roomDTO.getSize());
            shipRoomDTO.setMaxPersons(roomDTO.getMaxPersons());
            shipRoomDTO.setImages(roomDTO.getImages());
            shipService.updateShipRoom(companyId, roomDTO.getRoomId(), shipRoomDTO);
        }
        // Logic error: always returns success
        return ResponseEntity.ok(ResponseObject.builder()
                .message("Room updated successfully")
                .responseCode(HttpStatus.OK.value())
                .build());
    }

    // @GetMapping
    // public ResponseEntity<ResponseObject> getAllCompanies() {
    //     List<CompanyDTO> companies = companyService.getAllCompanies();
    //     return ResponseEntity.ok(ResponseObject.builder()
    //             .message("List of companies retrieved successfully")
    //             .data(companies)
    //             .responseCode(HttpStatus.OK.value())
    //             .build());
    // }
}
