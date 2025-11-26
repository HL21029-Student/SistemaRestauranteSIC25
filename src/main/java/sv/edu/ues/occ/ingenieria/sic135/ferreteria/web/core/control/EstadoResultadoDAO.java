package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.math.BigDecimal;

import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.dto.EstadoResultadoDTO;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.DetalleLibroMayor;

@Stateless
@LocalBean
public class EstadoResultadoDAO {

    @PersistenceContext(unitName = "FerreteriaPU")
    private EntityManager em;

    //Genera el Estado de Resultados a partir de un Libro Mayor seleccionado

    public EstadoResultadoDTO generarPorLibroMayor(Long idLibroMayor, BigDecimal tasaISR) {

        EstadoResultadoDTO dto = new EstadoResultadoDTO();

        //ingresos
        BigDecimal ingresos = sumPorNombre(idLibroMayor,
                "ingreso", "venta", "ventas"
        );
        dto.setIngresos(ingresos);


         // costo de ventas
        BigDecimal costoVentas = sumPorNombre(idLibroMayor,
                "compra", "compras", "costo", "costos"
        );
        dto.setCostoVentas(costoVentas);

        //utilidad bruta
        dto.setUtilidadBruta(ingresos.subtract(costoVentas));

        //gasto de venta
        BigDecimal gastoVenta = sumPorNombre(idLibroMayor,
                "venta", "vendedor", "publicidad", "promocion",
                "flete", "comision", "empaque", "embalaje"
        );
        dto.setGastoVenta(gastoVenta);

        //gastos administrativos
        BigDecimal gastoAdmin = sumPorNombre(idLibroMayor,
                "administr", "servicio", "servicios", "papeleria",
                "oficina", "depreciacion", "mantenimiento"
        );
        dto.setGastoAdministracion(gastoAdmin);

        //uilidad operativa
        dto.setUtilidadOperativa(
                dto.getUtilidadBruta()
                        .subtract(gastoVenta)
                        .subtract(gastoAdmin)
        );

      //gastos financieros
        BigDecimal gastoFin = sumPorNombre(idLibroMayor,
                "interes", "prestamo", "financ"
        );
        dto.setGastoFinanciero(gastoFin);

       //otros gastos
        BigDecimal otros = sumPorNombre(idLibroMayor,
                "otro", "otros"
        );
        dto.setOtrosGastos(otros);

      //utilidad antes del ISR
        dto.setUtilidadAntesISR(
                dto.getUtilidadOperativa()
                        .subtract(gastoFin)
                        .subtract(otros)
        );

      //ISR
        BigDecimal tasa = (tasaISR == null) ? new BigDecimal("0.25") : tasaISR;

        BigDecimal impuesto = dto.getUtilidadAntesISR()
                .max(BigDecimal.ZERO) // si hay pérdida → no ISR
                .multiply(tasa);

        dto.setImpuestoRenta(impuesto);

       //utilidad neta
        dto.setUtilidadNeta(dto.getUtilidadAntesISR().subtract(impuesto));
        return dto;
    }


    //Suma saldos del Libro Mayor clasificando por palabras del campo nombre_cuenta.
    private BigDecimal sumPorNombre(Long idLibroMayor, String... keywords) {

        var lista = em.createQuery(
                        "SELECT d FROM DetalleLibroMayor d " +
                                "WHERE d.idLibroMayor.id = :idLibroMayor",
                        DetalleLibroMayor.class)
                .setParameter("idLibroMayor", idLibroMayor)
                .getResultList();

        BigDecimal total = BigDecimal.ZERO;

        for (DetalleLibroMayor d : lista) {

            if (d.getNombreCuenta() == null) continue;

            String nombre = d.getNombreCuenta().toLowerCase();

            for (String k : keywords) {
                if (nombre.contains(k.toLowerCase())) {
                    total = total.add(d.getSaldo());
                    break;
                }
            }
        }

        return total;
    }
}
