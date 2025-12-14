package com.travel_agent.services;

import com.travel_agent.dto.*;
import com.travel_agent.dto.hotel.HotelDTO;
import com.travel_agent.dto.hotel.HotelLongDescriptionDTO;
import com.travel_agent.dto.hotel.HotelRoomDTO;
import com.travel_agent.mappers.HotelMapper;
import com.travel_agent.models.entity.hotel.*;
import com.travel_agent.repositories.*;
import com.travel_agent.repositories.hotel.*;
import com.travel_agent.models.entity.FeatureEntity;

import jakarta.persistence.criteria.CriteriaBuilder.In;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.travel_agent.models.entity.CompanyEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HotelService {
    private final HotelRepository hotelRepository;
    private final HotelMapper hotelMapper;
    private final CompanyRepository companyRepository;
    private final FeatureRepository featureRepository;
    private final HotelFeatureRepository hotelFeatureRepository;
    private final HotelShortDescriptionRepository hotelShortDescriptionRepository;
    private final HotelLongDescriptionRepository hotelLongDescriptionRepository;
    private final HotelRoomRepository hotelRoomRepository;
    private final HotelRoomFeatureRepository hotelRoomFeatureRepository;
    private final HotelImageRepository hotelImageRepository;
    private final HotelRoomImageRepository hotelRoomImageRepository;

    public ResultPaginationDTO getAllHotels(Pageable pageable) {
        Page<HotelEntity> pageHotel= hotelRepository.findAll(pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        Meta mt = new Meta();

        mt.setPage(pageHotel.getNumber());
        mt.setPageSize(pageHotel.getSize());
        mt.setPages(pageHotel.getTotalPages());
        mt.setTotal(pageHotel.getTotalElements());

        rs.setMeta((mt));
        rs.setResult(pageHotel.getContent());
        return rs;
    }

    // Search hotel by name
    public ResultPaginationDTO searchHotelsByName(String name, Pageable pageable) {
        Page<HotelEntity> hotelPage = hotelRepository.findByHotelName(name, pageable);
        
        List<HotelDTO> hotelDtos = hotelPage.getContent().stream()
                .map(hotelMapper::hotelToHotelDTO)
                .toList();

        ResultPaginationDTO result = new ResultPaginationDTO();
        result.setResult(hotelDtos);
        Meta meta = new Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPages(hotelPage.getTotalPages());
        meta.setTotal(hotelPage.getTotalElements());
        result.setMeta(meta);

        return result;
    }


    // View hotel details
    public HotelDTO getHotelDetails(Integer hotelId) {
        HotelEntity hotelEntity = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new IllegalArgumentException("Hotel not found with ID: " + hotelId));

        HotelDTO hotelDto = hotelMapper.hotelToHotelDTO(hotelEntity);

        // Fetch features
        List<HotelFeatureEntity> hotelFeatures = hotelFeatureRepository.findByHotelId(hotelId);
        List<Integer> featureIds = hotelFeatures.stream()
                .map(HotelFeatureEntity::getFeatureId)
                .toList();
        List<String> featureDescriptions = featureIds.stream()
                .map(featureId -> featureRepository.findById(featureId)
                        .orElseThrow(() -> new IllegalArgumentException("Feature not found with ID: " + featureId))
                        .getFeatureDescription())
                .toList();

        hotelDto.setFeatureIds(featureIds.isEmpty() ? null : featureIds);
        hotelDto.setFeatures(featureDescriptions.isEmpty() ? null : featureDescriptions);

        // Fetch short descriptions
        List<String> shortDescriptions = hotelShortDescriptionRepository.findByHotelId(hotelId)
                .stream()
                .map(HotelShortDescriptionEntity::getDescription)
                .toList();
        hotelDto.setShortDescriptions(shortDescriptions.isEmpty() ? null : shortDescriptions);

        // Fetch long descriptions
        List<HotelLongDescriptionEntity> longDescriptions = hotelLongDescriptionRepository.findByHotelId(hotelId);
        List<HotelLongDescriptionDTO> longDescriptionDtos = longDescriptions.stream()
                .map(desc -> new HotelLongDescriptionDTO(desc.getBlockId(), desc.getType(), desc.getData()))
                .toList();
        hotelDto.setLongDescriptions(longDescriptionDtos.isEmpty() ? null : longDescriptionDtos);

        // Fetch rooms
        List<HotelRoomEntity> hotelRoomEntities = hotelRoomRepository.findByHotel_HotelId(hotelId);
        List<HotelRoomDTO> roomDtos = hotelRoomEntities.stream()
                .map(room -> getHotelRoomDetails(room.getHotelRoomId()))
                .toList();
        hotelDto.setRooms(roomDtos.isEmpty() ? null : roomDtos);

        // Fetch images
        List<String> images = hotelImageRepository.findByHotelId(hotelId)
                .stream()
                .map(HotelImageEntity::getImgUrl)
                .toList();
        hotelDto.setImages(images.isEmpty() ? null : images);

        return hotelDto;
    }

    public List<HotelRoomDTO> getAllRoomsByHotelId(Integer hotelId) {
        // Check if the hotel exists
        hotelRepository.findById(hotelId)
                .orElseThrow(() -> new IllegalArgumentException("Hotel not found with ID: " + hotelId));

        // Fetch the list of rooms
        List<HotelRoomEntity> hotelRoomEntities = hotelRoomRepository.findByHotel_HotelId(hotelId);

        // Map entities to DTOs
        return hotelRoomEntities.stream()
                .map(room -> getHotelRoomDetails(room.getHotelRoomId()))
                .toList();
    }

    private void populateHotelEntity(HotelEntity hotelEntity, HotelDTO hotelDto) {
        hotelEntity.setHotelName(hotelDto.getHotelName());
        hotelEntity.setTotalRooms(hotelDto.getTotalRooms());
        hotelEntity.setHotelPrice(hotelDto.getHotelPrice());
        hotelEntity.setCity(hotelDto.getCity());
        hotelEntity.setAddress(hotelDto.getAddress());
        hotelEntity.setMapLink(hotelDto.getMapLink());
        hotelEntity.setThumbnail(hotelDto.getThumbnail());

        if (hotelDto.getCompanyId() != null) {
            CompanyEntity company = companyRepository.findById(hotelDto.getCompanyId())
                    .orElseThrow(() -> new IllegalArgumentException("Company not found with ID: " + hotelDto.getCompanyId()));
            hotelEntity.setCompany(company);
            hotelEntity.setCompanyName(company.getCompanyName());
        }

        hotelEntity = hotelRepository.save(hotelEntity);

        if (hotelDto.getFeatureIds() != null) {
            for (Integer featureId : hotelDto.getFeatureIds()) {
                featureRepository.findById(featureId)
                        .orElseThrow(() -> new IllegalArgumentException("Feature not found with ID: " + featureId));

                HotelFeatureEntity hotelFeature = new HotelFeatureEntity();
                hotelFeature.setHotelId(hotelEntity.getHotelId());
                hotelFeature.setFeatureId(featureId);
                hotelFeatureRepository.save(hotelFeature);
            }
        }
    }

    // Add hotel
    public HotelDTO addHotel(HotelDTO hotelDto) {
        HotelEntity hotelEntity = new HotelEntity();
        populateHotelEntity(hotelEntity, hotelDto);
        hotelEntity = hotelRepository.save(hotelEntity);

        if (hotelDto.getShortDescriptions() != null) {
            int blockId = 1;
            for (String description : hotelDto.getShortDescriptions()) {
                HotelShortDescriptionEntity shortDescription = new HotelShortDescriptionEntity();
                shortDescription.setHotelId(hotelEntity.getHotelId());
                shortDescription.setBlockId(blockId++);
                shortDescription.setDescription(description);
                hotelShortDescriptionRepository.save(shortDescription);
            }
        }

        // Save images
        if (hotelDto.getImages() != null) {
            for (String imgUrl : hotelDto.getImages()) {
                HotelImageEntity hotelImage = new HotelImageEntity();
                hotelImage.setHotelId(hotelEntity.getHotelId());
                hotelImage.setImgUrl(imgUrl);
                hotelImageRepository.save(hotelImage);
            }
        }

        // Save long descriptions
        if (hotelDto.getLongDescriptions() != null) {
            for (HotelLongDescriptionDTO longDescriptionDto : hotelDto.getLongDescriptions()) {
                HotelLongDescriptionEntity longDescription = new HotelLongDescriptionEntity();
                longDescription.setHotelId(hotelEntity.getHotelId());
                longDescription.setBlockId(longDescriptionDto.getBlockId());
                longDescription.setType(longDescriptionDto.getType());
                longDescription.setData(longDescriptionDto.getData());
                hotelLongDescriptionRepository.save(longDescription);
            }
        }

        // Map the saved hotel entity to DTO
        HotelDTO savedHotelDto = hotelMapper.hotelToHotelDTO(hotelEntity);

        // Fetch and set featureIds and features
        List<HotelFeatureEntity> hotelFeatures = hotelFeatureRepository.findByHotelId(hotelEntity.getHotelId());
        List<Integer> featureIds = hotelFeatures.stream()
                .map(HotelFeatureEntity::getFeatureId)
                .toList();
        List<String> features = featureIds.stream()
                .map(featureId -> featureRepository.findById(featureId)
                        .orElseThrow(() -> new IllegalArgumentException("Feature not found with ID: " + featureId))
                        .getFeatureDescription())
                .toList();

        savedHotelDto.setFeatureIds(featureIds.isEmpty() ? null : featureIds);
        savedHotelDto.setFeatures(features.isEmpty() ? null : features);

        savedHotelDto.setShortDescriptions(hotelDto.getShortDescriptions());
        savedHotelDto.setLongDescriptions(hotelDto.getLongDescriptions());
        savedHotelDto.setImages(hotelDto.getImages());

        return savedHotelDto;
    }

    public HotelDTO updateHotelGeneralInfo(Integer hotelId, CompanyUpdateDTO companyUpdateDTO) {
        HotelEntity hotelEntity = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new IllegalArgumentException("Hotel not found with ID: " + hotelId));
    
        if (companyUpdateDTO.getAccommodationName() != null) {
            hotelEntity.setHotelName(companyUpdateDTO.getAccommodationName());
        }
        if (companyUpdateDTO.getCompanyName() != null) {
            hotelEntity.setCompanyName(companyUpdateDTO.getCompanyName());
        }
        if (companyUpdateDTO.getAddress() != null) {
            hotelEntity.setAddress(companyUpdateDTO.getAddress());
        }
        if (companyUpdateDTO.getCity() != null) {
            hotelEntity.setCity(companyUpdateDTO.getCity());
        }
        if (companyUpdateDTO.getMapLink() != null) {
            hotelEntity.setMapLink(companyUpdateDTO.getMapLink());
        }
    
        // Lưu thay đổi
        HotelEntity updatedHotel = hotelRepository.save(hotelEntity);
    
        // Chuyển đổi sang DTO để trả về
        return hotelMapper.hotelToHotelDTO(updatedHotel);
    }
    
    // Update hotel
    public HotelDTO updateHotel(Integer hotelId, HotelDTO hotelDto) {
        HotelEntity hotelEntity = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new IllegalArgumentException("Hotel not found with ID: " + hotelId));

        // Update hotel entity fields
        populateHotelEntity(hotelEntity, hotelDto);

        // Clear existing features
        List<HotelFeatureEntity> existingFeatures = hotelFeatureRepository.findByHotelId(hotelId);
        hotelFeatureRepository.deleteAll(existingFeatures);

        // Add new features
        if (hotelDto.getFeatureIds() != null) {
            for (Integer featureId : hotelDto.getFeatureIds()) {
                featureRepository.findById(featureId)
                        .orElseThrow(() -> new IllegalArgumentException("Feature not found with ID: " + featureId));

                HotelFeatureEntity hotelFeature = new HotelFeatureEntity();
                hotelFeature.setHotelId(hotelId);
                hotelFeature.setFeatureId(featureId);
                hotelFeatureRepository.save(hotelFeature);
            }
        }
        // Clear existing short descriptions
        List<HotelShortDescriptionEntity> existingDescriptions = hotelShortDescriptionRepository.findByHotelId(hotelId);
        hotelShortDescriptionRepository.deleteAll(existingDescriptions);

        // Add new short descriptions
        if (hotelDto.getShortDescriptions() != null) {
            int blockId = 1;
            for (String description : hotelDto.getShortDescriptions()) {
                HotelShortDescriptionEntity shortDescription = new HotelShortDescriptionEntity();
                shortDescription.setHotelId(hotelId);
                shortDescription.setBlockId(blockId++);
                shortDescription.setDescription(description);
                hotelShortDescriptionRepository.save(shortDescription);
            }
        }

        // Clear existing long descriptions
        List<HotelLongDescriptionEntity> existingLongDescriptions = hotelLongDescriptionRepository.findByHotelId(hotelId);
        hotelLongDescriptionRepository.deleteAll(existingLongDescriptions);

        // Add new long descriptions
        if (hotelDto.getLongDescriptions() != null) {
            for (HotelLongDescriptionDTO longDescriptionDto : hotelDto.getLongDescriptions()) {
                HotelLongDescriptionEntity longDescription = new HotelLongDescriptionEntity();
                longDescription.setHotelId(hotelId);
                longDescription.setBlockId(longDescriptionDto.getBlockId());
                longDescription.setType(longDescriptionDto.getType());
                longDescription.setData(longDescriptionDto.getData());
                hotelLongDescriptionRepository.save(longDescription);
            }
        }

        // Clear existing images
        hotelImageRepository.deleteAll(hotelImageRepository.findByHotelId(hotelId));

        // Add new images
        if (hotelDto.getImages() != null) {
            for (String imgUrl : hotelDto.getImages()) {
                HotelImageEntity hotelImage = new HotelImageEntity();
                hotelImage.setHotelId(hotelId);
                hotelImage.setImgUrl(imgUrl);
                hotelImageRepository.save(hotelImage);
            }
        }

        // Save updated hotel entity
        hotelEntity = hotelRepository.save(hotelEntity);

        // Map updated hotel entity to DTO
        HotelDTO updatedHotelDto = hotelMapper.hotelToHotelDTO(hotelEntity);

        // Fetch and set updated featureIds and features
        List<HotelFeatureEntity> updatedFeatures = hotelFeatureRepository.findByHotelId(hotelId);
        List<Integer> featureIds = updatedFeatures.stream()
                .map(HotelFeatureEntity::getFeatureId)
                .toList();
        List<String> features = featureIds.stream()
                .map(featureId -> featureRepository.findById(featureId)
                        .orElseThrow(() -> new IllegalArgumentException("Feature not found with ID: " + featureId))
                        .getFeatureDescription())
                .toList();

        // Fetch and set updated short descriptions
        List<String> shortDescriptions = hotelShortDescriptionRepository.findByHotelId(hotelId)
                .stream()
                .map(HotelShortDescriptionEntity::getDescription)
                .toList();
        updatedHotelDto.setShortDescriptions(shortDescriptions.isEmpty() ? null : shortDescriptions);

        // Fetch and set updated long descriptions
        List<HotelLongDescriptionEntity> longDescriptions = hotelLongDescriptionRepository.findByHotelId(hotelId);
        List<HotelLongDescriptionDTO> longDescriptionDtos = longDescriptions.stream()
                .map(desc -> new HotelLongDescriptionDTO(desc.getBlockId(), desc.getType(), desc.getData()))
                .toList();
        updatedHotelDto.setLongDescriptions(longDescriptionDtos.isEmpty() ? null : longDescriptionDtos);

        updatedHotelDto.setFeatureIds(featureIds.isEmpty() ? null : featureIds);
        updatedHotelDto.setFeatures(features.isEmpty() ? null : features);

        return updatedHotelDto;
    }

    // Delete hotel
    @Transactional
    public void deleteHotels(List<Integer> hotelIds) {
        for (Integer hotelId : hotelIds) {
            HotelEntity hotelEntity = hotelRepository.findById(hotelId)
                    .orElseThrow(() -> new IllegalArgumentException("Hotel not found with ID: " + hotelId));

            // Delete associated rooms and their features
            List<HotelRoomEntity> hotelRoomEntities = hotelRoomRepository.findByHotel_HotelId(hotelId);
            for (HotelRoomEntity room : hotelRoomEntities) {
                hotelRoomFeatureRepository.deleteByHotelRoomId(room.getHotelRoomId());
            }
            hotelRoomRepository.deleteAll(hotelRoomEntities);

            // Delete associated features
            List<HotelFeatureEntity> hotelFeatures = hotelFeatureRepository.findByHotelId(hotelId);
            hotelFeatureRepository.deleteAll(hotelFeatures);

            // Delete associated short descriptions
            List<HotelShortDescriptionEntity> hotelShortDescriptions = hotelShortDescriptionRepository.findByHotelId(hotelId);
            hotelShortDescriptionRepository.deleteAll(hotelShortDescriptions);

            // Delete associated long descriptions
            List<HotelLongDescriptionEntity> hotelLongDescriptions = hotelLongDescriptionRepository.findByHotelId(hotelId);
            hotelLongDescriptionRepository.deleteAll(hotelLongDescriptions);

            // Delete associated images
            List<HotelImageEntity> hotelImages = hotelImageRepository.findByHotelId(hotelId);
            hotelImageRepository.deleteAll(hotelImages);

            // Finally, delete the hotel
            hotelRepository.delete(hotelEntity);
        }
    }

    // View room
    public HotelRoomDTO getHotelRoom(Integer hotelId, Integer roomId) {
        // Fetch the room by roomId
        HotelRoomEntity hotelRoomEntity = hotelRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found with ID: " + roomId));

        // Validate that the room belongs to the specified hotel
        if (!hotelRoomEntity.getHotel().getHotelId().equals(hotelId)) {
            throw new IllegalArgumentException("Room does not belong to the specified hotel");
        }

        // Fetch room features
        List<Integer> roomFeatureIds = hotelRoomFeatureRepository.findByHotelRoomId(roomId)
                .stream()
                .map(HotelRoomFeatureEntity::getRoomFeaturesId)
                .toList();
        List<String> roomFeatures = roomFeatureIds.stream()
                .map(featureId -> featureRepository.findById(featureId)
                        .orElseThrow(() -> new IllegalArgumentException("Feature not found with ID: " + featureId))
                        .getFeatureDescription())
                .toList();

        List<String> images = hotelRoomImageRepository.findByRoomId(roomId)
                .stream()
                .map(HotelRoomImageEntity::getImgUrl)
                .toList();

        // Map the entity to DTO
        return new HotelRoomDTO(
                hotelRoomEntity.getHotelRoomId(),
                hotelRoomEntity.getRoomName(),
                hotelRoomEntity.getRoomPrice(),
                roomFeatureIds,
                roomFeatures.isEmpty() ? null : roomFeatures,
                hotelRoomEntity.getSize(),
                hotelRoomEntity.getMaxPersons(),
                hotelRoomEntity.getBedType(),
                hotelRoomEntity.getView(),
                images.isEmpty() ? null : images
        );
    }

    // Add room
    public HotelRoomDTO addHotelRoom(Integer hotelId, HotelRoomDTO roomDto) {
        HotelEntity hotelEntity = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new IllegalArgumentException("Hotel not found with ID: " + hotelId));

        HotelRoomEntity hotelRoomEntity = new HotelRoomEntity();
        hotelRoomEntity.setHotel(hotelEntity);
        hotelRoomEntity.setRoomName(roomDto.getRoomName());
        hotelRoomEntity.setRoomPrice(roomDto.getRoomPrice());
        hotelRoomEntity.setSize(roomDto.getSize());
        hotelRoomEntity.setMaxPersons(roomDto.getMaxPersons());
        hotelRoomEntity.setBedType(roomDto.getBedType());
        hotelRoomEntity.setView(roomDto.getView());

        hotelRoomEntity = hotelRoomRepository.save(hotelRoomEntity);

        for (Integer featureId : roomDto.getRoomFeatureIds()) {
            featureRepository.findById(featureId)
                    .orElseThrow(() -> new IllegalArgumentException("Feature not found with ID: " + featureId));

            HotelRoomFeatureEntity roomFeature = new HotelRoomFeatureEntity();
            roomFeature.setHotelRoomId(hotelRoomEntity.getHotelRoomId());
            roomFeature.setRoomFeaturesId(featureId);
            hotelRoomFeatureRepository.save(roomFeature);
        }

        List<String> roomFeatures = roomDto.getRoomFeatureIds().stream()
                .map(featureId -> featureRepository.findById(featureId)
                        .orElseThrow(() -> new IllegalArgumentException("Feature not found with ID: " + featureId))
                        .getFeatureDescription())
                .toList();

        if (roomDto.getImages() != null) {
            for (String imgUrl : roomDto.getImages()) {
                HotelRoomImageEntity roomImage = new HotelRoomImageEntity();
                roomImage.setRoomId(hotelRoomEntity.getHotelRoomId());
                roomImage.setImgUrl(imgUrl);
                hotelRoomImageRepository.save(roomImage);
            }
        }

        return new HotelRoomDTO(
                hotelRoomEntity.getHotelRoomId(),
                hotelRoomEntity.getRoomName(),
                hotelRoomEntity.getRoomPrice(),
                roomDto.getRoomFeatureIds(),
                roomFeatures,
                hotelRoomEntity.getSize(),
                hotelRoomEntity.getMaxPersons(),
                hotelRoomEntity.getBedType(),
                hotelRoomEntity.getView(),
                roomDto.getImages()
        );
    }

    // Update room
    @Transactional
    public HotelRoomDTO updateHotelRoom(Integer hotelId, Integer roomId, HotelRoomDTO roomDto) {
        hotelRepository.findById(hotelId)
                .orElseThrow(() -> new IllegalArgumentException("Hotel not found with ID: " + hotelId));

        HotelRoomEntity hotelRoomEntity = hotelRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found with ID: " + roomId));

        if (!hotelRoomEntity.getHotel().getHotelId().equals(hotelId)) {
            throw new IllegalArgumentException("Room does not belong to the specified hotel");
        }

        // Update room details
        hotelRoomEntity.setRoomName(roomDto.getRoomName());
        hotelRoomEntity.setRoomPrice(roomDto.getRoomPrice());
        hotelRoomEntity.setSize(roomDto.getSize());
        hotelRoomEntity.setMaxPersons(roomDto.getMaxPersons());
        hotelRoomEntity.setBedType(roomDto.getBedType());
        hotelRoomEntity.setView(roomDto.getView());

        hotelRoomEntity = hotelRoomRepository.save(hotelRoomEntity);

        // Update room features
        List<HotelRoomFeatureEntity> existingFeatures = hotelRoomFeatureRepository.findByHotelRoomId(roomId);
        hotelRoomFeatureRepository.deleteAll(existingFeatures);

        for (Integer featureId : roomDto.getRoomFeatureIds()) {
            featureRepository.findById(featureId)
                    .orElseThrow(() -> new IllegalArgumentException("Feature not found with ID: " + featureId));

            HotelRoomFeatureEntity roomFeature = new HotelRoomFeatureEntity();
            roomFeature.setHotelRoomId(hotelRoomEntity.getHotelRoomId());
            roomFeature.setRoomFeaturesId(featureId);
            hotelRoomFeatureRepository.save(roomFeature);
        }

        hotelRoomImageRepository.deleteByRoomId(roomId);

        if (roomDto.getImages() != null) {
            for (String imgUrl : roomDto.getImages()) {
                HotelRoomImageEntity roomImage = new HotelRoomImageEntity();
                roomImage.setRoomId(hotelRoomEntity.getHotelRoomId());
                roomImage.setImgUrl(imgUrl);
                hotelRoomImageRepository.save(roomImage);
            }
        }

        return new HotelRoomDTO(
                hotelRoomEntity.getHotelRoomId(),
                hotelRoomEntity.getRoomName(),
                hotelRoomEntity.getRoomPrice(),
                roomDto.getRoomFeatureIds(),
                null, // Room features descriptions can be fetched separately if needed
                hotelRoomEntity.getSize(),
                hotelRoomEntity.getMaxPersons(),
                hotelRoomEntity.getBedType(),
                hotelRoomEntity.getView(),
                roomDto.getImages()
        );
    }

    // Delete room
    @Transactional
    public void deleteHotelRooms(Integer hotelId, List<Integer> roomIds) {
        // Check if the hotel exists
        hotelRepository.findById(hotelId)
                .orElseThrow(() -> new IllegalArgumentException("Hotel not found with ID: " + hotelId));

        // Fetch the rooms to delete
        List<HotelRoomEntity> roomsToDelete = hotelRoomRepository.findAllById(roomIds);

        // Validate that all rooms belong to the specified hotel
        for (HotelRoomEntity room : roomsToDelete) {
            if (!room.getHotel().getHotelId().equals(hotelId)) {
                throw new IllegalArgumentException("Room with ID " + room.getHotelRoomId() + " does not belong to the specified hotel");
            }
        }

        // Delete related records in hotel_room_features for each room
        for (HotelRoomEntity room : roomsToDelete) {
            hotelRoomFeatureRepository.deleteByHotelRoomId(room.getHotelRoomId());
        }

        // Delete the hotel rooms
        hotelRoomRepository.deleteAll(roomsToDelete);
    }

    public List<String> getAllCities() {
        return hotelRepository.findAll().stream()
                .map(HotelEntity::getCity)
                .filter(city -> city != null && !city.trim().isEmpty())
                .distinct()
                .sorted(String::compareToIgnoreCase)
                .collect(Collectors.toList());
    }

    public HotelRoomDTO getHotelRoomDetails(Integer hotelRoomId) {
        HotelRoomEntity room = hotelRoomRepository.findById(hotelRoomId)
                .orElseThrow(() -> new IllegalArgumentException("Hotel room not found with ID: " + hotelRoomId));

        List<Integer> roomFeatureIds = hotelRoomFeatureRepository.findByHotelRoomId(hotelRoomId)
                .stream()
                .map(HotelRoomFeatureEntity::getRoomFeaturesId)
                .toList();
        List<String> roomFeatures = roomFeatureIds.stream()
                .map(featureId -> featureRepository.findById(featureId)
                        .orElseThrow(() -> new IllegalArgumentException("Feature not found with ID: " + featureId))
                        .getFeatureDescription())
                .toList();
        List<String> images = hotelRoomImageRepository.findByRoomId(hotelRoomId)
                .stream()
                .map(HotelRoomImageEntity::getImgUrl)
                .toList();

        return new HotelRoomDTO(
                room.getHotelRoomId(),
                room.getRoomName(),
                room.getRoomPrice(),
                roomFeatureIds,
                roomFeatures.isEmpty() ? null : roomFeatures,
                room.getSize(),
                room.getMaxPersons(),
                room.getBedType(),
                room.getView(),
                images.isEmpty() ? null : images
        );
    }
    public List<String> suggestHotelNames(String keyword) {
        return hotelRepository.findByHotelNameContainingIgnoreCase(keyword)
            .stream()
            .map(HotelEntity::getHotelName)
            .filter(Objects::nonNull)
            .distinct()
            .limit(10) // Giới hạn 10 gợi ý
            .collect(Collectors.toList());
    }
}
