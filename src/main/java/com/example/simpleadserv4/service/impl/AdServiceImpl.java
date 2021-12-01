package com.example.simpleadserv4.service.impl;

import com.example.simpleadserv4.entity.AdEntity;
import com.example.simpleadserv4.enumeration.AdType;
import com.example.simpleadserv4.exception.NotFoundException;
import com.example.simpleadserv4.resource.Ad;
import com.example.simpleadserv4.service.AdService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class AdServiceImpl implements AdService {

    AtomicLong adCounter = new AtomicLong(101L);

    @Autowired
    private ModelMapper modelMapper;

    // Map to hold AdEntity just for demo purposes
    // Component is singleton. Only one map will exist
    private Map<Long, AdEntity> adEntityMap = new ConcurrentHashMap<Long, AdEntity>() {{
        put(101L, new AdEntity(101L, "ad_101", 500, "FirstAd", "FirstDesc", AdType.BANNER, "http://image.url", "http://target.url"));

    }};


    @Override
    public Ad createAd(Ad ad) {
        AdEntity adEntity = modelMapper.map(ad, AdEntity.class);
        Long id = adCounter.incrementAndGet();
        adEntity.setId(id);
        adEntity.setAdName(generateAdName(id));
        adEntityMap.put(id, adEntity);
        Ad savedAd = modelMapper.map(adEntity, Ad.class);
        return savedAd;
    }

    @Override
    public Ad getAd(long id, String username) {

        AdEntity adEntity = adEntityMap.get(id);
        if (Objects.isNull(adEntity)) {
            throw new NotFoundException("Ad Not Found");
        }
        if(Objects.isNull(username) || !username.equalsIgnoreCase(adEntity.getUsername())) {
            throw new AccessDeniedException("Access Denied");
        }
        return modelMapper.map(adEntity, Ad.class);
    }

    @Override
    public List<Ad> findAds(Optional<String> username) {

        List<AdEntity> adEntityList = username.isPresent() ?
                adEntityMap.values().stream().filter(u -> u.getUsername().equalsIgnoreCase(username.get())).collect(Collectors.toList())
                : new ArrayList(adEntityMap.values());
        List<Ad> adList = modelMapper.map(adEntityList, new TypeToken<List<Ad>>() {
        }.getType());
        return adList;
    }

    @Override
    public void updateAd(long id, Ad ad) {

        AdEntity existingAdEntity = adEntityMap.get(id);

        if (Objects.isNull(existingAdEntity)) {
            throw new NotFoundException("Ad Not Found");
        }
        AdEntity adEntity = modelMapper.map(ad, AdEntity.class);
        //FORCE
        adEntity.setId(id);
        adEntityMap.put(id, adEntity);


    }


    private String generateAdName(long id) {
        return "ad_" + id;
    }

}
