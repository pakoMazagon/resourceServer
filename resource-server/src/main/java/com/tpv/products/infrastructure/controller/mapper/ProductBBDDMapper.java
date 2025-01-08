package com.tpv.products.infrastructure.controller.mapper;

import com.tpv.products.domain.entities.Producto;
import com.tpv.products.infrastructure.dto.ProductoBBDDDTO;
import org.mapstruct.*;

@Mapper(collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, componentModel = "spring", nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ProductBBDDMapper {
    ProductoBBDDDTO mapToProductoBBDDDTO(Producto producto);
}
