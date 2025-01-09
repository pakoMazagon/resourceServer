package com.tpv.mesas.application.usecases;

import com.tpv.mesas.application.ports.MesasPort;
import com.tpv.mesas.domain.entities.Mesa;
import com.tpv.mesas.domain.usecases.MesasCU;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class MesasCUImpl implements MesasCU {

    private final MesasPort mesaPort;

    @Override
    public List<Mesa> obtenerTodas() {
        return this.mesaPort.obtenerTodas();
    }

    @Override
    public Mesa actualizarMesa(Mesa mesa) {
        this.mesaPort.actualizarMesa(mesa);
        return mesa;
    }
}
