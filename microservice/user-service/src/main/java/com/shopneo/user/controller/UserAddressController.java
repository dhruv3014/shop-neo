package com.shopneo.user.controller;

import com.shopneo.common.authorization.model.AuthenticatedUser;
import com.shopneo.user.dto.request.AddUserAddressRequestDto;
import com.shopneo.user.dto.request.UpdateUserAddressRequestDto;
import com.shopneo.user.dto.response.UserAddressDto;
import com.shopneo.user.mapper.AddressMapper;
import com.shopneo.user.service.UserAddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/addresses")
@RequiredArgsConstructor
public class UserAddressController {

    private final UserAddressService addressService;
    private final AddressMapper mapper;

    @GetMapping
    public List<UserAddressDto> getAddressesForUser(
            @AuthenticationPrincipal AuthenticatedUser user) {
        return mapper.toDto(addressService.getUserAddresses(user.getId()));
    }

    @PostMapping
    public UserAddressDto addUserAddress(
            @AuthenticationPrincipal AuthenticatedUser user,
            @RequestBody AddUserAddressRequestDto requestDto) {
        var request = mapper.toModel(user.getId(), requestDto);
        return mapper.toDto(addressService.addAddress(request));
    }

    @PatchMapping("/{addressId}")
    public UserAddressDto editUserAddress(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable String addressId,
            @RequestBody UpdateUserAddressRequestDto requestDto) {
        var request = mapper.toModel(user.getId(), addressId, requestDto);
        return mapper.toDto(addressService.editAddress(request));
    }


}
