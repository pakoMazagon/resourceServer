package com.tpv.mesas.domain.usecases;

import com.tpv.mesas.domain.entities.MesaServida;
import com.tpv.mesas.domain.entities.enums.MetodoPagoEnum;
import com.tpv.mesas.domain.entities.vo.PedidoVO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface MesasCU {
    List<MesaServida> obtenerTodas();

    MesaServida actualizarMesa(MesaServida mesa);

    MesaServida obtenerMesaServidaPorId(String id);

    MesaServida cambiaNombreMesa(String id, String nuevoNombre);

    @Transactional
    MesaServida cambiaCamareroMesa(String id, String nuevoCamarero);

    void eliminar(String id);

    void cobrar(String id, MetodoPagoEnum metodoPago);

    List<String> obtenerCamareros();

    void arquearMesas(List<UUID> ids, String usuario);

    List<PedidoVO> obtenerTodosLosPedidos();
}
