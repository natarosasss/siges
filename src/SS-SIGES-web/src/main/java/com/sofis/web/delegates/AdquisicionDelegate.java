package com.sofis.web.delegates;

import com.sofis.business.ejbs.AdquisicionBean;
import com.sofis.entities.data.Adquisicion;
import com.sofis.entities.data.Pagos;
import com.sofis.entities.data.SsUsuario;
import com.sofis.entities.tipos.AdqPagosTO;
import com.sofis.exceptions.GeneralException;
import java.util.List;
import javax.inject.Inject;

/**
 *
 * @author Usuario
 */
public class AdquisicionDelegate {
    
    @Inject
    private AdquisicionBean adquisicionBean;
    
    public Adquisicion guardarAdquisicion(Adquisicion adqisicion, Integer fichaFk, Integer tipoFicha, SsUsuario usuario, Integer orgPk) throws GeneralException {
        return adquisicionBean.guardarAdquisicion(adqisicion, fichaFk, tipoFicha, usuario, orgPk);
    }
    
    public List<AdqPagosTO> obtenerAdquisicionPagosList(Integer presupuestoId) {
        return adquisicionBean.obtenerAdquisicionPagosList(presupuestoId);
    }

    public Adquisicion obtenerAdquisicionPorId(Integer adqPk) {
        return adquisicionBean.obtenerAdquisicionPorId(adqPk);
    }
    
    public List<Adquisicion> obtenerAdquisicionPorPre(Integer prePk) {
        return adquisicionBean.obtenerAdquisicionPorPre(prePk);
    }
    
    public List<Adquisicion> obtenerAdquisicionPorProy(Integer proyPk) {
        return adquisicionBean.obtenerAdquisicionPorProy(proyPk);
    }
    
    public List<Adquisicion> obtenerAdqDevPorProy(Integer proyPk) {
        return adquisicionBean.obtenerAdqDevPorProy(proyPk);
    }

    public void eliminarAdquisicion(Integer adqPk, Integer proyPk, Integer orgPk) {
        adquisicionBean.eliminarAdquisicion(adqPk, proyPk, orgPk);
    }

    public Double costoEjecutado(Integer adqPk) {
        return adquisicionBean.costoEjecutado(adqPk);
    }

    public Double costoTotal(Integer adqPk) {
        return adquisicionBean.costoTotal(adqPk);
    }
       
    public Pagos obtenerUltimoPago(Integer adqPk){
        return adquisicionBean.obtenerUltimoPago(adqPk);
    }
}
