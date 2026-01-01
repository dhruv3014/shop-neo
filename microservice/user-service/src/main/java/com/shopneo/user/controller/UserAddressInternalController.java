package com.shopneo.user.controller;

import com.shopneo.user.dto.response.UserAddressDto;
import com.shopneo.user.mapper.AddressMapper;
import com.shopneo.user.service.UserAddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/internal")
@RequiredArgsConstructor
public class UserAddressInternalController {

    private final UserAddressService addressService;
    private final AddressMapper mapper;

    @GetMapping("/addresses/{addressId}")
    public ResponseEntity<UserAddressDto> getAddressesForUser(
            @RequestParam String userId,
            @PathVariable String addressId) {
        return ResponseEntity.ok()
                .body(mapper.toDto(addressService.getUserAddress(addressId, userId)));
    }
}
