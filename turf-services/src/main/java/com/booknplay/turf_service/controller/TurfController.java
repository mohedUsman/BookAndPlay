package com.booknplay.turf_service.controller;

import com.booknplay.turf_service.dto.TurfRequestDto;
import com.booknplay.turf_service.dto.TurfResponseDto;
import com.booknplay.turf_service.service.TurfService;
import io.swagger.v3.oas.annotations.Operation; // CHANGE: swagger docs
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid; // CHANGE: validation on inputs
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SecurityRequirement(name = "BearerAuth")
@RestController
@RequestMapping("/api/turfs")
@RequiredArgsConstructor
public class TurfController { // CHANGE: thin controller

    private final TurfService turfService;

    @Operation(
            summary = "Create a new turf (owner only)", // CHANGE
            description = "Creates a turf owned by the authenticated user. Requires ROLE_OWNER. " +
                    "Supports multiple sport options and structured address."
    )
    @ApiResponse(responseCode = "200", description = "Turf created", content = @Content(schema = @Schema(implementation = TurfResponseDto.class)))
    @PostMapping
    public ResponseEntity<TurfResponseDto> addTurf(@Valid @RequestBody TurfRequestDto dto,
                                                   @RequestHeader("X-User-Email") String ownerEmail) { // CHANGE
        return ResponseEntity.ok(turfService.addTurf(dto, ownerEmail));
    }


    @Operation(
            summary = "Get turf by ID",
            description = "Fetches a turf by its ID. Any authenticated user may access."
    )
    @ApiResponse(responseCode = "200", description = "Turf found", content = @Content(schema = @Schema(implementation = TurfResponseDto.class)))
    @GetMapping("/{id}")
    public ResponseEntity<TurfResponseDto> getTurf(@PathVariable Long id) {
        return ResponseEntity.ok(turfService.getTurfById(id));
    }


    @Operation(
            summary = "Update my turf (owner only)", // CHANGE
            description = "Updates the authenticated owner's turf without requiring a path ID. " +
                    "Requires ROLE_OWNER. If multiple turfs exist per owner, clarify selection strategy."
    )
    @ApiResponse(responseCode = "200", description = "Turf updated", content = @Content(schema = @Schema(implementation = TurfResponseDto.class)))
    @PutMapping("/{turfId}")
    public ResponseEntity<TurfResponseDto> updateTurf(@PathVariable Long turfId,
                                                      @RequestBody TurfRequestDto dto,
                                                      @RequestHeader("X-User-Email") String ownerEmail) { // CHANGE
        return ResponseEntity.ok(turfService.updateTurfById(turfId, dto, ownerEmail));
    }


    @Operation(
            summary = "Delete my turf (owner only)", // CHANGE
            description = "Deletes the authenticated owner's turf without requiring a path ID. Requires ROLE_OWNER."
    )
    @ApiResponse(responseCode = "200", description = "Turf deleted")
    @DeleteMapping("/{turfId}")
    public ResponseEntity<Void> deleteTurf(@PathVariable Long turfId,
                                           @RequestHeader("X-User-Email") String ownerEmail) { // CHANGE
        turfService.deleteTurfById(turfId, ownerEmail);
        return ResponseEntity.ok().build();
    }


    @Operation(
            summary = "List all turfs (any authenticated user)",
            description = "Returns all turfs in the system."
    )
    @ApiResponse(responseCode = "200", description = "List of turfs", content = @Content)
    @GetMapping
    public ResponseEntity<List<TurfResponseDto>> all() {
        return ResponseEntity.ok(turfService.getAllTurfs());
    }

    @Operation(
            summary = "List my turfs (owner only)", // CHANGE: new API
            description = "Returns all turfs owned by the authenticated user. Requires ROLE_OWNER."
    )
    @ApiResponse(responseCode = "200", description = "List of my turfs", content = @Content)
    @GetMapping("/me")
    public ResponseEntity<List<TurfResponseDto>> myTurfs(@RequestHeader("X-User-Email") String ownerEmail) { // CHANGE
        return ResponseEntity.ok(turfService.getMyTurfs(ownerEmail));
    }
}
