package com.vqms.mapper;

import com.vqms.dto.request.VendorRequest;
import com.vqms.dto.response.VendorResponse;
import com.vqms.entity.Vendor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class VendorMapper {

    public Vendor toEntity(VendorRequest vendorRequest){
        Vendor vendor = new Vendor();
        vendor.setName(vendorRequest.getName());
        vendor.setEmail(vendorRequest.getEmail());
        vendor.setPhone(vendorRequest.getPhone());
        vendor.setAddress(vendorRequest.getAddress());

        return  vendor;


    }

    public VendorResponse toResponse(Vendor vendor){
        VendorResponse vendorResponse = new VendorResponse();
        vendorResponse.setId(vendor.getId());
        vendorResponse.setName(vendor.getName());
        vendorResponse.setEmail(vendor.getEmail());
        vendorResponse.setAddress(vendor.getAddress());
        vendorResponse.setStatus(vendor.getStatus());
        vendorResponse.setCreatedAt(vendor.getCreatedAt());
        vendorResponse.setUpdatedAt(vendor.getUpdatedAt());
        return vendorResponse;
    }
}
