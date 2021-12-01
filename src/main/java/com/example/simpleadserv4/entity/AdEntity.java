package com.example.simpleadserv4.entity;

import com.example.simpleadserv4.enumeration.AdType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdEntity {

    private long id;
    private String username;
    private long activationId;
    private String adName;
    private String description;
    private AdType adType;
    private String imageUrl;
    private String targetUrl;

}
