package com.tpv.mesas.domain.entities;

import java.time.LocalDateTime;

public record FacturaRequest(LocalDateTime fecha, int cantidad, String concepto, Double total, String cliente,
                             String cif, String domicilio) {
}
