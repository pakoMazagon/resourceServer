package com.tpv.mesas.infrastructure.adapters;

import com.tpv.mesas.application.ports.PrintPort;
import com.tpv.mesas.domain.criteria.criteria.Filter;
import com.tpv.mesas.domain.criteria.criteria.Filters;
import com.tpv.mesas.domain.entities.FacturaRequest;
import com.tpv.mesas.domain.entities.MesaServida;
import com.tpv.mesas.domain.entities.ProductoMesa;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.imageio.ImageIO;
import javax.print.PrintException;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;


@Component
@AllArgsConstructor
public class PrintAdapter implements PrintPort {

    private static final String SEPARATOR = "-----------------------------------------------\n";

    private static final String NAME_PRINTER = "CP858";

    @Override
    public void printTicket(String printerName, String camarero, List<ProductoMesa> productos, MesaServida mesaServida) throws PrintException {
        final byte[] data = this.buildTicketArticulos(camarero, productos, mesaServida);

        // 2. Preparamos para el envío REST
        final RestTemplate rest = new RestTemplate();
        final String url = "http://host.docker.internal:9101/print";

        // Configurar los Headers
        final HttpHeaders headers = new HttpHeaders();
        // Esto es crucial: enviamos un stream de bytes (octet-stream)
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        // Crear el cuerpo de la petición con los bytes y los headers
        final HttpEntity<byte[]> entity = new HttpEntity<>(data, headers);

        // 3. Enviamos al endpoint
        // Usamos postForObject para enviar la entidad (bytes + headers)
        final String response = rest.postForObject(url, entity, String.class);

        // Opcional: imprimir la respuesta del servicio remoto
        System.out.println("Respuesta del servicio de impresión: " + response);
    }

    @Override
    public void printFactura(FacturaRequest factura) throws PrintException {
        final byte[] data = this.buildFactura(factura);

        // 2. Preparamos para el envío REST
        final RestTemplate rest = new RestTemplate();
        final String url = "http://host.docker.internal:9101/print";

        // Configurar los Headers
        final HttpHeaders headers = new HttpHeaders();
        // Esto es crucial: enviamos un stream de bytes (octet-stream)
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        // Crear el cuerpo de la petición con los bytes y los headers
        final HttpEntity<byte[]> entity = new HttpEntity<>(data, headers);

        // 3. Enviamos al endpoint
        // Usamos postForObject para enviar la entidad (bytes + headers)
        final String response = rest.postForObject(url, entity, String.class);

        // Opcional: imprimir la respuesta del servicio remoto
        System.out.println("Respuesta del servicio de impresión: " + response);
    }

    @Override
    public void printArqueo(Filters filters, List<MesaServida> listaMesas) {
        final byte[] data = this.buildArqueoTicket(filters, listaMesas);
        // 2. Preparamos para el envío REST
        final RestTemplate rest = new RestTemplate();
        final String url = "http://host.docker.internal:9101/print";

        // Configurar los Headers
        final HttpHeaders headers = new HttpHeaders();
        // Esto es crucial: enviamos un stream de bytes (octet-stream)
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        // Crear el cuerpo de la petición con los bytes y los headers
        final HttpEntity<byte[]> entity = new HttpEntity<>(data, headers);

        // 3. Enviamos al endpoint
        // Usamos postForObject para enviar la entidad (bytes + headers)
        final String response = rest.postForObject(url, entity, String.class);

        // Opcional: imprimir la respuesta del servicio remoto
        System.out.println("Respuesta del servicio de impresión: " + response);
    }

    private byte[] buildArqueoTicket(Filters filters, List<MesaServida> listaMesas) {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final Charset charset = Charset.forName(NAME_PRINTER);
        final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        // 1. Calcular totales
        final int totalMesas = listaMesas.size();
        final double sumaTotal = listaMesas.stream()
                .mapToDouble(m -> Optional.ofNullable(m.getCantidad()).orElse(0.0))
                .sum();

        try {
            baos.write(EscPosCommands.INIT);
            baos.write(EscPosCommands.SELECT_CP858);

            // === CABECERA ===
            baos.write(EscPosCommands.CENTER);
            baos.write(EscPosCommands.BOLD_ON);
            baos.write(EscPosCommands.DOUBLE_ON);
            baos.write("RESUMEN ARQUEO\n".getBytes(charset));
            baos.write(EscPosCommands.DOUBLE_OFF);
            baos.write(EscPosCommands.BOLD_OFF);
            baos.write(SEPARATOR.getBytes(charset));

            // === DATOS DE ARQUEO ===
            baos.write(EscPosCommands.LEFT);
            baos.write(EscPosCommands.BOLD_ON);
            baos.write(("FECHA Y HORA ARQUEO: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yy HH:mm:ss")) + "\n").getBytes(charset));
            baos.write(SEPARATOR.getBytes(charset));
            baos.write(EscPosCommands.BOLD_OFF);

            // === FILTROS APLICADOS ===
            baos.write(EscPosCommands.BOLD_ON);
            baos.write("FILTROS APLICADOS:\n".getBytes(charset));
            baos.write(EscPosCommands.BOLD_OFF);

            boolean hasDateFilter = false;

            // Iterar sobre los filtros para imprimir solo Fechas
            for (final Filter filter : filters.filters()) {
                final String fieldName = filter.field().value(); // Asumiendo value() es el getter
                final String filterValue = filter.value().value();

                if (fieldName.equalsIgnoreCase("fecha_inicio")) {
                    if (!hasDateFilter) {
                        baos.write(("  F. INICIO: " + filterValue + "\n").getBytes(charset));
                        hasDateFilter = true;
                        continue;
                    }
                    baos.write(("  F. FIN: " + filterValue + "\n").getBytes(charset));
                }
                // Podrías añadir más filtros si fueran relevantes (e.g., Camarero)
            }

            // Si no se aplicaron filtros de fecha, imprimimos un indicador.
            if (!hasDateFilter) {
                baos.write("  (Sin filtros de fecha específicos)\n".getBytes(charset));
                baos.write(("  F. actual: " + LocalDateTime.now() + "\n").getBytes(charset));
            }

            baos.write(SEPARATOR.getBytes(charset));

            // === TOTALES ===
            baos.write(EscPosCommands.LEFT);
            baos.write(EscPosCommands.BOLD_ON);
            baos.write(EscPosCommands.TEXT_MEDIUM);
            baos.write(EscPosCommands.TEXT_WIDE);

            // Línea de Total Mesas
            baos.write(("TOTAL MESAS: " + totalMesas + "\n").getBytes(charset));

            // Línea de Suma Total
            baos.write(("SUMA TOTAL: " + String.format(Locale.US, "%.2f", sumaTotal) + " EUR\n").getBytes(charset));

            baos.write(EscPosCommands.DOUBLE_OFF);
            baos.write(EscPosCommands.BOLD_OFF);

            baos.write(SEPARATOR.getBytes(charset));

            // === PIE ===
            baos.write(EscPosCommands.CENTER);
            baos.write("\nFIN DE ARQUEO\n".getBytes(charset));

            // --- ESPACIO DE CORTE ---
            baos.write("\n\n\n\n\n\n\n".getBytes(charset));
            baos.write(EscPosCommands.CUT);

        } catch (final Exception e) {
            e.printStackTrace();
        }

        return baos.toByteArray();
    }

    private byte[] buildFactura(FacturaRequest factura) {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final Charset charset = Charset.forName(NAME_PRINTER);

        try {
            baos.write(EscPosCommands.INIT);
            baos.write(EscPosCommands.SELECT_CP858);

            // --- LOGO CENTRADO (si existe) ---
            try (final InputStream logoStream = this.getClass().getResourceAsStream("/logoBar.png")) {
                if (logoStream != null) {
                    final BufferedImage logo = ImageIO.read(logoStream);

                    // Escalar manteniendo proporciones
                    final int targetWidth = Math.min(logo.getWidth(), 384); // 384px ≈ 80mm
                    final int targetHeight = (int) ((double) logo.getHeight() * targetWidth / logo.getWidth());

                    // Fondo blanco para evitar negros invertidos
                    final BufferedImage whiteBg = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
                    final Graphics2D g = whiteBg.createGraphics();
                    g.setColor(Color.WHITE);
                    g.fillRect(0, 0, targetWidth, targetHeight);
                    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                    g.drawImage(logo, 0, 0, targetWidth, targetHeight, null);
                    g.dispose();

                    // Convertimos a blanco y negro con umbral manual
                    final BufferedImage bwImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_BYTE_BINARY);
                    final Graphics2D g2 = bwImage.createGraphics();
                    g2.drawImage(whiteBg, 0, 0, null);
                    g2.dispose();

                    // Contraste opcional (para recuperar detalles del calamar y texto)
                    for (int y = 0; y < bwImage.getHeight(); y++) {
                        for (int x = 0; x < bwImage.getWidth(); x++) {
                            final int rgb = whiteBg.getRGB(x, y);
                            final int r = (rgb >> 16) & 0xFF;
                            final int g3 = (rgb >> 8) & 0xFF;
                            final int b = rgb & 0xFF;
                            final int luminance = (r + g3 + b) / 3;
                            if (luminance < 190) bwImage.setRGB(x, y, 0xFF000000); // negro
                            else bwImage.setRGB(x, y, 0xFFFFFFFF); // blanco
                        }
                    }

                    baos.write(EscPosCommands.CENTER);
                    baos.write(EscPosImageHelper.imageToRasterBitImageCommand(bwImage));

                    // --- Reset de modo texto y espacio físico tras el logo ---
                    baos.write(new byte[]{0x1B, 0x32}); // Line spacing default
                    baos.write(new byte[]{0x1B, 0x4A, 40}); // Avanza físicamente 40 puntos (~5-6mm)
                    baos.write(EscPosCommands.INIT); // Reinicia modo texto para evitar que se corte o se pegue
                    baos.write(EscPosCommands.SELECT_CP858);
                    baos.write("\n".getBytes(charset));

                } else {
                    System.out.println("Logo no encontrado en /logoBar.png");
                }
            } catch (final Exception ex) {
                ex.printStackTrace();
            }


            // === CABECERA ===
            baos.write(EscPosCommands.CENTER);
            baos.write("\n".getBytes(charset));

            baos.write("FRANCISCO RODRÍGUEZ SUÁREZ\n".getBytes(charset));
            baos.write("C.I.F.: E-21.520.705\n".getBytes(charset));
            baos.write("C/ FUENTEPIÑA, 11 - MAZAGÓN (HUELVA)\n".getBytes(charset));
            baos.write("TELF: (959)536253 / 675733682\n".getBytes(charset));

            // === DATOS FACTURA ===
            baos.write(("FACTURA Nº: 02739  ").getBytes(charset));
            baos.write(("FECHA: " + (factura.fecha() != null ? factura.fecha() : LocalDateTime.now()) + "\n\n").getBytes(charset));

            baos.write(SEPARATOR.getBytes(charset));

            // === COLUMNAS ===
            baos.write(EscPosCommands.BOLD_ON);
            baos.write(this.formatLineFactura("ARTICULO", "CANT", "PREC", "IMP").getBytes(charset));
            baos.write(EscPosCommands.BOLD_OFF);
            baos.write(SEPARATOR.getBytes(charset));

            // === PRODUCTO ===
            final double total = factura.total() != null ? factura.total() : 0;
            final double precio = factura.cantidad() > 0 ? total / factura.cantidad() : 0;
            baos.write(this.formatLineFactura(
                    factura.concepto(),
                    String.valueOf(factura.cantidad()),
                    String.format("%.2f", precio),
                    String.format("%.2f", total)
            ).getBytes(charset));

            baos.write("\n\n".getBytes(charset));

            // === TOTAL ===
            baos.write(EscPosCommands.RIGHT);
            baos.write(EscPosCommands.BOLD_ON);
            baos.write(EscPosCommands.TEXT_MEDIUM);
            baos.write(EscPosCommands.TEXT_WIDE);
            baos.write(("TOTAL: " + String.format("%.2f", total) + " EUR\n").getBytes(charset));
            baos.write(EscPosCommands.BOLD_OFF);
            baos.write(EscPosCommands.DOUBLE_OFF);
            baos.write(EscPosCommands.RIGHT);
            baos.write("I.V.A. INCLUIDO\n\n".getBytes(charset));

            // === CLIENTE ===
            baos.write(EscPosCommands.CENTER);
            baos.write(EscPosCommands.BOLD_ON);
            baos.write(SEPARATOR.getBytes(charset));
            baos.write(("Cliente: ").getBytes(charset));
            baos.write(EscPosCommands.TEXT_MEDIUM);
            baos.write(EscPosCommands.TEXT_WIDE);
            baos.write(((factura.cliente() != null ? factura.cliente() : "") + "\n").getBytes(charset));
            baos.write(EscPosCommands.DOUBLE_OFF);
            baos.write(("C.I.F.:: ").getBytes(charset));
            baos.write(EscPosCommands.TEXT_MEDIUM);
            baos.write(EscPosCommands.TEXT_WIDE);
            baos.write(((factura.cif() != null ? factura.cif() : "") + "\n").getBytes(charset));
            baos.write(EscPosCommands.DOUBLE_OFF);
            baos.write(("Domicilio: ").getBytes(charset));
            baos.write(EscPosCommands.TEXT_MEDIUM);
//            baos.write(EscPosCommands.TEXT_WIDE);
            baos.write(((factura.domicilio() != null ? factura.domicilio() : "") + "\n").getBytes(charset));
            baos.write(EscPosCommands.DOUBLE_OFF);

            baos.write("-----------------------------------------------\n\n".getBytes(charset));
            baos.write(EscPosCommands.DOUBLE_OFF);
            baos.write(EscPosCommands.BOLD_OFF);
            // === PIE ===
            baos.write(EscPosCommands.CENTER);
            baos.write("\nGRACIAS POR SU VISITA\n".getBytes(charset));

            // --- ESPACIO DE CORTE ---
            baos.write("\n\n\n\n\n\n\n".getBytes(charset));
            baos.write(EscPosCommands.CUT);

        } catch (final Exception e) {
            e.printStackTrace();
        }

        return baos.toByteArray();
    }


    private byte[] buildTicketArticulos(String camarero, List<ProductoMesa> productos, MesaServida mesaServida) {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final Charset charset = Charset.forName(NAME_PRINTER);

        try {
            baos.write(EscPosCommands.INIT);
            baos.write(EscPosCommands.SELECT_CP858);

            // Si quieres imprimir el logo, llama aquí:
            // baos.write(printImage("src/main/resources/logo_bar_bw.png"));

            // === CABECERA ===
            baos.write(EscPosCommands.CENTER);
            baos.write(EscPosCommands.BOLD_ON);
            baos.write(EscPosCommands.DOUBLE_ON);
            baos.write("BAR-REST EL CHOCO\n".getBytes(charset));
            baos.write(EscPosCommands.DOUBLE_OFF);
            baos.write(EscPosCommands.BOLD_OFF);
            baos.write("MAZAGÓN, C.B.\n".getBytes(charset));
            baos.write("Avda/ Fuentepiña, 11\n".getBytes(charset));
            baos.write("TELÉFONOS: 959536825/675733682\n".getBytes(charset));
            baos.write("N.I.F.: E21520705\n".getBytes(charset));
            baos.write("10% IVA INCLUIDO\n\n".getBytes(charset));

            // === DATOS FACTURA ===
            baos.write(EscPosCommands.LEFT);
            baos.write("CAJA: CAJA 0001\n".getBytes(charset));
            baos.write("FACTURA SIMPLIFICADA: \n".getBytes(charset));
            final String fecha = mesaServida.getFechaFin() != null
                    ? mesaServida.getFechaFin().format(DateTimeFormatter.ofPattern("dd/MM/yy"))
                    : LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yy"));
            final String hora = mesaServida.getFechaFin() != null
                    ? mesaServida.getFechaFin().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
                    : LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));

            baos.write(("FECHA: " + fecha + "   HORA: " + hora + "\n").getBytes(charset));
            baos.write(("MESA: " + mesaServida.getNumero() + "\n").getBytes(charset));
            baos.write(SEPARATOR.getBytes(charset));

            // === ENCABEZADO DE ARTÍCULOS ===
            baos.write(EscPosCommands.BOLD_ON);
            baos.write(this.formatLine("ARTÍCULO", "UD", "PRECIO", "IMPORTE").getBytes(charset));
            baos.write(EscPosCommands.BOLD_OFF);
            baos.write(SEPARATOR.getBytes(charset));

            // === LÍNEAS DE PRODUCTOS ===
            double total = 0.0;
            for (final ProductoMesa p : productos) {
                final int unidades = Optional.ofNullable(p.getUnidades()).orElse(0);
                final double precio = Optional.ofNullable(p.getPrecio()).orElse(0.0);
                final double importe = unidades * precio;
                total += importe;

                String nombre = Optional.ofNullable(p.getNombre()).orElse("-");
                if (nombre.length() > 26) {
                    nombre = nombre.substring(0, 26); // evita desbordar línea
                }

                baos.write(this.formatLine(nombre, String.valueOf(unidades),
                        String.format(Locale.US, "%.2f", precio),
                        String.format(Locale.US, "%.2f", importe)).getBytes(charset));
            }

            baos.write(SEPARATOR.getBytes(charset));

            // === TOTAL ===
            baos.write(EscPosCommands.RIGHT);
            baos.write(EscPosCommands.BOLD_ON);
            baos.write(("TOTAL: " + String.format(Locale.ENGLISH, "%.2f", total) + " EUR\n").getBytes(charset));
            baos.write(EscPosCommands.BOLD_OFF);

            // === PIE ===
            baos.write(EscPosCommands.LEFT);
            baos.write(("\nFue atendido por: " + camarero + "\n").getBytes(charset));

            baos.write(EscPosCommands.CENTER);
            baos.write("\nGRACIAS POR SU VISITA\n".getBytes(charset));

            // más espacio para corte
            baos.write("\n\n\n\n\n".getBytes(charset));
            baos.write(EscPosCommands.CUT);

        } catch (final Exception e) {
            e.printStackTrace();
        }

        return baos.toByteArray();
    }


    private String formatLine(String articulo, String ud, String precio, String importe) {
        final int widthArticulo = 28;
        final int widthUd = 2;
        final int widthPrecio = 8;
        final int widthImporte = 8;

        final String art = this.padRight(articulo, widthArticulo);
        final String u = this.padLeft(ud, widthUd);
        final String p = this.padLeft(precio, widthPrecio);
        final String i = this.padLeft(importe, widthImporte);

        return art + u + p + i + "\n";
    }

    private String formatLineFactura(String articulo, String ud, String precio, String importe) {
        final int widthArticulo = 24;
        final int widthUd = 6;
        final int widthPrecio = 8;
        final int widthImporte = 8;

        final String u = this.padRight(ud, widthUd);
        final String art = this.padLeft(articulo, widthArticulo);
        final String p = this.padLeft(precio, widthPrecio);
        final String i = this.padLeft(importe, widthImporte);

        return u + art + p + i + "\n";
    }

    private String padRight(String text, int length) {
        if (text.length() > length) return text.substring(0, length);
        return String.format("%-" + length + "s", text);
    }

    private String padLeft(String text, int length) {
        if (text.length() > length) return text.substring(0, length);
        return String.format("%" + length + "s", text);
    }

}


