package com.shopneo.user.service;

import com.shopneo.user.entity.UserAddress;
import com.shopneo.user.mapper.AddressMapper;
import com.shopneo.user.model.AddUserAddressRequest;
import com.shopneo.user.model.UpdateUserAddressRequest;
import com.shopneo.user.repository.UserAddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserAddressService {

  private final UserAddressRepository addressRepository;
  private final AddressMapper mapper;

  public UserAddress addAddress(AddUserAddressRequest request) {
      var address = mapper.toEntity(request);
      return addressRepository.save(address);
  }

  public UserAddress editAddress(UpdateUserAddressRequest request) {
      var address = addressRepository.findById(request.getId())
              .orElseThrow(() -> new IllegalArgumentException("Invalid address Id"));
      if(request.getPhone() != null) {
          address.setPhone(request.getPhone());
      }
      if(request.getAddressType() != null) {
          address.setAddressType(request.getAddressType());
      }
      if(request.getAddressLine() != null && !request.getAddressLine().isEmpty()) {
          address.setAddressLine(request.getAddressLine());
      }
      if(request.getCity() != null) {
          address.setCity(request.getCity());
      }
      if(request.getState() != null) {
          address.setState(request.getState());
      }
      if(request.getPincode() != null) {
          address.setPincode(request.getPincode());
      }
      return addressRepository.save(address);
  }

  public List<UserAddress> getUserAddresses(String userId) {
      return addressRepository.findByUserId(userId);
  }

  public UserAddress getUserAddress(String addressId, String userId) {
      return addressRepository.findByIdAndUserId(addressId, userId)
              .orElseThrow(() -> new IllegalArgumentException("Address Not Found"));
  }

}
