package com.shopneo.user.model;

import jakarta.persistence.JoinColumn;
import lombok.Data;

@Data
public class UpdateUserAddressRequest {

  private String id;
  private String userId;
  private String name;
  private String phone;
  @JoinColumn(name = "address_line", nullable = false, unique = true)
  private String addressLine;
  private String city;
  private String state;
  private String pincode;
  private AddressType addressType;

}
