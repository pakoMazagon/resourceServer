package com.tpv.mesas.infrastructure.controller;

import com.tpv.mesas.domain.entities.MesaServida;
import com.tpv.mesas.domain.entities.enums.MetodoPagoEnum;
import com.tpv.mesas.domain.usecases.MesasCU;
import com.tpv.mesas.infrastructure.controller.mapper.MesaMapper;
import com.tpv.mesas.infrastructure.dto.*;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
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
    public ResponseEntity<MesasDTO> mesasBuscarGet(String id) {
        final MesaServida mesa = this.mesasCU.obtenerMesaServidaPorId(id);
        return ResponseEntity.status(HttpStatus.OK).body(this.mapper.mapToMesaDTO(mesa));
    }

    @Override
    public ResponseEntity<List<MesasDTO>> mesasGet() {
        log.info("init REQUEST mesasGet");
        final List<MesaServida> mesasList = this.mesasCU.obtenerTodas();
        return ResponseEntity.status(HttpStatus.OK).body(mesasList.stream().map(mesa -> this.mapper.mapToMesaDTO(mesa)).toList());
    }

    @Override
    public ResponseEntity<UpdateMesa200ResponseDTO> updateMesa(
            @Parameter(name = "MesasDTO", description = "", required = true) @Valid @RequestBody MesasDTO mesasDTO
    ) {
        final MesaServida mesa = this.mapper.mapToMesaServida(mesasDTO);
        this.mesasCU.actualizarMesa(mesa);

        final UpdateMesa200ResponseDTO bodyDev = new UpdateMesa200ResponseDTO();
        bodyDev.setMessage(String.format("Mesa {} modificada ocupada {} por camarero{}", mesasDTO.getNombre(), mesasDTO.getOcupada(), mesasDTO.getCamarero()));
        return ResponseEntity.status(HttpStatus.OK).body(bodyDev);
    }

    @Override
    public ResponseEntity<CambiaNombreMesa200ResponseDTO> cambiaNombreMesa(
            @Parameter(name = "id", description = "ID de la mesa a renombrar.", required = true, in = ParameterIn.PATH) @PathVariable("id") String id,
            @Parameter(name = "CambiaNombreMesaRequestDTO", description = "", required = true) @Valid @RequestBody CambiaNombreMesaRequestDTO cambiaNombreMesaRequestDTO
    ) {
        this.mesasCU.cambiaNombreMesa(id, cambiaNombreMesaRequestDTO.getNombre());

        final CambiaNombreMesa200ResponseDTO bodyDev = new CambiaNombreMesa200ResponseDTO();
        bodyDev.setMessage(String.format("Mesa id {} cambia de nombre {} por camarero{}", id, cambiaNombreMesaRequestDTO.getNombre()));
        return ResponseEntity.status(HttpStatus.OK).body(bodyDev);
    }

    @Override
    public ResponseEntity<BorraMesa200ResponseDTO> borraMesa(
            @Parameter(name = "id", description = "ID de la mesa a eliminar.", required = true, in = ParameterIn.PATH) @PathVariable("id") String id
    ) {
        this.mesasCU.eliminar(id);

        final BorraMesa200ResponseDTO bodyDev = new BorraMesa200ResponseDTO();
        bodyDev.setMessage(String.format("Mesa id {} borrada{}", id));
        return ResponseEntity.status(HttpStatus.OK).body(bodyDev);
    }

    @Override
    public ResponseEntity<CobraMesa200ResponseDTO> cobraMesa(
            @Parameter(name = "CobraMesaRequestDTO", description = "", required = true) @Valid @RequestBody CobraMesaRequestDTO cobraMesaRequestDTO
    ) {
        this.mesasCU.cobrar(cobraMesaRequestDTO.getId(), MetodoPagoEnum.valueOf(cobraMesaRequestDTO.getTipoPago().getValue()));

        final CobraMesa200ResponseDTO bodyDev = new CobraMesa200ResponseDTO();
        bodyDev.setMessage(String.format("Mesa id {} cobrada{}", cobraMesaRequestDTO.getId()));
        return ResponseEntity.status(HttpStatus.OK).body(bodyDev);

    }
}
