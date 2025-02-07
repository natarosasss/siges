package com.sofis.business.validations;

import com.sofis.entities.constantes.ConstanteApp;
import com.sofis.entities.constantes.MensajesNegocio;
import com.sofis.entities.data.Gastos;
import com.sofis.entities.data.RegistrosHoras;
import com.sofis.exceptions.BusinessException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Usuario
 */
public class GastosValidacion {

    private static final Logger logger = Logger.getLogger(GastosValidacion.class.getName());

    public static boolean validar(Gastos gasto) throws BusinessException {

        BusinessException be = new BusinessException();

        if (gasto == null) {
            be.addError(MensajesNegocio.ERROR_GASTO_NULL);
        } else {
            if (gasto.getGasProyFk() == null
                    || gasto.getGasProyFk().getProyPk() == null
                    || gasto.getGasProyFk().getProyPk().intValue() == 0) {
                be.addError(MensajesNegocio.ERROR_GASTO_PROY);
            }
            if (gasto.getGasTipoFk() == null
                    || gasto.getGasTipoFk().getTipogasPk() == null
                    || gasto.getGasTipoFk().getTipogasPk().intValue() == 0) {
                be.addError(MensajesNegocio.ERROR_GASTO_TIPO_GASTO);
            }
            if (gasto.getGasFecha() == null) {
                be.addError(MensajesNegocio.ERROR_GASTO_FECHA);
            }
            if (gasto.getGasMonFk() == null
                    || gasto.getGasMonFk().getMonPk() == null
                    || gasto.getGasMonFk().getMonPk() == 0) {
                be.addError(MensajesNegocio.ERROR_GASTO_MONEDA);
            }
            if (gasto.getGasImporte() == null
                    || gasto.getGasImporte() <= 0) {
                be.addError(MensajesNegocio.ERROR_GASTO_IMPORTE);
            }
        }

        if (be.getErrores().size() > 0) {
            logger.logp(Level.WARNING, RegistrosHorasValidacion.class.getName(), "validar", be.getErrores().toString(), be);
            throw be;
        }

        return true;
    }
}
