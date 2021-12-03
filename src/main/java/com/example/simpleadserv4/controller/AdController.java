package com.example.simpleadserv4.controller;

import com.example.simpleadserv4.resource.ActivationResponse;
import com.example.simpleadserv4.resource.Ad;
import com.example.simpleadserv4.resource.AdCollection;
import com.example.simpleadserv4.resource.ActivationRequest;
import com.example.simpleadserv4.security.Role;
import com.example.simpleadserv4.service.AdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@RestController
@RequestMapping("/v1/marketing")
public class AdController {

    @Autowired
    Environment environment;

    private AdService adService;

    public AdController(AdService AdService) {
        this.adService = AdService;
    }

    @PostMapping("/activate")
    @ResponseStatus(code = HttpStatus.OK)
    public ActivationResponse activate(@RequestBody ActivationRequest activationRequest) {
        System.out.println("ACTIVATION COMPLETED");
        String port = environment.getProperty("local.server.port");
        return new ActivationResponse(new Random().nextLong(), "ACTIVATED | SOURCE: " + port);
    }

    @GetMapping("/ads")
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    @ResponseStatus(code = HttpStatus.OK)
    public AdCollection search() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        System.out.println("username: " + username);
        System.out.println("authorities: " + authorities);

        AdCollection adCollection;

        if(authorities.contains(new SimpleGrantedAuthority(Role.ROLE_ADMIN.name()))) {
            List<Ad> ads = adService.findAds(Optional.ofNullable(null));
            adCollection = new AdCollection(ads);
        } else {
            List<Ad> ads = adService.findAds(Optional.of(username));
            adCollection = new AdCollection(ads);
        }
        return adCollection;
    }

    @PostMapping("/ads")
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    @ResponseStatus(code = HttpStatus.CREATED)
    public Ad createAd(@RequestBody Ad ad, Principal principal) {
        //FORCE
        ad.setUsername(principal.getName());
        Ad createdAd = adService.createAd(ad);
        return createdAd;
    }

    @PutMapping("/ads/{id}")
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void updateAd(@PathVariable("id") long id, @RequestBody Ad ad, Principal principal) {
        //FORCE
        ad.setUsername(principal.getName());
        adService.updateAd(id, ad);
    }

    @GetMapping("/ads/{id}")
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    @ResponseStatus(code = HttpStatus.OK)
    public Ad getAd(@PathVariable("id") long id, Principal principal) {
        return adService.getAd(id, principal.getName());
    }


}


