package com.shopneo.user.dto.response;

import com.shopneo.user.model.AddressType;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class UserAddressDto {

    private String id;
    private String userId;
    private String name;
    private String phone;
    private String addressLine;
    private String city;
    private String state;
    private String pincode;
    private AddressType addressType;
    private Instant createdAt;
    private Instant updatedAt;
}
