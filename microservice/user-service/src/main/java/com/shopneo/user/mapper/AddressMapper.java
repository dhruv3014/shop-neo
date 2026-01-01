package com.shopneo.user.mapper;

import com.shopneo.user.dto.request.AddUserAddressRequestDto;
import com.shopneo.user.dto.request.UpdateUserAddressRequestDto;
import com.shopneo.user.dto.response.UserAddressDto;
import com.shopneo.user.model.AddUserAddressRequest;
import com.shopneo.user.entity.UserAddress;
import com.shopneo.user.model.UpdateUserAddressRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AddressMapper {

    UserAddress toEntity(AddUserAddressRequest request);

    @Mapping(target = "id", source = "addressId")
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "name", source = "requestDto.name")
    @Mapping(target = "phone", source = "requestDto.phone")
    @Mapping(target = "addressLine", source = "requestDto.addressLine")
    @Mapping(target = "city", source = "requestDto.city")
    @Mapping(target = "state", source = "requestDto.state")
    @Mapping(target = "pincode", source = "requestDto.pincode")
    @Mapping(target = "addressType", source = "requestDto.addressType")
    UpdateUserAddressRequest toModel(String userId, String addressId, UpdateUserAddressRequestDto requestDto);

    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "name", source = "requestDto.name")
    @Mapping(target = "phone", source = "requestDto.phone")
    @Mapping(target = "addressLine", source = "requestDto.addressLine")
    @Mapping(target = "city", source = "requestDto.city")
    @Mapping(target = "state", source = "requestDto.state")
    @Mapping(target = "pincode", source = "requestDto.pincode")
    @Mapping(target = "addressType", source = "requestDto.addressType")
    AddUserAddressRequest toModel(String userId, AddUserAddressRequestDto requestDto);

    UserAddressDto toDto(UserAddress address);

    List<UserAddressDto> toDto(List<UserAddress> address);
}
