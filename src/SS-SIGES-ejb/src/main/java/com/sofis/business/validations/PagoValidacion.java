package com.sofis.business.validations;

import com.sofis.entities.constantes.ConstanteApp;
import com.sofis.entities.constantes.MensajesNegocio;
import com.sofis.entities.data.Pagos;
import com.sofis.exceptions.BusinessException;
import com.sofis.generico.utils.generalutils.DatesUtils;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Usuario
 */
public class PagoValidacion {

    private static final Logger logger = Logger.getLogger(PagoValidacion.class.getName());

    public static boolean validar(Pagos pago, boolean exigeProveedorEnPago, boolean exigeClienteEnPago) throws BusinessException {
        BusinessException be = new BusinessException();

        if (pago == null) {
            be.addError(MensajesNegocio.ERROR_PAGO_NULL);
        } else {
            if (pago.getEntregables() == null) {
                be.addError(MensajesNegocio.ERROR_PAGO_ENTREGABLE);
            }

            if (pago.getPagFechaPlanificada() == null) {
                be.addError(MensajesNegocio.ERROR_PAGO_FECHA_PLANIFICADA);
            }

            if (pago.getPagImportePlanificado() == null) {
                be.addError(MensajesNegocio.ERROR_PAGO_IMPORTE_PLANIFICADO);
            }

            if (pago.isPagConfirmado()
                    && ((pago.getPagFechaReal() == null || DatesUtils.esMayor(pago.getPagFechaReal(), new Date())) //                    ||
                    //                    (pago.getPagImporteReal() == null || pago.getPagImporteReal() < 0)
                    )) {
                be.addError(MensajesNegocio.ERROR_PAGO_CONFIRMAR_REAL);
            }

            if (pago.getPagTxtReferencia() != null
                    && pago.getPagTxtReferencia().length() > 50) {
                be.addError(MensajesNegocio.ERROR_PAGO_REFERENCIA_LARGO);
            }

            if (exigeProveedorEnPago && Boolean.TRUE.equals(pago.getPagConfirmar()) && pago.getPagProveedorFk() == null) {
                be.addError(MensajesNegocio.ERROR_PAGO_SIN_PROVEEDOR);
            }
            if (exigeClienteEnPago && Boolean.TRUE.equals(pago.getPagConfirmar()) && pago.getPagContrOrganizacionFk() == null) {
                be.addError(MensajesNegocio.ERROR_PAGO_APROB_SIN_CLIENTE);
            }            
        }

        if (be.getErrores().size() > 0) {
            logger.logp(Level.WARNING, PagoValidacion.class.getName(), "validar", be.getErrores().toString(), be);
            throw be;
        }

        return true;
    }
}
