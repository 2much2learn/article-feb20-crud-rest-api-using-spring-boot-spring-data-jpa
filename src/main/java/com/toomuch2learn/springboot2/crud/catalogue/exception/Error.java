package com.toomuch2learn.springboot2.crud.catalogue.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
public class Error {

    @NonNull private int code;
    @NonNull private String message;
    @NonNull private String description;
}
