package com.shopneo.user.dto.request;

import com.shopneo.user.model.AddressType;
import lombok.Data;

@Data
public class AddUserAddressRequestDto {

  private String name;
  private String phone;
  private String addressLine;
  private String city;
  private String state;
  private String pincode;
  private AddressType addressType;

}
