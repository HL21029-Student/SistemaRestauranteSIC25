package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.boundary.jsf;

import jakarta.faces.view.ViewScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.faces.application.FacesMessage;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.FacturaDAO;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.ClienteDAO;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.ProductoDAO;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.VentaDAO;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.VentaDetalleDAO;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.InventarioDefaultDataAccess;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.Factura;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.Cliente;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.Producto;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.Venta;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.VentaDetalle;

import java.time.ZoneId;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named("facturaFrm")
@ViewScoped
public class FacturaFrm extends DefaultFrm<Factura> implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(FacturaFrm.class.getName());
    private static final BigDecimal IVA_RATE = new BigDecimal("0.13"); // 13% IVA

    @Inject
    private transient FacturaDAO facturaDAO;

    @Inject
    private transient VentaDAO ventaDAO;

    @Inject
    private transient VentaDetalleDAO ventaDetalleDAO;

    @Inject
    private transient ClienteDAO clienteDAO;

    @Inject
    private transient ProductoDAO productoDAO;

    @Inject
    private transient FacesContext facesContext;

    // Propiedades para gestión de factura
    private Cliente clienteSeleccionado;
    private List<ItemFactura> items;
    private ItemFactura itemActual;

    public FacturaFrm() {
        this.nombreBean = "facturaFrm";
        this.items = new ArrayList<>();
    }

    // Clase interna para items de factura
    public static class ItemFactura {
        private Producto producto;
        private BigDecimal cantidad;
        private BigDecimal precio;
        private String observaciones;

        public ItemFactura() {
            this.cantidad = BigDecimal.ONE;
            this.precio = BigDecimal.ZERO;
        }

        public Producto getProducto() { return producto; }
        public void setProducto(Producto producto) {
            this.producto = producto;
            // El precio debe ser ingresado manualmente por el usuario
            // ya que la entidad Producto no almacena precio directamente
        }

        public BigDecimal getCantidad() { return cantidad; }
        public void setCantidad(BigDecimal cantidad) { this.cantidad = cantidad; }

        public BigDecimal getPrecio() { return precio; }
        public void setPrecio(BigDecimal precio) { this.precio = precio; }

        public String getObservaciones() { return observaciones; }
        public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

        public BigDecimal getSubtotal() {
            if (cantidad != null && precio != null) {
                return cantidad.multiply(precio).setScale(2, RoundingMode.HALF_UP);
            }
            return BigDecimal.ZERO;
        }
    }

    @Override
    protected FacesContext getFacesContext() {
        return facesContext;
    }

    @Override
    protected InventarioDefaultDataAccess<Factura, Object> getDao() {
        return facturaDAO;
    }

    @Override
    protected String getIdAsText(Factura r) {
        return r != null && r.getId() != null ? r.getId().toString() : null;
    }

    @Override
    protected Factura getIdByText(String id) {
        if (id == null || id.isBlank()) {
            return null;
        }
        try {
            UUID uuid = UUID.fromString(id);
            return facturaDAO.findById(uuid);
        } catch (IllegalArgumentException ex) {
            LOGGER.log(Level.WARNING, "ID de factura no válido: " + id, ex);
            return null;
        }
    }

    @Override
    protected Factura nuevoRegistro() {
        Factura f = new Factura();
        f.setFechaFactura(java.time.OffsetDateTime.now());
        f.setIva(Boolean.TRUE); // Por defecto aplicar IVA
        f.setSubtotal(BigDecimal.ZERO);
        f.setDescuento(BigDecimal.ZERO);
        f.setTotal(BigDecimal.ZERO);
        f.setEstado("BORRADOR");

        // Limpiar items y cliente
        this.items = new ArrayList<>();
        this.clienteSeleccionado = null;
        this.itemActual = null;

        return f;
    }

    @Override
    public InventarioDefaultDataAccess<Factura, Object> getDataAccess() {
        return facturaDAO;
    }

    @Override
    protected Factura buscarRegistroPorId(Object id) {
        if (id == null) {
            return null;
        }
        try {
            UUID uuid = UUID.fromString(id.toString());
            return facturaDAO.findById(uuid);
        } catch (Exception ex) {
            LOGGER.log(Level.WARNING, "ID inválido para Factura: " + id, ex);
            return null;
        }
    }

    // ========== Métodos de gestión de Cliente ==========

    public void seleccionarCliente(Cliente cliente) {
        this.clienteSeleccionado = cliente;
        enviarMensaje("Cliente seleccionado: " + cliente.getNombre(), FacesMessage.SEVERITY_INFO);
    }

    public List<Cliente> buscarClientes(String query) {
        try {
            return clienteDAO.findRange(0, 20); // Simplificado, puedes mejorar con búsqueda
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error buscando clientes", ex);
            return new ArrayList<>();
        }
    }

    // ========== Métodos de gestión de Items ==========

    public void nuevoItem() {
        this.itemActual = new ItemFactura();
    }

    public void agregarItem() {
        if (itemActual == null || itemActual.getProducto() == null) {
            enviarMensaje("Debe seleccionar un producto", FacesMessage.SEVERITY_WARN);
            return;
        }
        if (itemActual.getCantidad() == null || itemActual.getCantidad().compareTo(BigDecimal.ZERO) <= 0) {
            enviarMensaje("La cantidad debe ser mayor a cero", FacesMessage.SEVERITY_WARN);
            return;
        }

        items.add(itemActual);
        calcularTotales();
        this.itemActual = null;
        enviarMensaje("Producto agregado", FacesMessage.SEVERITY_INFO);
    }

    public void eliminarItem(ItemFactura item) {
        items.remove(item);
        calcularTotales();
        enviarMensaje("Producto eliminado", FacesMessage.SEVERITY_INFO);
    }

    public void seleccionarProducto(Producto producto) {
        if (itemActual == null) {
            itemActual = new ItemFactura();
        }
        itemActual.setProducto(producto);
        enviarMensaje("Producto seleccionado: " + producto.getNombreProducto(), FacesMessage.SEVERITY_INFO);
    }

    public List<Producto> buscarProductos(String query) {
        try {
            return productoDAO.findRange(0, 20); // Simplificado
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error buscando productos", ex);
            return new ArrayList<>();
        }
    }

    // ========== Cálculos ==========

    public void calcularTotales() {
        if (registro == null) return;

        // Calcular subtotal
        BigDecimal subtotal = items.stream()
            .map(ItemFactura::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        registro.setSubtotal(subtotal);

        // Aplicar descuento
        BigDecimal descuento = registro.getDescuento() != null ? registro.getDescuento() : BigDecimal.ZERO;
        BigDecimal subtotalConDescuento = subtotal.subtract(descuento);

        // Calcular IVA
        BigDecimal total;
        if (Boolean.TRUE.equals(registro.getIva())) {
            BigDecimal iva = subtotalConDescuento.multiply(IVA_RATE);
            total = subtotalConDescuento.add(iva);
        } else {
            total = subtotalConDescuento;
        }

        registro.setTotal(total.setScale(2, RoundingMode.HALF_UP));
    }

    public void aplicarDescuento() {
        calcularTotales();
    }

    public void cambiarIVA() {
        calcularTotales();
    }

    public BigDecimal getMontoIVA() {
        if (registro == null || !Boolean.TRUE.equals(registro.getIva())) {
            return BigDecimal.ZERO;
        }
        BigDecimal subtotal = registro.getSubtotal() != null ? registro.getSubtotal() : BigDecimal.ZERO;
        BigDecimal descuento = registro.getDescuento() != null ? registro.getDescuento() : BigDecimal.ZERO;
        return subtotal.subtract(descuento).multiply(IVA_RATE).setScale(2, RoundingMode.HALF_UP);
    }

    // ========== Método de formato de fecha ==========

    /**
     * Formatea una fecha OffsetDateTime en formato legible
     * Ejemplo: 2025-11-13T03:00-06:00 → 13/11/2025 03:00
     */
    public String formatearFecha(OffsetDateTime fecha) {
        if (fecha == null) {
            return "";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return fecha.format(formatter);
    }

    // ========== Método de Guardado Completo (Factura + Venta + Detalles) ==========

    @Override
    public void btnGuardarHandler(jakarta.faces.event.ActionEvent actionEvent) {
        if (this.registro == null) {
            enviarMensaje("No hay registro para guardar", FacesMessage.SEVERITY_ERROR);
            return;
        }

        try {
            // Determinar si es creación o modificación
            boolean esCreacion = (this.estado == ESTADO_CRUD.CREAR);

            if (esCreacion) {
                // ========== CREACIÓN: Guardar Factura + Venta + Detalles ==========

                // 1. Validaciones
                if (clienteSeleccionado == null) {
                    enviarMensaje("Debe seleccionar un cliente", FacesMessage.SEVERITY_WARN);
                    return;
                }

                if (items == null || items.isEmpty()) {
                    enviarMensaje("Debe agregar al menos un producto", FacesMessage.SEVERITY_WARN);
                    return;
                }

                // 2. Generar UUID para la factura si no existe
                if (this.registro.getId() == null) {
                    this.registro.setId(UUID.randomUUID());
                }

                // 3. Crear y guardar la Venta
                Venta venta = new Venta();
                venta.setId(UUID.randomUUID());
                venta.setIdCliente(clienteSeleccionado);

                // Convertir OffsetDateTime a Date
                if (this.registro.getFechaFactura() != null) {
                    Date fechaVenta = Date.from(this.registro.getFechaFactura()
                        .atZoneSameInstant(ZoneId.systemDefault())
                        .toInstant());
                    venta.setFecha(fechaVenta);
                } else {
                    venta.setFecha(new Date());
                }

                venta.setEstado(this.registro.getEstado() != null ? this.registro.getEstado() : "ACTIVA");
                venta.setObservaciones("Venta generada desde factura: " + this.registro.getNumeroFactura());

                // Guardar la venta
                ventaDAO.create(venta);
                LOGGER.log(Level.INFO, "Venta creada con ID: " + venta.getId());

                // 4. Crear y guardar los VentaDetalle
                for (ItemFactura item : items) {
                    VentaDetalle detalle = new VentaDetalle();
                    detalle.setId(UUID.randomUUID());
                    detalle.setIdVenta(venta);
                    detalle.setIdProducto(item.getProducto());
                    detalle.setCantidad(item.getCantidad());
                    detalle.setPrecio(item.getPrecio());
                    detalle.setEstado("ACTIVO");
                    detalle.setObservaciones(item.getObservaciones());

                    ventaDetalleDAO.create(detalle);
                    LOGGER.log(Level.INFO, "VentaDetalle creado para producto: " + item.getProducto().getNombreProducto());
                }

                // 5. Marcar la factura como ACTIVA ya que la venta se completó
                this.registro.setEstado("ACTIVA");

                // 6. Guardar la factura
                facturaDAO.create(this.registro);
                LOGGER.log(Level.INFO, "Factura creada con ID: " + this.registro.getId());

                enviarMensaje("Factura, venta y productos guardados exitosamente", FacesMessage.SEVERITY_INFO);

            } else {
                // ========== MODIFICACIÓN: Solo actualizar la Factura ==========
                facturaDAO.update(this.registro);
                LOGGER.log(Level.INFO, "Factura actualizada con ID: " + this.registro.getId());

                enviarMensaje("Factura actualizada exitosamente", FacesMessage.SEVERITY_INFO);
            }

            // 7. Cambiar estado y limpiar
            this.estado = ESTADO_CRUD.NADA;
            this.registro = null;
            this.items = new ArrayList<>();
            this.clienteSeleccionado = null;

        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error al guardar factura", ex);
            enviarMensaje("Error al guardar: " + ex.getMessage(), FacesMessage.SEVERITY_ERROR);
        }
    }

    // ========== Getters y Setters ==========

    public Cliente getClienteSeleccionado() {
        return clienteSeleccionado;
    }

    public void setClienteSeleccionado(Cliente clienteSeleccionado) {
        this.clienteSeleccionado = clienteSeleccionado;
    }

    public List<ItemFactura> getItems() {
        return items;
    }

    public void setItems(List<ItemFactura> items) {
        this.items = items;
    }

    public ItemFactura getItemActual() {
        return itemActual;
    }

    public void setItemActual(ItemFactura itemActual) {
        this.itemActual = itemActual;
    }
}

