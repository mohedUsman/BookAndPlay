package com.booknplay.turf_service.controller;

import com.booknplay.turf_service.client.UserClient;
import com.booknplay.turf_service.dto.TurfRequestDto;
import com.booknplay.turf_service.dto.TurfResponseDto;
import com.booknplay.turf_service.dto.UserDto;
import com.booknplay.turf_service.service.TurfService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@SecurityRequirement(name= "BearerAuth")
@RestController
@RequestMapping("/api/turfs")
@RequiredArgsConstructor
public class TurfController {
    private final TurfService turfService;
    private final UserClient userClient;
    @PostMapping
    public ResponseEntity<TurfResponseDto> addTurf(@RequestBody TurfRequestDto dto, @AuthenticationPrincipal Jwt principal){
        String email = principal.getSubject();
        log.debug("email of owner:{}", email);
        return ResponseEntity.ok(turfService.addTurf(dto,email));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TurfResponseDto> getTurf(@PathVariable Long id){
        return ResponseEntity.ok(turfService.getTurfById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TurfResponseDto> updateTurf(@PathVariable Long id,
                                                      @RequestBody TurfRequestDto dto,
                                                      @AuthenticationPrincipal Jwt principal){
        UserDto user = userClient.getUserByEmail(principal.getSubject());
        return ResponseEntity.ok(turfService.updateTurf(id, dto, user.getId()));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<TurfResponseDto> updateStatus(@PathVariable Long id,
                                                      @RequestBody TurfRequestDto dto
                                                      ){
        return ResponseEntity.ok(turfService.updateStatus(id, dto.getStatus()));
    }

    @GetMapping
    public ResponseEntity<List<TurfResponseDto>> all(){
        return ResponseEntity.ok(turfService.getAllTurfs());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTurf(@PathVariable Long id, @AuthenticationPrincipal Jwt principal ){
        UserDto user = userClient.getUserByEmail(principal.getSubject());
        Long userId = user.getId();
        List<String> roles = principal.getClaim("roles");
        turfService.deleteTurf(id, userId, roles);
        return ResponseEntity.ok("Turf deleted successfully");
    }
}
