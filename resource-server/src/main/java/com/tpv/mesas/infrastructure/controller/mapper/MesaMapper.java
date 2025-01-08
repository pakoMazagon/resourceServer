package com.tpv.mesas.infrastructure.controller.mapper;

import com.tpv.mesas.domain.entities.Mesa;
import com.tpv.mesas.infrastructure.dto.MesasDTO;
import org.mapstruct.*;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Mapper(collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, componentModel = "spring", nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface MesaMapper {
    @Mapping(target = "lastUpdatedAt", source = "lastUpdatedAt")
    MesasDTO mapToMesaDTO(Mesa mesa);

    default OffsetDateTime mapLocalDateTimeToOffsetDateTime(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        // Aquí puedes usar cualquier offset, como ZoneOffset.UTC
        return localDateTime.atOffset(ZoneOffset.UTC); // O el offset que necesites
    }
}
