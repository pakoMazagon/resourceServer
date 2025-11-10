package com.tpv.mesas.application.ports;

import com.tpv.mesas.domain.criteria.criteria.Filters;
import com.tpv.mesas.domain.entities.FacturaRequest;
import com.tpv.mesas.domain.entities.MesaServida;
import com.tpv.mesas.domain.entities.ProductoMesa;

import javax.print.PrintException;
import java.util.List;

public interface PrintPort {

    void printTicket(String printerName, String camarero, List<ProductoMesa> productos, MesaServida mesaServida) throws PrintException;

    void printFactura(FacturaRequest printerName) throws PrintException;

    void printArqueo(Filters filters, List<MesaServida> listaMesas);
}
