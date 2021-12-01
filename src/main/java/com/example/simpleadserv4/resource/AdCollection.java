package com.example.simpleadserv4.resource;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class AdCollection {

    private List<Ad> items;

    public AdCollection(List<Ad> items) {
        this.items = items;
    }
}
