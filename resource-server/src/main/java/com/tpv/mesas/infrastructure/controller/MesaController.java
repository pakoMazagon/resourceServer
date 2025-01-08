package com.tpv.mesas.infrastructure.controller;

import com.tpv.mesas.domain.entities.Mesa;
import com.tpv.mesas.domain.usecases.MesasCU;
import com.tpv.mesas.infrastructure.controller.mapper.MesaMapper;
import com.tpv.mesas.infrastructure.dto.MesasDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RequestMapping("")
@RestController
@AllArgsConstructor
public class MesaController implements MesasApi {

    private MesasCU mesasCU;

    private MesaMapper mapper;

    @Override
    public ResponseEntity<List<MesasDTO>> mesasGet() {
        log.info("init REQUEST mesasGet");
        final List<Mesa> mesasList = this.mesasCU.obtenerTodas();
        return ResponseEntity.status(HttpStatus.OK).body(mesasList.stream().map(mesa -> this.mapper.mapToMesaDTO(mesa)).toList());

    }
}
