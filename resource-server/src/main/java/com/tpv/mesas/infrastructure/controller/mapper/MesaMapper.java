package com.tpv.mesas.infrastructure.controller.mapper;

import com.tpv.mesas.domain.entities.Mesa;
import com.tpv.mesas.domain.entities.MesaServida;
import com.tpv.mesas.domain.entities.ProductoMesa;
import com.tpv.mesas.infrastructure.dto.MesaServidaDTO;
import com.tpv.mesas.infrastructure.dto.MesasDTO;
import com.tpv.mesas.infrastructure.dto.ProductoEnMesaDTO;
import org.mapstruct.*;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;

@Mapper(collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, componentModel = "spring", nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface MesaMapper {
    @Mapping(target = "lastUpdatedAt", source = "lastUpdatedAt", qualifiedByName = "mapToOffsetDateTime")
    MesasDTO mapToMesaDTO(Mesa mesa);

    @Mapping(target = "lastUpdatedAt", source = "lastUpdatedAt", qualifiedByName = "mapToOffsetDateTime")
    MesasDTO mapToMesaDTO(MesaServida mesaServida);

    @Mapping(target = "lastUpdatedAt", source = "lastUpdatedAt", qualifiedByName = "mapToOffsetDateTime")
    @Mapping(target = "fechaInicio", source = "fechaInicio", qualifiedByName = "mapToOffsetDateTime")
    @Mapping(target = "fechaFin", source = "fechaFin", qualifiedByName = "mapToOffsetDateTime")
    MesaServidaDTO mapToMesaServidaDTO(MesaServida mesaServida);

    @Mapping(target = "fechaHoraCreacion", source = "fechaHoraCreacion", qualifiedByName = "mapToOffsetDateTime")
    @Mapping(target = "fechaHoraPedido", source = "fechaHoraPedido", qualifiedByName = "mapToOffsetDateTime")
    @Mapping(target = "fechaHoraServido", source = "fechaHoraServido", qualifiedByName = "mapToOffsetDateTime")
    ProductoEnMesaDTO mapToProductoEnMesaDTO(ProductoMesa productoMesa);

    @Mapping(target = "lastUpdatedAt", source = "lastUpdatedAt", qualifiedByName = "mapToLocalDateTime")
    Mesa mapToMesa(MesasDTO mesaDTO);

    @Mapping(target = "lastUpdatedAt", source = "lastUpdatedAt", qualifiedByName = "mapToLocalDateTime")
    MesaServida mapToMesaServida(MesasDTO mesaDTO);

    @Mapping(target = "fechaHoraCreacion", source = "fechaHoraCreacion", qualifiedByName = "mapToLocalDateTime")
    @Mapping(target = "fechaHoraPedido", source = "fechaHoraPedido", qualifiedByName = "mapToLocalDateTime")
    @Mapping(target = "fechaHoraServido", source = "fechaHoraServido", qualifiedByName = "mapToLocalDateTime")
    ProductoMesa mapToProductoMesa(ProductoEnMesaDTO productoEnMesaDTO);

    @Named("mapToOffsetDateTime")
    default OffsetDateTime mapLocalDateTimeToOffsetDateTime(LocalDateTime localDateTime) {
        return localDateTime == null ? null : localDateTime.atZone(ZoneId.of("Europe/Madrid")).toOffsetDateTime();
    }

    @Named("mapToLocalDateTime")
    default LocalDateTime mapOffsetDateTimeToLocalDateTime(OffsetDateTime offsetDateTime) {
        return offsetDateTime == null ? null : offsetDateTime.toLocalDateTime();
    }
}
