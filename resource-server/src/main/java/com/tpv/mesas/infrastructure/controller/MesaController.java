package com.tpv.mesas.infrastructure.controller;

import com.tpv.mesas.domain.entities.Mesa;
import com.tpv.mesas.domain.usecases.MesasCU;
import com.tpv.mesas.infrastructure.controller.mapper.MesaMapper;
import com.tpv.mesas.infrastructure.dto.MesasDTO;
import com.tpv.mesas.infrastructure.dto.UpdateMesa200ResponseDTO;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
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

    @Override
    public ResponseEntity<UpdateMesa200ResponseDTO> updateMesa(
            @Parameter(name = "MesasDTO", description = "", required = true) @Valid @RequestBody MesasDTO mesasDTO
    ) {
        final Mesa mesa = this.mapper.mapToMesa(mesasDTO);
        this.mesasCU.actualizarMesa(mesa);

        final UpdateMesa200ResponseDTO bodyDev = new UpdateMesa200ResponseDTO();
        bodyDev.setMessage(String.format("Mesa {} modificada ocupada {} por camarero{}", mesasDTO.getNombreTradicional(), mesasDTO.getOcupada(), mesasDTO.getCamarero()));
        return ResponseEntity.status(HttpStatus.OK).body(bodyDev);
    }
}
