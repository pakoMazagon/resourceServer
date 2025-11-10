package com.tpv.mesas.infrastructure.controller;

import com.tpv.mesas.domain.criteria.criteria.*;
import com.tpv.mesas.domain.entities.MesaServida;
import com.tpv.mesas.domain.usecases.FacturacionCU;
import com.tpv.mesas.domain.usecases.MesasCU;
import com.tpv.mesas.infrastructure.controller.mapper.MesaMapper;
import com.tpv.mesas.infrastructure.dto.*;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequestMapping("")
@RestController
@AllArgsConstructor
public class ArqueoController implements ArqueoApi {

    private FacturacionCU facturacionCU;

    private MesasCU mesaServidaCU;

    private MesaMapper mapper;

    @PostMapping(value = "/arqueo/buscar", produces = "application/json")
    public ResponseEntity<List<MesaServidaDTO>> arqueoBuscarPost(
            @Valid @RequestBody ArqueoBuscarPostRequestDTO arqueoBuscarPostRequestDTO
    ) {
        final Filters parsedFilters = this.parseFiltersJson(arqueoBuscarPostRequestDTO.getFilters()); // usa Jackson o Gson
        final Order order = Order.fromValues(arqueoBuscarPostRequestDTO.getSortField(), arqueoBuscarPostRequestDTO.getSortDirection());

        final List<MesaServida> mesasList = this.facturacionCU.findByCriteria(parsedFilters, order, arqueoBuscarPostRequestDTO.getLimit(), arqueoBuscarPostRequestDTO.getOffset(), arqueoBuscarPostRequestDTO.getImprimir());
        return ResponseEntity.status(HttpStatus.OK).body(mesasList.stream().map(mesa -> this.mapper.mapToMesaServidaDTO(mesa)).toList());
    }

    @PostMapping(value = "/arqueo/arquear")
    public ResponseEntity<ArqueoArquearPost200ResponseDTO> arqueoArquearPost(
            @Valid @RequestBody ArqueoArquearPostRequestDTO arqueoArquearPostRequestDTO
    ) {
        try {
            this.mesaServidaCU.arquearMesas(arqueoArquearPostRequestDTO.getIds(), arqueoArquearPostRequestDTO.getUsuario());
            return ResponseEntity.ok(new ArqueoArquearPost200ResponseDTO().success(true).message("Mesas arqueadas correctamente"));
        } catch (final Exception e) {
            final ArqueoArquearPost200ResponseDTO errorResponse = new ArqueoArquearPost200ResponseDTO()
                    .success(false)
                    .message("Error al arquear mesas: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    private Filters parseFiltersJson(List<ArqueoBuscarPostRequestFiltersInnerDTO> filtersDTO) {
        return new Filters(filtersDTO.stream().map(filterDTO -> new Filter(
                new FilterField(filterDTO.getField()),
                FilterOperator.fromValue(filterDTO.getOperator()),
                new FilterValue(filterDTO.getValue())
        )).toList());
    }

    @GetMapping(value = "/arqueo/camareros/buscar")
    public ResponseEntity<List<String>> arqueoCamarerosBuscarGet() {
        final List<String> camareros = this.mesaServidaCU.obtenerCamareros();
        return ResponseEntity.ok(camareros);
    }
}
