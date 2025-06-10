package com.tpv.mesas.infrastructure.controller.mapper;

import com.tpv.mesas.domain.entities.vo.PedidoVO;
import com.tpv.mesas.infrastructure.dto.PedidoDTO;
import org.mapstruct.*;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;

@Mapper(collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, componentModel = "spring", nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface PedidoMapper {

    @Mapping(target = "product.fechaHoraCreacion", source = "product.fechaHoraCreacion", qualifiedByName = "mapToOffsetDateTime")
    @Mapping(target = "product.fechaHoraPedido", source = "product.fechaHoraPedido", qualifiedByName = "mapToOffsetDateTime")
    @Mapping(target = "product.fechaHoraServido", source = "product.fechaHoraServido", qualifiedByName = "mapToOffsetDateTime")
    PedidoDTO mapToPedidoDTO(PedidoVO pedidoVO);

    @Named("mapToOffsetDateTime")
    default OffsetDateTime mapLocalDateTimeToOffsetDateTime(LocalDateTime localDateTime) {
        return localDateTime == null ? null : localDateTime.atZone(ZoneId.of("Europe/Madrid")).toOffsetDateTime();
    }

    @Named("mapToLocalDateTime")
    default LocalDateTime mapOffsetDateTimeToLocalDateTime(OffsetDateTime offsetDateTime) {
        return offsetDateTime == null ? null : offsetDateTime.toLocalDateTime();
    }
}
