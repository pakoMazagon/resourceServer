package com.tpv.mesas.infrastructure.controller;

import com.tpv.mesas.application.ports.PrintPort;
import com.tpv.mesas.domain.entities.FacturaRequest;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/print")
@AllArgsConstructor
public class PrintController {
    PrintPort printPort;


    @PostMapping("/factura")
    public String printFactura(@RequestBody FacturaRequest factura) {
        try {
            this.printPort.printFactura(factura);
            return "Factura enviada a la impresora: ";
        } catch (final Exception e) {
            return "Error al imprimir: " + e.getMessage();
        }
    }
}
