package com.booknplay.turf_service.service.impl;

import com.booknplay.turf_service.client.UserClient;
import com.booknplay.turf_service.dto.TurfRequestDto;
import com.booknplay.turf_service.dto.TurfResponseDto;
import com.booknplay.turf_service.dto.UserDto;
import com.booknplay.turf_service.entity.Turf;
import com.booknplay.turf_service.entity.TurfStatus;
import com.booknplay.turf_service.repository.TurfRepository;
import com.booknplay.turf_service.service.CustomException;
import com.booknplay.turf_service.service.TurfService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TurfServiceImpl implements TurfService {

    private final TurfRepository turfRepository;
    private final UserClient userClient;

    @Override
    public TurfResponseDto addTurf(TurfRequestDto dto, String ownerEmail) {
        log.info("Adding turf for user");
        UserDto owner = userClient.getUserByEmail(ownerEmail);
        log.debug("Fetched owner from user-service: {}", owner);
        if (owner == null || owner.getRoles().stream().noneMatch(role-> "ROLE_OWNER".equalsIgnoreCase(role.getName()))) {
            log.warn("Unauthorized attempt to add turf by user: {}",ownerEmail);
            throw new CustomException("Only owners can add turf.");
        }

        Turf turf = Turf.builder()
                .name(dto.getName())
                .location(dto.getLocation())
                .sportType(dto.getSportType())
                .isIndoor(dto.getIsIndoor())
                .availableFrom(dto.getAvailableFrom())
                .availableTo(dto.getAvailableTo())
                .status(TurfStatus.PENDING)
                .pricePerHour(dto.getPricePerHour())
                .ownerId(owner.getId())
                .build();
        return map(turfRepository.save(turf));
    }

    @Override
    public TurfResponseDto updateTurf(Long turfId, TurfRequestDto dto, Long ownerId) {
        log.info("Updating turf for user");
        Turf turf = turfRepository.findById(turfId).orElseThrow(() -> new CustomException("Turf not found!"));
        if (!turf.getOwnerId().equals(ownerId)) throw new CustomException("Unauthorized");

        UserDto owner = userClient.getUserById(ownerId);
        if (owner == null || owner.getRoles().stream().noneMatch(role-> "ROLE_OWNER".equalsIgnoreCase(role.getName()))) {
            throw new CustomException("Only owners can add turf.");
        }

        turf.setName(dto.getName());
        turf.setLocation(dto.getLocation());
        turf.setSportType(dto.getSportType());
        turf.setIsIndoor(dto.getIsIndoor());
        turf.setAvailableFrom(dto.getAvailableFrom());
        turf.setAvailableTo(dto.getAvailableTo());
        turf.setPricePerHour(dto.getPricePerHour());
        return map(turfRepository.save(turf));
    }

    @Override
    public List<TurfResponseDto> getAllTurfs() {
        log.info("get all turfs");
        return turfRepository.findAll().stream().map(this::map).collect(Collectors.toList());
    }

    @Override
    public TurfResponseDto updateStatus(Long id, TurfStatus status) {
        log.info("Update turf");
        Turf turf = turfRepository.findById(id).orElseThrow(() -> new CustomException("Turf not found!!"));
        turf.setStatus(status);
        return map(turfRepository.save(turf));
    }

    @Override
    public void deleteTurf(Long turfId, Long userId, List<String> roles) {
        Turf turf = turfRepository.findById(turfId).orElseThrow(() -> new CustomException("turf not found"));
        UserDto owner = userClient.getUserById(userId);
        boolean isOwner = turf.getOwnerId().equals(userId);
        boolean isAdmin = owner.getRoles().stream().anyMatch(role-> "ROLE_ADMIN".equalsIgnoreCase(role.getName()));

        if (!isAdmin && !isOwner) throw new CustomException("You are not authorized to delete this turf");

        turfRepository.delete(turf);
    }

    @Override
    public TurfResponseDto getTurfById(Long turfId) {
        return turfRepository.findById(turfId).map(this::map).orElseThrow(()->new CustomException("Turf not found with the id: "+turfId));
    }

    private TurfResponseDto map(Turf t) {
        return TurfResponseDto.builder()
                .id(t.getId())
                .ownerId(t.getOwnerId())
                .name(t.getName())
                .location(t.getLocation())
                .sportType(t.getSportType())
                .pricePerHour(t.getPricePerHour())
                .isIndoor(t.getIsIndoor())
                .availableFrom(t.getAvailableFrom())
                .availableTo(t.getAvailableTo())
                .status(t.getStatus())
                .build();
    }
}