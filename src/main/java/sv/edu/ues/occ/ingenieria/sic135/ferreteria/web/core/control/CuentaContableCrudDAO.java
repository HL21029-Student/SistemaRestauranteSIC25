package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.CuentaContable;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.entity.ManualCuenta;

import java.io.Serializable;
import jakarta.inject.Inject;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
@LocalBean
public class CuentaContableCrudDAO extends InventarioDefaultDataAccess<CuentaContable, Object> implements Serializable {

    @PersistenceContext(unitName = "SistemaRestaurantePU")
    private EntityManager em;

    @Inject
    private CuentaContableDAO cuentaContableDAO;

    @Inject
    private ManualCuentaDAO manualCuentaDAO;

    private static final Logger LOGGER = Logger.getLogger(CuentaContableCrudDAO.class.getName());

    public CuentaContableCrudDAO() {
        super(CuentaContable.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    protected Class<CuentaContable> getEntityClass() {
        return CuentaContable.class;
    }

    @Override
    public void delete(CuentaContable registro) {
        if (registro == null || registro.getId() == null) {
            throw new IllegalArgumentException("Registro nulo o sin id");
        }
        try {
            boolean puede = cuentaContableDAO.puedeSerEliminadaPorId(registro.getId());
            if (!puede) {
                throw new IllegalStateException("La cuenta no puede eliminarse porque tiene dependencias (subcuentas o movimientos). ");
            }
            super.delete(registro);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error intentando eliminar CuentaContable id=" + (registro != null ? registro.getId() : null), ex);
            throw ex;
        }
    }

    @Override
    public void create(final CuentaContable registro) throws IllegalStateException, IllegalArgumentException {
        // Crear la cuenta contable utilizando la lógica base
        super.create(registro);

        // Forzar sincronización para asegurarnos de que se haya generado el ID
        try {
            em.flush();
        } catch (Exception ex) {
            LOGGER.log(Level.WARNING, "No se pudo hacer flush explícito tras crear CuentaContable", ex);
        }

        // Una vez persistida la cuenta, crear automáticamente su manual de cuenta asociado
        try {
            if (registro != null && registro.getId() != null) {
                ManualCuenta existente = manualCuentaDAO.findByCuentaContableId(registro.getId());
                if (existente == null) {
                    ManualCuenta manual = new ManualCuenta();
                    manual.setIdCuentaContable(registro);
                    // Inicializar textos en blanco para que el usuario pueda completarlos luego
                    manual.setFuncionCuenta("");
                    manual.setNaturalezaCuenta("");
                    manual.setEjemploMovimiento("");
                    manualCuentaDAO.create(manual);
                    LOGGER.log(Level.INFO, "ManualCuenta creado automáticamente para CuentaContable id={0}", registro.getId());
                }
            } else {
                LOGGER.log(Level.WARNING, "No se pudo crear ManualCuenta: CuentaContable sin ID después de persistir");
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error creando ManualCuenta para CuentaContable id=" + registro.getId(), ex);
            // No re-lanzamos la excepción para no bloquear la creación de la cuenta contable
        }
    }
}
