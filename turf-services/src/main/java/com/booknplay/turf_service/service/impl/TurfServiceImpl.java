package com.booknplay.turf_service.service.impl;

import com.booknplay.turf_service.client.UserClient;
import com.booknplay.turf_service.dto.TurfSportOptionDto;
import com.booknplay.turf_service.dto.*;
import com.booknplay.turf_service.entity.Address;
import com.booknplay.turf_service.entity.Turf;
import com.booknplay.turf_service.entity.TurfSportOption;
import com.booknplay.turf_service.exception.CustomException;
import com.booknplay.turf_service.repository.TurfRepository;

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
        log.info("Adding turf for owner email={}", ownerEmail);
        UserDto owner = userClient.getUserByEmail(ownerEmail);
        validateOwnerRole(owner, ownerEmail);

        Turf turf = toEntity(dto);
        turf.setOwnerId(owner.getId());
        return toResponse(turfRepository.save(turf));
    }

    @Override
    public TurfResponseDto updateTurfById(Long turfId, TurfRequestDto dto, String ownerEmail) {
        UserDto owner = userClient.getUserByEmail(ownerEmail);
        validateOwnerRole(owner, ownerEmail);

        Turf turf = turfRepository.findByIdAndOwnerId(turfId, owner.getId())
                .orElseThrow(() -> new CustomException("Turf not found or not owned by you"));

        applyUpdates(turf, dto);
        Turf saved = turfRepository.save(turf);
        return toResponse(saved);
    }

    @Override
    public void deleteTurfById(Long turfId, String ownerEmail) {
        UserDto owner = userClient.getUserByEmail(ownerEmail);
        validateOwnerRole(owner, ownerEmail);

        Turf turf = turfRepository.findByIdAndOwnerId(turfId, owner.getId())
                .orElseThrow(() -> new CustomException("Turf not found or not owned by you"));

        turfRepository.delete(turf);
    }

    @Override
    public TurfResponseDto getTurfById(Long turfId) {
        Turf turf = turfRepository.findById(turfId)
                .orElseThrow(() -> new CustomException("Turf not found with the id: " + turfId));
        return toResponse(turf);
    }

    @Override
    public List<TurfResponseDto> getAllTurfs() {
        return turfRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public List<TurfResponseDto> getMyTurfs(String ownerEmailId) { // CHANGE
        log.info("Listing turfs for owner email={}", ownerEmailId);
        UserDto owner = userClient.getUserByEmail(ownerEmailId);
        validateOwnerRole(owner, ownerEmailId);

        return turfRepository.findByOwnerId(owner.getId())
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private void validateOwnerRole(UserDto owner, String ownerEmail) {
        if (owner == null || owner.getRoles() == null ||
                owner.getRoles().stream().noneMatch(role -> "ROLE_OWNER".equalsIgnoreCase(role.getName()))) {
            log.warn("Unauthorized: user={} is not an owner", ownerEmail);
            throw new CustomException("Only owners can perform this action.");
        }
    }

    private Turf toEntity(TurfRequestDto dto) {
        Turf turf = Turf.builder()
                .name(dto.getName())
                .pricePerHour(dto.getPricePerHour())
                .availableFrom(dto.getAvailableFrom())
                .availableTo(dto.getAvailableTo())
                .build();

        turf.setAddress(Address.builder()
                .street(dto.getAddress().getStreet())
                .city(dto.getAddress().getCity())
                .build());

        List<TurfSportOption> options = dto.getSportOptions().stream()
                .map(optDto -> TurfSportOption.builder()
                        .sportType(optDto.getSportType())
                        .isIndoor(optDto.getIsIndoor())
                        .turf(turf)
                        .build())
                .collect(java.util.stream.Collectors.toList());

        turf.setSportOptions(options);
        return turf;
    }

    private void applyUpdates(Turf turf, TurfRequestDto dto) {
        turf.setName(dto.getName());
        turf.setPricePerHour(dto.getPricePerHour());
        turf.setAvailableFrom(dto.getAvailableFrom());
        turf.setAvailableTo(dto.getAvailableTo());
        if (turf.getAddress() == null) {
            turf.setAddress(new Address());
        }

        turf.getAddress().setStreet(dto.getAddress().getStreet());
        turf.getAddress().setCity(dto.getAddress().getCity());

        List<TurfSportOption> newOptions = dto.getSportOptions().stream()
                .map(opt -> TurfSportOption.builder()
                        .sportType(opt.getSportType())
                        .isIndoor(opt.getIsIndoor())
                        .turf(turf)
                        .build())
                .collect(Collectors.toList());
        turf.setSportOptions(newOptions);
    }

    private TurfResponseDto
    toResponse(Turf turf) {
        AddressDto addr = AddressDto.builder()
                .street(turf.getAddress() != null ? turf.getAddress().getStreet() : "")
                .city(turf.getAddress() != null ? turf.getAddress().getCity() : "")
                .build();

        List<TurfSportOptionDto> optionDtos = turf.getSportOptions() != null
                ? turf.getSportOptions().stream()
                .map(opt -> TurfSportOptionDto.builder()
                        .sportType(opt.getSportType())
                        .isIndoor(opt.getIsIndoor())
                        .build())
                .collect(java.util.stream.Collectors.toList())
                : java.util.List.of();

        return TurfResponseDto.builder()
                .id(turf.getId())
                .ownerId(turf.getOwnerId())
                .name(turf.getName())
                .address(addr)
                .pricePerHour(turf.getPricePerHour())
                .availableFrom(turf.getAvailableFrom())
                .availableTo(turf.getAvailableTo())
                .sportOptions(optionDtos)
                .build();
    }
}