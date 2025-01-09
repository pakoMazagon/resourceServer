package com.tpv.mesas.infrastructure.controller.mapper;

import com.tpv.mesas.domain.entities.Mesa;
import com.tpv.mesas.infrastructure.dto.MesasDTO;
import org.mapstruct.*;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Mapper(collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, componentModel = "spring", nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface MesaMapper {
    @Mapping(target = "lastUpdatedAt", source = "lastUpdatedAt", qualifiedByName = "mapToOffsetDateTime")
    MesasDTO mapToMesaDTO(Mesa mesa);

    @Mapping(target = "lastUpdatedAt", source = "lastUpdatedAt", qualifiedByName = "mapToLocalDateTime")
    Mesa mapToMesa(MesasDTO mesaDTO);

    @Named("mapToOffsetDateTime")
    default OffsetDateTime mapLocalDateTimeToOffsetDateTime(LocalDateTime localDateTime) {
        return localDateTime == null ? null : localDateTime.atOffset(ZoneOffset.UTC);
    }

    @Named("mapToLocalDateTime")
    default LocalDateTime mapOffsetDateTimeToLocalDateTime(OffsetDateTime offsetDateTime) {
        return offsetDateTime == null ? null : offsetDateTime.toLocalDateTime();
    }
}
