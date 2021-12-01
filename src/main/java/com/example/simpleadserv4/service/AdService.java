package com.example.simpleadserv4.service;

import com.example.simpleadserv4.resource.Ad;

import java.util.List;
import java.util.Optional;

public interface AdService {

    Ad createAd(Ad ad);
    Ad getAd(long id, String username);
    void updateAd(long id, Ad ad);
    List<Ad> findAds(Optional<String> username);

}
