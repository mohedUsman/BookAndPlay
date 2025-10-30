package com.booknplay.turf_service.service;

import com.booknplay.turf_service.dto.TurfRequestDto;
import com.booknplay.turf_service.dto.TurfResponseDto;
import com.booknplay.turf_service.entity.TurfStatus;

import java.util.List;

public interface TurfService {
    TurfResponseDto addTurf(TurfRequestDto dto, String ownerEmailId);
    TurfResponseDto updateTurf(Long turfId, TurfRequestDto dto, Long ownerId);
    List<TurfResponseDto> getAllTurfs();
    TurfResponseDto updateStatus(Long id, TurfStatus status);
    void deleteTurf(Long turfId, Long userId, List<String> roles);
    TurfResponseDto getTurfById(Long turfId);
}
