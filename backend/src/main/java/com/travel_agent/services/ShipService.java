package com.travel_agent.services;

import com.travel_agent.dto.*;
import com.travel_agent.dto.ship.ShipDTO;
import com.travel_agent.dto.ship.ShipLongDescriptionDTO;
import com.travel_agent.dto.ship.ShipRoomDTO;
import com.travel_agent.mappers.ShipMapper;
import com.travel_agent.models.entity.ship.*;
import com.travel_agent.repositories.*;
import com.travel_agent.repositories.ship.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.travel_agent.models.entity.CompanyEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.travel_agent.models.entity.FeatureEntity;

@Service
@RequiredArgsConstructor
public class ShipService {
    private final ShipRepository shipRepository;
    private final ShipMapper shipMapper;
    private final CompanyRepository companyRepository;
    private final FeatureRepository featureRepository;
    private final ShipFeatureRepository shipFeatureRepository;
    private final ShipShortDescriptionRepository shipShortDescriptionRepository;
    private final ShipLongDescriptionRepository shipLongDescriptionRepository;
    private final ShipRoomRepository shipRoomRepository;
    private final ShipRoomFeatureRepository shipRoomFeatureRepository;
    private final ShipImageRepository shipImageRepository;
    private final ShipRoomImageRepository shipRoomImageRepository;

    public ShipDTO getShipById(Integer shipId) {
        ShipEntity shipEntity = shipRepository.findById(shipId)
                .orElseThrow(() -> new IllegalArgumentException("Ship not found with ID: " + shipId));
        return shipMapper.shipToShipDTO(shipEntity);
    }

    public ResultPaginationDTO getAllShips(Pageable pageable) {
        Page<ShipEntity> pageShip = shipRepository.findAll(pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        Meta mt = new Meta();

        mt.setPage(pageShip.getNumber());
        mt.setPageSize(pageShip.getSize());
        mt.setPages(pageShip.getTotalPages());
        mt.setTotal(pageShip.getTotalElements());

        rs.setMeta(mt);
        rs.setResult(pageShip.getContent());
        return rs;
    }

    // Search ship
    public ResultPaginationDTO searchShipsByNamePriceAndTrip(
            String name,
            Integer minPrice,
            Integer maxPrice,
            String trip,
            String features,
            Pageable pageable) {
        
        Page<ShipEntity> shipPage = shipRepository.findByShipNamePriceAndAddress(
                name, minPrice, maxPrice, trip, pageable);

        List<ShipDTO> shipDtos = shipPage.getContent().stream().map(ship -> {
            ShipDTO shipDto = shipMapper.shipToShipDTO(ship);

            // Fetch features
            List<ShipFeatureEntity> shipFeatures = shipFeatureRepository.findByShipId(ship.getShipId());
            List<Integer> featureIds = shipFeatures.stream()
                    .map(ShipFeatureEntity::getFeatureId)
                    .toList();
            List<String> featureDescriptions = featureIds.stream()
                    .map(featureId -> featureRepository.findById(featureId)
                            .orElseThrow(() -> new IllegalArgumentException("Feature not found with ID: " + featureId))
                            .getFeatureDescription())
                    .toList();

            shipDto.setFeatureIds(featureIds.isEmpty() ? null : featureIds);
            shipDto.setFeatures(featureDescriptions.isEmpty() ? null : featureDescriptions);

            return shipDto;
        }).toList();

        // Lọc theo features nếu có
        if (features != null && !features.isEmpty()) {
            List<String> requiredFeatures = List.of(features.split(","));
            System.out.println("Required features: " + requiredFeatures);
            
            // Lấy danh sách featureId từ tên feature
            List<Integer> requiredFeatureIds = requiredFeatures.stream()
                .map(featureName -> {
                    List<FeatureEntity> featureList = featureRepository.findByFeatureDescription(featureName);
                    return featureList.isEmpty() ? null : featureList.get(0).getFeatureId();
                })
                .filter(Objects::nonNull)
                .toList();
            
            System.out.println("Required feature IDs: " + requiredFeatureIds);
            
            shipDtos = shipDtos.stream()
                    .filter(ship -> {
                        System.out.println("Checking ship: " + ship.getShipName());
                        System.out.println("Ship feature IDs: " + ship.getFeatureIds());
                        boolean matches = ship.getFeatureIds() != null && 
                                requiredFeatureIds.stream()
                                        .allMatch(reqId -> ship.getFeatureIds().contains(reqId));
                        System.out.println("Matches: " + matches);
                        return matches;
                    })
                    .toList();
            
            System.out.println("Filtered ships count: " + shipDtos.size());
        }

        ResultPaginationDTO result = new ResultPaginationDTO();
        result.setResult(shipDtos);
        Meta meta = new Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPages(shipPage.getTotalPages());
        meta.setTotal(shipDtos.size());
        result.setMeta(meta);

        return result;
    }

    public ShipDTO getShipDetails(Integer shipId) {
        ShipEntity shipEntity = shipRepository.findById(shipId)
                .orElseThrow(() -> new IllegalArgumentException("Ship not found with ID: " + shipId));
        ShipDTO shipDto = shipMapper.shipToShipDTO(shipEntity);

        // Fetch features
        List<ShipFeatureEntity> shipFeatures = shipFeatureRepository.findByShipId(shipId);
        List<Integer> featureIds = shipFeatures.stream()
                .map(ShipFeatureEntity::getFeatureId)
                .toList();
        List<String> featureDescriptions = featureIds.stream()
                .map(featureId -> featureRepository.findById(featureId)
                        .orElseThrow(() -> new IllegalArgumentException("Feature not found with ID: " + featureId))
                        .getFeatureDescription())
                .toList();

        shipDto.setFeatureIds(featureIds.isEmpty() ? null : featureIds);
        shipDto.setFeatures(featureDescriptions.isEmpty() ? null : featureDescriptions);

        // Fetch short descriptions
        List<String> shortDescriptions = shipShortDescriptionRepository.findByShipId(shipId)
                .stream()
                .map(ShipShortDescriptionEntity::getDescription)
                .toList();
        shipDto.setShortDescriptions(shortDescriptions.isEmpty() ? null : shortDescriptions);

        // Fetch long descriptions
        List<ShipLongDescriptionEntity> longDescriptions = shipLongDescriptionRepository.findByShipId(shipId);
        List<ShipLongDescriptionDTO> longDescriptionDtos = longDescriptions.stream()
                .map(desc -> new ShipLongDescriptionDTO(desc.getBlockId(), desc.getType(), desc.getData()))
                .toList();
        shipDto.setLongDescriptions(longDescriptionDtos.isEmpty() ? null : longDescriptionDtos);

        // Fetch rooms
        List<ShipRoomEntity> shipRoomEntities = shipRoomRepository.findByShip_ShipId(shipId);

        List<String> images = shipImageRepository.findByShipId(shipId)
                .stream()
                .map(ShipImageEntity::getImgUrl)
                .toList();
        shipDto.setImages(images.isEmpty() ? null : images);

        List<ShipRoomDTO> roomDtos = shipRoomEntities.stream()
                .map(room -> getShipRoomDetails(room.getShipRoomId()))
                .toList();
        shipDto.setRooms(roomDtos.isEmpty() ? null : roomDtos);

        return shipDto;
    }

    public List<ShipRoomDTO> getAllRoomsByShipId(Integer shipId) {
        // Check if the ship exists
        shipRepository.findById(shipId)
                .orElseThrow(() -> new IllegalArgumentException("Ship not found with ID: " + shipId));

        // Fetch the list of rooms
        List<ShipRoomEntity> shipRoomEntities = shipRoomRepository.findByShip_ShipId(shipId);

        // Map entities to DTOs
        return shipRoomEntities.stream().map(room -> {
            List<Integer> roomFeatureIds = shipRoomFeatureRepository.findByShipRoomId(room.getShipRoomId())
                    .stream()
                    .map(ShipRoomFeatureEntity::getRoomFeaturesId)
                    .toList();

            List<String> roomFeatures = roomFeatureIds.stream()
                    .map(featureId -> featureRepository.findById(featureId)
                            .orElseThrow(() -> new IllegalArgumentException("Feature not found with ID: " + featureId))
                            .getFeatureDescription())
                    .toList();

            List<String> images = shipRoomImageRepository.findByRoomId(room.getShipRoomId())
                    .stream()
                    .map(ShipRoomImageEntity::getImgUrl)
                    .toList();

            return new ShipRoomDTO(
                    room.getShipRoomId(),
                    room.getRoomName(),
                    room.getRoomPrice(),
                    roomFeatureIds,
                    roomFeatures.isEmpty() ? null : roomFeatures,
                    room.getSize(),
                    room.getMaxPersons(),
                    images.isEmpty() ? null : images
            );
        }).toList();
    }

    private void populateShipEntity(ShipEntity shipEntity, ShipDTO shipDto) {
        shipEntity.setShipName(shipDto.getShipName());
        shipEntity.setLaunch(shipDto.getLaunch());
        shipEntity.setCabin(shipDto.getCabin());
        shipEntity.setShell(shipDto.getShell());
        shipEntity.setTrip(shipDto.getTrip());
        shipEntity.setShipPrice(shipDto.getShipPrice());
        shipEntity.setAddress(shipDto.getAddress());
        shipEntity.setMapLink(shipDto.getMapLink());
        shipEntity.setThumbnail(shipDto.getThumbnail());

        if (shipDto.getCompanyId() != null) {
            CompanyEntity company = companyRepository.findById(shipDto.getCompanyId())
                    .orElseThrow(() -> new IllegalArgumentException("Company not found with ID: " + shipDto.getCompanyId()));
            shipEntity.setCompany(company);
            shipEntity.setCompanyName(company.getCompanyName());
        }

        shipEntity = shipRepository.save(shipEntity);

        if (shipDto.getFeatureIds() != null) {
            for (Integer featureId : shipDto.getFeatureIds()) {
                featureRepository.findById(featureId)
                        .orElseThrow(() -> new IllegalArgumentException("Feature not found with ID: " + featureId));

                ShipFeatureEntity shipFeature = new ShipFeatureEntity();
                shipFeature.setShipId(shipEntity.getShipId());
                shipFeature.setFeatureId(featureId);
                shipFeatureRepository.save(shipFeature);
            }
        }
    }

    // Add ship
    public ShipDTO addShip(ShipDTO shipDto) {
        ShipEntity shipEntity = new ShipEntity();
        populateShipEntity(shipEntity, shipDto);
        shipEntity = shipRepository.save(shipEntity);

        // Save short descriptions
        if (shipDto.getShortDescriptions() != null) {
            int blockId = 1;
            for (String description : shipDto.getShortDescriptions()) {
                ShipShortDescriptionEntity shortDescription = new ShipShortDescriptionEntity();
                shortDescription.setShipId(shipEntity.getShipId());
                shortDescription.setBlockId(blockId++);
                shortDescription.setDescription(description);
                shipShortDescriptionRepository.save(shortDescription);
            }
        }

        // Save images
        if (shipDto.getImages() != null) {
            for (String imgUrl : shipDto.getImages()) {
                ShipImageEntity shipImage = new ShipImageEntity();
                shipImage.setShipId(shipEntity.getShipId());
                shipImage.setImgUrl(imgUrl);
                shipImageRepository.save(shipImage);
            }
        }

        // Save long descriptions
        if (shipDto.getLongDescriptions() != null) {
            for (ShipLongDescriptionDTO longDescriptionDto : shipDto.getLongDescriptions()) {
                ShipLongDescriptionEntity longDescription = new ShipLongDescriptionEntity();
                longDescription.setShipId(shipEntity.getShipId());
                longDescription.setBlockId(longDescriptionDto.getBlockId());
                longDescription.setType(longDescriptionDto.getType());
                longDescription.setData(longDescriptionDto.getData());
                shipLongDescriptionRepository.save(longDescription);
            }
        }

        // Map the saved ship entity to DTO
        ShipDTO savedShipDto = shipMapper.shipToShipDTO(shipEntity);

        // Fetch and set featureIds and features
        List<ShipFeatureEntity> shipFeatures = shipFeatureRepository.findByShipId(shipEntity.getShipId());
        List<Integer> featureIds = shipFeatures.stream()
                .map(ShipFeatureEntity::getFeatureId)
                .toList();
        List<String> features = featureIds.stream()
                .map(featureId -> featureRepository.findById(featureId)
                        .orElseThrow(() -> new IllegalArgumentException("Feature not found with ID: " + featureId))
                        .getFeatureDescription())
                .toList();

        savedShipDto.setFeatureIds(featureIds.isEmpty() ? null : featureIds);
        savedShipDto.setFeatures(features.isEmpty() ? null : features);

        savedShipDto.setShortDescriptions(shipDto.getShortDescriptions());
        savedShipDto.setLongDescriptions(shipDto.getLongDescriptions());
        savedShipDto.setImages(shipDto.getImages());

        return savedShipDto;
    }

    public ShipDTO updateShipGeneralInfo(Integer shipId, CompanyUpdateDTO companyUpdateDTO) {
        ShipEntity shipEntity = shipRepository.findById(shipId)
                .orElseThrow(() -> new IllegalArgumentException("Ship not found with ID: " + shipId));

        if (companyUpdateDTO.getAccommodationName() != null) {
            shipEntity.setShipName(companyUpdateDTO.getAccommodationName());
        }
        if (companyUpdateDTO.getAddress() != null) {
            shipEntity.setAddress(companyUpdateDTO.getAddress());
        }
        if (companyUpdateDTO.getMapLink() != null) {
            shipEntity.setMapLink(companyUpdateDTO.getMapLink());
        }
        if (companyUpdateDTO.getCompanyName() != null) {
            shipEntity.setCompanyName(companyUpdateDTO.getCompanyName());
        }
        shipEntity = shipRepository.save(shipEntity);
        
        return shipMapper.shipToShipDTO(shipEntity);

    }

    // Update ship
    public ShipDTO updateShip(Integer shipId, ShipDTO shipDto) {
        ShipEntity shipEntity = shipRepository.findById(shipId)
                .orElseThrow(() -> new IllegalArgumentException("Ship not found with ID: " + shipId));

        // Update ship entity fields
        populateShipEntity(shipEntity, shipDto);

        // Clear existing features
        List<ShipFeatureEntity> existingFeatures = shipFeatureRepository.findByShipId(shipId);
        shipFeatureRepository.deleteAll(existingFeatures);

        // Add new features
        if (shipDto.getFeatureIds() != null) {
            for (Integer featureId : shipDto.getFeatureIds()) {
                featureRepository.findById(featureId)
                        .orElseThrow(() -> new IllegalArgumentException("Feature not found with ID: " + featureId));

                ShipFeatureEntity shipFeature = new ShipFeatureEntity();
                shipFeature.setShipId(shipId);
                shipFeature.setFeatureId(featureId);
                shipFeatureRepository.save(shipFeature);
            }
        }

        // Clear existing short descriptions
        List<ShipShortDescriptionEntity> existingDescriptions = shipShortDescriptionRepository.findByShipId(shipId);
        shipShortDescriptionRepository.deleteAll(existingDescriptions);

        // Add new short descriptions
        if (shipDto.getShortDescriptions() != null) {
            int blockId = 1;
            for (String description : shipDto.getShortDescriptions()) {
                ShipShortDescriptionEntity shortDescription = new ShipShortDescriptionEntity();
                shortDescription.setShipId(shipId);
                shortDescription.setBlockId(blockId++);
                shortDescription.setDescription(description);
                shipShortDescriptionRepository.save(shortDescription);
            }
        }

        // Clear existing long descriptions
        List<ShipLongDescriptionEntity> existingLongDescriptions = shipLongDescriptionRepository.findByShipId(shipId);
        shipLongDescriptionRepository.deleteAll(existingLongDescriptions);

        // Add new long descriptions
        if (shipDto.getLongDescriptions() != null) {
            for (ShipLongDescriptionDTO longDescriptionDto : shipDto.getLongDescriptions()) {
                ShipLongDescriptionEntity longDescription = new ShipLongDescriptionEntity();
                longDescription.setShipId(shipId);
                longDescription.setBlockId(longDescriptionDto.getBlockId());
                longDescription.setType(longDescriptionDto.getType());
                longDescription.setData(longDescriptionDto.getData());
                shipLongDescriptionRepository.save(longDescription);
            }
        }

        // Clear existing images
        shipImageRepository.deleteAll(shipImageRepository.findByShipId(shipId));

        // Add new images
        if (shipDto.getImages() != null) {
            for (String imgUrl : shipDto.getImages()) {
                ShipImageEntity shipImage = new ShipImageEntity();
                shipImage.setShipId(shipId);
                shipImage.setImgUrl(imgUrl);
                shipImageRepository.save(shipImage);
            }
        }

        // Save updated ship entity
        shipEntity = shipRepository.save(shipEntity);

        // Map updated ship entity to DTO
        ShipDTO updatedShipDto = shipMapper.shipToShipDTO(shipEntity);

        // Fetch and set updated featureIds and features
        List<ShipFeatureEntity> updatedFeatures = shipFeatureRepository.findByShipId(shipId);
        List<Integer> featureIds = updatedFeatures.stream()
                .map(ShipFeatureEntity::getFeatureId)
                .toList();
        List<String> features = featureIds.stream()
                .map(featureId -> featureRepository.findById(featureId)
                        .orElseThrow(() -> new IllegalArgumentException("Feature not found with ID: " + featureId))
                        .getFeatureDescription())
                .toList();

        // Fetch and set updated short descriptions
        List<String> shortDescriptions = shipShortDescriptionRepository.findByShipId(shipId)
                .stream()
                .map(ShipShortDescriptionEntity::getDescription)
                .toList();
        updatedShipDto.setShortDescriptions(shortDescriptions.isEmpty() ? null : shortDescriptions);

        // Fetch and set updated long descriptions
        List<ShipLongDescriptionEntity> longDescriptions = shipLongDescriptionRepository.findByShipId(shipId);
        List<ShipLongDescriptionDTO> longDescriptionDtos = longDescriptions.stream()
                .map(desc -> new ShipLongDescriptionDTO(desc.getBlockId(), desc.getType(), desc.getData()))
                .toList();
        updatedShipDto.setLongDescriptions(longDescriptionDtos.isEmpty() ? null : longDescriptionDtos);

        updatedShipDto.setFeatureIds(featureIds.isEmpty() ? null : featureIds);
        updatedShipDto.setFeatures(features.isEmpty() ? null : features);

        return updatedShipDto;
    }

    // Delete ship
    @Transactional
    public void deleteShips(List<Integer> shipIds) {
        for (Integer shipId : shipIds) {
            ShipEntity shipEntity = shipRepository.findById(shipId)
                    .orElseThrow(() -> new IllegalArgumentException("Ship not found with ID: " + shipId));

            // Delete associated rooms and their features
            List<ShipRoomEntity> shipRoomEntities = shipRoomRepository.findByShip_ShipId(shipId);
            for (ShipRoomEntity room : shipRoomEntities) {
                shipRoomFeatureRepository.deleteByShipRoomId(room.getShipRoomId());
            }
            shipRoomRepository.deleteAll(shipRoomEntities);

            // Delete associated features
            List<ShipFeatureEntity> shipFeatures = shipFeatureRepository.findByShipId(shipId);
            shipFeatureRepository.deleteAll(shipFeatures);

            // Delete associated short descriptions
            List<ShipShortDescriptionEntity> shipShortDescriptions = shipShortDescriptionRepository.findByShipId(shipId);
            shipShortDescriptionRepository.deleteAll(shipShortDescriptions);

            // Delete associated long descriptions
            List<ShipLongDescriptionEntity> shipLongDescriptions = shipLongDescriptionRepository.findByShipId(shipId);
            shipLongDescriptionRepository.deleteAll(shipLongDescriptions);

            // Delete associated images
            List<ShipImageEntity> shipImages = shipImageRepository.findByShipId(shipId);
            shipImageRepository.deleteAll(shipImages);

            // Finally, delete the ship
            shipRepository.delete(shipEntity);
        }
    }

    // View room
    public ShipRoomDTO getShipRoom(Integer shipId, Integer roomId) {
        // Fetch the room by roomId
        ShipRoomEntity shipRoomEntity = shipRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found with ID: " + roomId));

        // Validate that the room belongs to the specified ship
        if (!shipRoomEntity.getShip().getShipId().equals(shipId)) {
            throw new IllegalArgumentException("Room does not belong to the specified ship");
        }

        // Fetch room features
        List<Integer> roomFeatureIds = shipRoomFeatureRepository.findByShipRoomId(roomId)
                .stream()
                .map(ShipRoomFeatureEntity::getRoomFeaturesId)
                .toList();
        List<String> roomFeatures = roomFeatureIds.stream()
                .map(featureId -> featureRepository.findById(featureId)
                        .orElseThrow(() -> new IllegalArgumentException("Feature not found with ID: " + featureId))
                        .getFeatureDescription())
                .toList();

        List<String> images = shipRoomImageRepository.findByRoomId(roomId)
                .stream()
                .map(ShipRoomImageEntity::getImgUrl)
                .toList();

        // Map the entity to DTO
        return new ShipRoomDTO(
                shipRoomEntity.getShipRoomId(),
                shipRoomEntity.getRoomName(),
                shipRoomEntity.getRoomPrice(),
                roomFeatureIds,
                roomFeatures.isEmpty() ? null : roomFeatures,
                shipRoomEntity.getSize(),
                shipRoomEntity.getMaxPersons(),
                images.isEmpty() ? null : images
        );
    }

    public ShipRoomDTO addShipRoom(Integer shipId, ShipRoomDTO roomDto) {
        ShipEntity shipEntity = shipRepository.findById(shipId)
                .orElseThrow(() -> new IllegalArgumentException("Ship not found with ID: " + shipId));

        ShipRoomEntity shipRoomEntity = new ShipRoomEntity();
        shipRoomEntity.setShip(shipEntity);
        shipRoomEntity.setRoomName(roomDto.getRoomName());
        shipRoomEntity.setRoomPrice(roomDto.getRoomPrice());
        shipRoomEntity.setSize(roomDto.getSize());
        shipRoomEntity.setMaxPersons(roomDto.getMaxPersons());

        shipRoomEntity = shipRoomRepository.save(shipRoomEntity);

        for (Integer featureId : roomDto.getRoomFeatureIds()) {
            featureRepository.findById(featureId)
                    .orElseThrow(() -> new IllegalArgumentException("Feature not found with ID: " + featureId));

            ShipRoomFeatureEntity roomFeature = new ShipRoomFeatureEntity();
            roomFeature.setShipRoomId(shipRoomEntity.getShipRoomId());
            roomFeature.setRoomFeaturesId(featureId);
            shipRoomFeatureRepository.save(roomFeature);
        }

        List<String> roomFeatures = roomDto.getRoomFeatureIds().stream()
                .map(featureId -> featureRepository.findById(featureId)
                        .orElseThrow(() -> new IllegalArgumentException("Feature not found with ID: " + featureId))
                        .getFeatureDescription())
                .toList();

        if (roomDto.getImages() != null) {
            for (String imgUrl : roomDto.getImages()) {
                ShipRoomImageEntity roomImage = new ShipRoomImageEntity();
                roomImage.setRoomId(shipRoomEntity.getShipRoomId());
                roomImage.setImgUrl(imgUrl);
                shipRoomImageRepository.save(roomImage);
            }
        }

        return new ShipRoomDTO(
                shipRoomEntity.getShipRoomId(),
                shipRoomEntity.getRoomName(),
                shipRoomEntity.getRoomPrice(),
                roomDto.getRoomFeatureIds(),
                roomFeatures,
                shipRoomEntity.getSize(),
                shipRoomEntity.getMaxPersons(),
                roomDto.getImages()
        );
    }

    // Update room
    @Transactional
    public ShipRoomDTO updateShipRoom(Integer shipId, Integer roomId, ShipRoomDTO roomDto) {
        shipRepository.findById(shipId)
                .orElseThrow(() -> new IllegalArgumentException("Ship not found with ID: " + shipId));

        ShipRoomEntity shipRoomEntity = shipRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found with ID: " + roomId));

        if (!shipRoomEntity.getShip().getShipId().equals(shipId)) {
            throw new IllegalArgumentException("Room does not belong to the specified ship");
        }

        // Update room details
        shipRoomEntity.setRoomName(roomDto.getRoomName());
        shipRoomEntity.setRoomPrice(roomDto.getRoomPrice());
        shipRoomEntity.setSize(roomDto.getSize());
        shipRoomEntity.setMaxPersons(roomDto.getMaxPersons());

        shipRoomEntity = shipRoomRepository.save(shipRoomEntity);

        // Update room features
        List<ShipRoomFeatureEntity> existingFeatures = shipRoomFeatureRepository.findByShipRoomId(roomId);
        shipRoomFeatureRepository.deleteAll(existingFeatures);

        for (Integer featureId : roomDto.getRoomFeatureIds()) {
            featureRepository.findById(featureId)
                    .orElseThrow(() -> new IllegalArgumentException("Feature not found with ID: " + featureId));

            ShipRoomFeatureEntity roomFeature = new ShipRoomFeatureEntity();
            roomFeature.setShipRoomId(shipRoomEntity.getShipRoomId());
            roomFeature.setRoomFeaturesId(featureId);
            shipRoomFeatureRepository.save(roomFeature);
        }

        shipRoomImageRepository.deleteByRoomId(roomId);

        if (roomDto.getImages() != null) {
            for (String imgUrl : roomDto.getImages()) {
                ShipRoomImageEntity roomImage = new ShipRoomImageEntity();
                roomImage.setRoomId(shipRoomEntity.getShipRoomId());
                roomImage.setImgUrl(imgUrl);
                shipRoomImageRepository.save(roomImage);
            }
        }

        return new ShipRoomDTO(
                shipRoomEntity.getShipRoomId(),
                shipRoomEntity.getRoomName(),
                shipRoomEntity.getRoomPrice(),
                roomDto.getRoomFeatureIds(),
                null, // Room features descriptions can be fetched separately if needed
                shipRoomEntity.getSize(),
                shipRoomEntity.getMaxPersons(),
                roomDto.getImages()
        );
    }

    // Delete room
    @Transactional
    public void deleteShipRooms(Integer shipId, List<Integer> roomIds) {
        // Check if the ship exists
        shipRepository.findById(shipId)
                .orElseThrow(() -> new IllegalArgumentException("Ship not found with ID: " + shipId));

        // Fetch the rooms to delete
        List<ShipRoomEntity> roomsToDelete = shipRoomRepository.findAllById(roomIds);

        // Validate that all rooms belong to the specified ship
        for (ShipRoomEntity room : roomsToDelete) {
            if (!room.getShip().getShipId().equals(shipId)) {
                throw new IllegalArgumentException("Room with ID " + room.getShipRoomId() + " does not belong to the specified ship");
            }
        }

        // Delete related records in ship_room_features for each room
        for (ShipRoomEntity room : roomsToDelete) {
            shipRoomFeatureRepository.deleteByShipRoomId(room.getShipRoomId());
        }

        // Delete the ship rooms
        shipRoomRepository.deleteAll(roomsToDelete);
    }

    public ShipRoomDTO getShipRoomDetails(Integer shipRoomId) {
        ShipRoomEntity room = shipRoomRepository.findById(shipRoomId)
                .orElseThrow(() -> new IllegalArgumentException("Ship room not found with ID: " + shipRoomId));

        List<Integer> roomFeatureIds = shipRoomFeatureRepository.findByShipRoomId(shipRoomId)
                .stream()
                .map(ShipRoomFeatureEntity::getRoomFeaturesId)
                .toList();
        List<String> roomFeatures = roomFeatureIds.stream()
                .map(featureId -> featureRepository.findById(featureId)
                        .orElseThrow(() -> new IllegalArgumentException("Feature not found with ID: " + featureId))
                        .getFeatureDescription())
                .toList();
        List<String> images = shipRoomImageRepository.findByRoomId(shipRoomId)
                .stream()
                .map(ShipRoomImageEntity::getImgUrl)
                .toList();

        return new ShipRoomDTO(
                room.getShipRoomId(),
                room.getRoomName(),
                room.getRoomPrice(),
                roomFeatureIds,
                roomFeatures.isEmpty() ? null : roomFeatures,
                room.getSize(),
                room.getMaxPersons(),
                images.isEmpty() ? null : images
        );
    }

    public List<String> suggestShipNames(String keyword) {
        return shipRepository.findByShipNameContainingIgnoreCase(keyword)
            .stream()
            .map(ShipEntity::getShipName)
            .filter(Objects::nonNull)
            .distinct()
            .limit(10) // Giới hạn 10 gợi ý
            .collect(Collectors.toList());
    }

    public List<String> getAllFeatures() {
        return featureRepository.findAll().stream()
                .map(feature -> feature.getFeatureDescription())
                .distinct()
                .collect(Collectors.toList());
    }

    private String normalizeString(String input) {
        if (input == null) return "";
        return java.text.Normalizer.normalize(input, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase()
                .trim();
    }
}