package com.booknplay.turf_service.service;

import com.booknplay.turf_service.dto.TurfRequestDto;
import com.booknplay.turf_service.dto.TurfResponseDto;

import java.util.List;

public interface TurfService {

    TurfResponseDto addTurf(TurfRequestDto dto, String ownerEmailId);

    // CHANGE: update/delete without path id (owner-only, inferred via token)
    TurfResponseDto updateTurfById(Long turfId, TurfRequestDto dto, String ownerEmail);
    void deleteTurfById(Long turfId, String ownerEmail);

    TurfResponseDto getTurfById(Long turfId);

    List<TurfResponseDto> getAllTurfs();

    // CHANGE: new API to list a turf owner’s turfs
    List<TurfResponseDto> getMyTurfs(String ownerEmailId); // CHANGE
}

