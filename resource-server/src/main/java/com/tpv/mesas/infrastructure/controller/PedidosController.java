package com.tpv.mesas.infrastructure.controller;

import com.tpv.mesas.domain.entities.vo.PedidoVO;
import com.tpv.mesas.domain.usecases.MesasCU;
import com.tpv.mesas.infrastructure.controller.mapper.PedidoMapper;
import com.tpv.mesas.infrastructure.dto.PedidoDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RequestMapping("")
@RestController
@AllArgsConstructor
public class PedidosController implements PedidosApi {

    private MesasCU mesasCU;

    private PedidoMapper mapper;

    @Override
    public ResponseEntity<List<PedidoDTO>> pedidosGet() {
        final List<PedidoVO> pedidos = this.mesasCU.obtenerTodosLosPedidos();
        final List<PedidoDTO> pedidosDTO = pedidos.stream().map(pedidoVO -> this.mapper.mapToPedidoDTO(pedidoVO)).toList();
        return ResponseEntity.ok(pedidosDTO);
    }
}
