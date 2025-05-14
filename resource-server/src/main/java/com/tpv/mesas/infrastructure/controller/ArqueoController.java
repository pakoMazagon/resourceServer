package com.tpv.mesas.infrastructure.controller;

import com.tpv.mesas.domain.criteria.criteria.*;
import com.tpv.mesas.domain.entities.MesaServida;
import com.tpv.mesas.domain.usecases.FacturacionCU;
import com.tpv.mesas.infrastructure.controller.mapper.MesaMapper;
import com.tpv.mesas.infrastructure.dto.ArqueoBuscarPostRequestDTO;
import com.tpv.mesas.infrastructure.dto.ArqueoBuscarPostRequestFiltersInnerDTO;
import com.tpv.mesas.infrastructure.dto.MesaServidaDTO;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RequestMapping("")
@RestController
@AllArgsConstructor
public class ArqueoController implements ArqueoApi {

    private FacturacionCU facturacionCU;

    private MesaMapper mapper;

    @PostMapping(value = "/arqueo/buscar", produces = "application/json")
    public ResponseEntity<List<MesaServidaDTO>> arqueoBuscarPost(
            @Valid @RequestBody ArqueoBuscarPostRequestDTO arqueoBuscarPostRequestDTO
    ) {
        final Filters parsedFilters = this.parseFiltersJson(arqueoBuscarPostRequestDTO.getFilters()); // usa Jackson o Gson
        final Order order = Order.fromValues(arqueoBuscarPostRequestDTO.getSortField(), arqueoBuscarPostRequestDTO.getSortDirection());

        final List<MesaServida> mesasList = this.facturacionCU.findByCriteria(parsedFilters, order, arqueoBuscarPostRequestDTO.getLimit(), arqueoBuscarPostRequestDTO.getOffset());
        return ResponseEntity.status(HttpStatus.OK).body(mesasList.stream().map(mesa -> this.mapper.mapToMesaServidaDTO(mesa)).toList());
    }

    private Filters parseFiltersJson(List<ArqueoBuscarPostRequestFiltersInnerDTO> filtersDTO) {
        return new Filters(filtersDTO.stream().map(filterDTO -> new Filter(
                new FilterField(filterDTO.getField()),
                FilterOperator.fromValue(filterDTO.getOperator()),
                new FilterValue(filterDTO.getValue())
        )).toList());
    }
}
