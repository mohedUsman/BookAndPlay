package com.booknplay.booking_services.service;


import com.booknplay.booking_services.client.TurfClient;
import com.booknplay.booking_services.client.UserClient;
import com.booknplay.booking_services.dto.SlotDto;
import com.booknplay.booking_services.dto.TurfDto;
import com.booknplay.booking_services.dto.UserDto;
import com.booknplay.booking_services.entity.Slot;
import com.booknplay.booking_services.exception.CustomException;
import com.booknplay.booking_services.repository.SlotRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SlotServiceImpl implements SlotService {

    private final SlotRepository slotRepository;
    private final TurfClient turfClient;
    private final UserClient userClient;

    @Override
    @Transactional
    public List<SlotDto> createSlots(@Valid SlotDto slotDto, Long userId) {
        TurfDto turf = turfClient.getTurfById(slotDto.getTurfId());
        UserDto user = userClient.getUserById(userId);

        if (!turf.getOwnerId().equals(userId)) {
            throw new CustomException("You are not the owner of this turf.");
        }

        LocalTime from = turf.getAvailableFrom();
        LocalTime to = turf.getAvailableTo();
        int fixedDurationMinutes = 60;

        if (from == null || to == null || !from.isBefore(to)) {
            throw new CustomException("Invalid turf availability window");
        }

        List<Slot> toCreate = new ArrayList<>();
        LocalTime cursor = from;
        while (!cursor.plusMinutes(fixedDurationMinutes).isAfter(to)) {
            LocalTime next = cursor.plusMinutes(fixedDurationMinutes);


            toCreate.add(Slot.builder()
                    .turfId(slotDto.getTurfId())
                    .date(slotDto.getDate())
                    .startTime(cursor)
                    .endTime(next)
                    .isBooked(false)
                    .build());

            cursor = next;
        }

        if (toCreate.isEmpty()) {
            throw new CustomException("No slots created (invalid range).");
        }

        List<Slot> savedSlots = slotRepository.saveAll(toCreate);
        return savedSlots.stream().map(this::mapToDto).toList();
    }

    private SlotDto mapToDto(Slot slot) {
        return SlotDto.builder()
                .id(slot.getId())
                .turfId(slot.getTurfId())
                .from(slot.getStartTime())
                .to(slot.getEndTime())
                .date(slot.getDate())
                .booked(Boolean.TRUE.equals(slot.getIsBooked()))
                .build();
    }
}