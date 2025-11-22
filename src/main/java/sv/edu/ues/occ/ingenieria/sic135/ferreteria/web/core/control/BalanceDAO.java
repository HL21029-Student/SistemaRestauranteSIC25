package sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control;

import jakarta.ejb.Stateless;
import jakarta.persistence.*;
import sv.edu.ues.occ.ingenieria.sic135.ferreteria.web.core.control.dto.BalanceDTO;

import java.util.List;
@SqlResultSetMapping(
        name = "BalanceDTOMapping",
        classes = @ConstructorResult(
                targetClass = BalanceDTO.class,
                columns = {
                        @ColumnResult(name = "id_cuenta_contable", type = Long.class),
                        @ColumnResult(name = "codigo", type = String.class),
                        @ColumnResult(name = "nombre", type = String.class),
                        @ColumnResult(name = "tipo_cuenta", type = String.class),
                        @ColumnResult(name = "saldo", type = Double.class)
                }
        )
)

@Stateless
public class BalanceDAO {

    @PersistenceContext(unitName = "inventarioPU")
    EntityManager em;

    public List<BalanceDTO> obtenerBalance() {
        return em.createNativeQuery("""
        SELECT 
            cc.id_cuenta_contable,
            cc.codigo,
            cc.nombre,
            tc.nombre AS tipo_cuenta,
            COALESCE(SUM(dlm.saldo), 0) AS saldo
        FROM cuenta_contable cc
        LEFT JOIN tipo_cuenta tc 
            ON tc.id_tipo_cuenta = cc.id_tipo_cuenta
        LEFT JOIN libro_mayor lm 
            ON lm.id_cuenta_contable = cc.id_cuenta_contable
        LEFT JOIN detalle_libro_mayor dlm 
            ON dlm.id_libro_mayor = lm.id_libro_mayor
        GROUP BY 
            cc.id_cuenta_contable,
            cc.codigo,
            cc.nombre,
            tc.nombre
        ORDER BY cc.codigo
        """, "BalanceDTOMapping")
                .getResultList();
    }

}
