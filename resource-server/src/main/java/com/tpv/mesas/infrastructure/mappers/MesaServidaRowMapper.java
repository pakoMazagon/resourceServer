package com.tpv.mesas.infrastructure.mappers;

import com.tpv.mesas.domain.entities.MesaServida;
import com.tpv.mesas.domain.entities.enums.EstadoMesaEnum;
import com.tpv.mesas.domain.entities.enums.MetodoPagoEnum;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@Component
public class MesaServidaRowMapper implements RowMapper<MesaServida> {

    @Override
    public MesaServida mapRow(ResultSet rs, int rowNum) throws SQLException {
        return MesaServida.builder()
                .id(UUID.fromString(rs.getString("id")))
                .mesaReferencia(rs.getString("mesa_referencia"))
                .numero(rs.getInt("numero"))
                .sector(rs.getString("sector"))
                .nombre(rs.getString("nombre"))
                .camarero(rs.getString("camarero"))
                .cantidad(rs.getDouble("cantidad"))
                .fechaInicio(rs.getTimestamp("fecha_inicio") != null ?
                        rs.getTimestamp("fecha_inicio").toLocalDateTime() : null)
                .fechaFin(rs.getTimestamp("fecha_fin") != null ?
                        rs.getTimestamp("fecha_fin").toLocalDateTime() : null)
                .arqueada(rs.getBoolean("arqueada"))
                .activa(rs.getBoolean("activa"))
                .estado(EstadoMesaEnum.valueOf(rs.getString("estado")))
                .metodoPago(rs.getString("metodo_pago") != null ?
                        MetodoPagoEnum.valueOf(rs.getString("metodo_pago")) : null)
                .borrada(rs.getBoolean("borrada"))
                .ocupada(rs.getBoolean("ocupada"))
                .version(rs.getInt("version"))
                .lastUpdatedAt(rs.getTimestamp("last_updated_at") != null ?
                        rs.getTimestamp("last_updated_at").toLocalDateTime() : null)
                .lastUpdatedBy(rs.getString("last_updated_by"))
                .build();
    }
}
