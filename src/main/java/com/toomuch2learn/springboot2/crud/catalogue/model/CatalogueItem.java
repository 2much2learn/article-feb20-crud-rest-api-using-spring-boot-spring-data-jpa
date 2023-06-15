package com.toomuch2learn.springboot2.crud.catalogue.model;

import com.toomuch2learn.springboot2.crud.catalogue.validation.IEnumValidator;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@AllArgsConstructor
@RequiredArgsConstructor(staticName = "of")
@Document(collection = "product_catalogue")
public class CatalogueItem {
    public CatalogueItem() {
    }

    @Id
    private String id;

    @NotEmpty(message = "SKU cannot be null or empty")
    @NonNull
    private String sku;

    @NotEmpty(message = "Name cannot be null or empty")
    @NonNull
    private String name;

    @NotEmpty(message = "Description cannot be null or empty")
    @NonNull
    private String description;

    @NonNull
    @IEnumValidator(
        enumClazz = Category.class,
        message = "Invalid category provided"
    )
    private String category;

    @NotNull(message = "Price cannot be null or empty")
    @NonNull
    private Double price;

    @NotNull(message = "Inventory cannot be null or empty")
    @NonNull
    private Integer inventory;

    @NonNull
    private Date createdOn;

    private Date updatedOn;
}
