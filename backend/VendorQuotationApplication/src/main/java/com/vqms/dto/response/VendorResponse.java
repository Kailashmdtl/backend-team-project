package com.vqms.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;


@Data
@NoArgsConstructor
public class VendorResponse {

    private UUID id;
    private  String name;
    private String email;
    private  String phone;
    private String address;
    private String status;
    private LocalDateTime createdAt;
    private  LocalDateTime updatedAt;

}
