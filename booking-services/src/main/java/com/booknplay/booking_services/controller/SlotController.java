package com.booknplay.booking_services.controller;

import com.booknplay.booking_services.client.UserClient;
import com.booknplay.booking_services.dto.SlotDto;
import com.booknplay.booking_services.dto.UserDto;
import com.booknplay.booking_services.service.SlotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/bookings/slots")
@RequiredArgsConstructor
public class SlotController {
    private final SlotService slotService;
    private final UserClient userClient;

    @Operation(
            summary = "Create slots for a turf (owner)",
            description = "Generates 60-minute slots for the given turf and date based on the turf's availability window. "
                    + "Enforces that the authenticated user is the owner of the turf. Idempotent per slot window.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping
    public ResponseEntity<List<SlotDto>> createSlots(@RequestBody SlotDto dto,
                                                     @AuthenticationPrincipal Jwt principal){

        UserDto user = userClient.getUserByEmail(principal.getSubject());
        return ResponseEntity.ok(slotService.createSlots(dto, user.getId()));
    }
}