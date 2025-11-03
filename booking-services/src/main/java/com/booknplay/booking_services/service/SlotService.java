package com.booknplay.booking_services.service;



import com.booknplay.booking_services.dto.SlotDto;

import java.util.List;

public interface SlotService {
    List<SlotDto> createSlots(SlotDto slot, Long userId);
}