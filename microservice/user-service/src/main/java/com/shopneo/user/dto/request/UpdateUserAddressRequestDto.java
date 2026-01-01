package com.shopneo.user.dto.request;

import com.shopneo.user.model.AddressType;
import jakarta.persistence.JoinColumn;
import lombok.Data;

@Data
public class UpdateUserAddressRequestDto {
  private String name;
  private String phone;
  @JoinColumn(name = "address_line", nullable = false, unique = true)
  private String addressLine;
  private String city;
  private String state;
  private String pincode;
  private AddressType addressType;

}
