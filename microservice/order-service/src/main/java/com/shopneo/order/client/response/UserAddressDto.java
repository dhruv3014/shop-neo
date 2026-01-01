package com.shopneo.order.client.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserAddressDto {

    private Long id;
    private String userId;
    private String name;
    private String phone;
    private String addressLine;
    private String city;
    private String state;
    private String pincode;
}
