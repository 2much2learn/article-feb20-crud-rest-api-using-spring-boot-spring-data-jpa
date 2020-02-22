package com.toomuch2learn.springboot2.crud.catalogue.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CatalogueItemList {

    @NonNull
    private List<CatalogueItem> data;
}
