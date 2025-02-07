package com.sofis.data.daos;

import com.sofis.entities.constantes.ConstanteApp;
import com.sofis.entities.constantes.ConstantesEstandares;
import com.sofis.entities.constantes.MensajesNegocio;
import com.sofis.entities.data.Adquisicion;
import com.sofis.entities.data.Pagos;
import com.sofis.entities.tipos.AdqPagosTO;
import com.sofis.exceptions.TechnicalException;
import com.sofis.generico.utils.generalutils.StringsUtils;
import com.sofis.persistence.dao.exceptions.DAOGeneralException;
import com.sofis.persistence.dao.imp.hibernate.HibernateJpaDAOImp;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 *
 * @author Usuario
 */
public class AdquisicionDAO extends HibernateJpaDAOImp<Adquisicion, Integer> implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(AdquisicionDAO.class.getName());

    public AdquisicionDAO(EntityManager em) {
        super(em);
    }

    public List<AdqPagosTO> obtenerAdquisicionPagosList(Integer presupuestoId) throws DAOGeneralException {

        List<AdqPagosTO> resultado = new ArrayList<>();
        List<Adquisicion> adquisiciones = this.findByOneProperty(Adquisicion.class, "adqPreFk.prePk", presupuestoId, "adqPk");
        for (Adquisicion a : adquisiciones) {
            AdqPagosTO adqTO = new AdqPagosTO();
            adqTO.setTipo(1);
            adqTO.setAdqPk(a.getAdqPk());
            adqTO.setMonedaSigno(a.getAdqMoneda().getMonSigno());
            adqTO.setFuenteNombre(a.getAdqFuente().getFueNombre());
            adqTO.setAdqNombre(a.getAdqNombre());
            adqTO.setOrgaNombre(a.getAdqProvOrga() != null ? a.getAdqProvOrga().getOrgaNombre() : null);
            adqTO.setImportePlan(0d);
            adqTO.setImporteReal(0d);
            adqTO.setImporteSaldo(0d);
            
            
            /*
            *   24-05-2018 Nico: Se comenta la parte de manejo de String de los Procedimientos de Compra ya que ahora
            *           se maneja como una entidad nueva.
            */             
            
//            adqTO.setProcCompra(a.getAdqProcCompra());            
            
            if(a.getAdqProcedimientoCompra() != null){
                 adqTO.setProcCompra(a.getAdqProcedimientoCompra().getProcCompNombre());
            }else{
                 adqTO.setProcCompra(null);
            }
            
            
            adqTO.setProcCompraGrp(a.getAdqIdGrpErpFk() != null ? a.getAdqIdGrpErpFk().getIdGrpErpNombre() : null);

            resultado.add(adqTO);

            //procesa los pagos y suma
            SimpleDateFormat sf = new SimpleDateFormat(ConstantesEstandares.CALENDAR_PATTERN);

            List<Pagos> listaPagos = new ArrayList<>(a.getPagosSet());
            Collections.sort(listaPagos, new Comparator<Pagos>() {
                @Override
                public int compare(Pagos o1, Pagos o2) {
                    if (o1 != null && o1.getPagFechaPlanificada() != null && o2 != null && o2.getPagFechaPlanificada() != null) {
                        return o1.getPagFechaPlanificada().compareTo(o2.getPagFechaPlanificada());
                    }
                    return 0;
                }
            });

            for (Pagos p : listaPagos) {
                AdqPagosTO pago = new AdqPagosTO();
                pago.setTipo(2);
                pago.setPagPk(p.getPagPk());
                if (p.getEntregables() != null) {
                    String dateS = "";
                    if (p.getEntregables().getEntFin() != null) {
                        Date d = new Date();
                        d.setTime(p.getEntregables().getEntFin());
                        dateS = StringsUtils.concat(" (", sf.format(d), ")");
                    }

                    pago.setAdqNombre(StringsUtils.concat(p.getEntregables().getEntNombre(), dateS));
                    pago.setAdqPk(p.getPagAdqFk().getAdqPk());
                }
                pago.setImportePlan(p.getPagImportePlanificado());
                pago.setImporteReal(p.getPagImporteReal());
                if (p.getPagImporteReal() != null) {
                    pago.setImporteSaldo(p.getPagImportePlanificado() - p.getPagImporteReal());
                }
                pago.setFechaPlan(p.getPagFechaPlanificada());
                if(pago.getImporteReal() != null){
                    pago.setFechaReal(p.getPagFechaReal());
                }
                if (pago.getImporteReal() != null && pago.getImportePlan() != null) {
                    boolean tienePlan = pago.getImportePlan() != null && pago.getImportePlan() > 0;
                    boolean tieneReal = pago.getImporteReal() != null && pago.getImporteReal() > 0;
                    pago.setEjecucion(tieneReal && tienePlan ? pago.getImporteReal() * 100 / pago.getImportePlan() : 0d);
                }
                if (p.getPagImportePlanificado() != null) {
                    adqTO.setImportePlan(adqTO.getImportePlan() + p.getPagImportePlanificado());
                }

                if (p.getPagImporteReal() != null) {
                    adqTO.setImporteReal(adqTO.getImporteReal() + p.getPagImporteReal());
                }
                if (pago.getImporteSaldo() != null) {
                    adqTO.setImporteSaldo(adqTO.getImporteSaldo() + pago.getImporteSaldo());
                }

                pago.setReferencia(p.getPagTxtReferencia());
                pago.setConfirmado(p.isPagConfirmado());
                //pago.setPagoDoc(p.getDocumento());
                pago.setOrgaNombre(p.getPagProveedorFk() != null ? p.getPagProveedorFk().getOrgaNombre() : null);
                resultado.add(pago);
            }
        }

        return resultado;
    }

    public List<Adquisicion> obtenerAdquisicionPorPre(Integer prePk, Integer monPk) throws DAOGeneralException {
        String query = "SELECT a"
                + " FROM Adquisicion a"
                + " WHERE a.adqPreFk.prePk = :prePk"
                + " AND a.adqMoneda.monPk = :monPk"
                + " ORDER BY a.adqPk";

        Query q = super.getEm().createQuery(query);
        q.setParameter("prePk", prePk);
        q.setParameter("monPk", monPk);

        List<Adquisicion> adqList = q.getResultList();
        return adqList;
    }
    
    public List<Adquisicion> obtenerAdquisicionPorPre(Integer prePk) throws DAOGeneralException {
        List<Adquisicion> adquisiciones = this.findByOneProperty(Adquisicion.class, "adqPreFk.prePk", prePk, "adqPk");
        return adquisiciones;
    }

    public List<Adquisicion> obtenerAdquisicionPorPreProg(Integer progPk) throws DAOGeneralException {
        String query = "SELECT b"
                + " FROM Programas p,"
                + " IN(p.proyectosSet) proy,"
                + " IN(proy.proyPreFk.adquisicionSet) b"
                + " WHERE proy.proyProgFk.progPk = :progPk";

        Query q = super.getEm().createQuery(query);
        q.setParameter("progPk", progPk);

        List<Adquisicion> adqList = q.getResultList();
        return adqList;
    }

    public List<Adquisicion> obtenerAdquisicionPorProy(Integer proyPk) {
        try {
            return this.findByOneProperty(Adquisicion.class, "adqPreFk.proyecto.proyPk", proyPk, "adqPk");
        } catch (DAOGeneralException ex) {
            logger.log(Level.SEVERE, null, ex);
            TechnicalException te = new TechnicalException(ex);
            te.addError(MensajesNegocio.ERROR_ADQISICION_OBTENER);
            throw te;
        }
    }
    
    /**
     * Retorna la suma del costo actual(real) confirmado y según la moneda.
     * @param adqPk
     * @return Double
     */
    public Double obtenerACPorMoneda(Integer adqPk) {
        String query = "SELECT SUM(p.pagImporteReal)"
                + " FROM Adquisicion a, IN(a.pagosSet) p"
                + " WHERE a.adqPk = :adqPk"
                + " AND p.pagConfirmar = :pagConfirmado"
                + " AND p.pagFechaReal <= :fecha";

        Query q = super.getEm().createQuery(query);
        q.setParameter("adqPk", adqPk);
        q.setParameter("pagConfirmado", Boolean.TRUE);
        q.setParameter("fecha", new Date());

        Double reutValue = (Double) q.getSingleResult();
        if (reutValue == null) {
            return 0d;
        }
        return reutValue;
    }

    public Double costoTotal(Integer adqPk) {
        String query = "SELECT SUM(p.pagImportePlanificado)"
                + " FROM Adquisicion a, IN(a.pagosSet) p"
                + " WHERE a.adqPk = :adqPk";

        Query q = super.getEm().createQuery(query);
        q.setParameter("adqPk", adqPk);

        Double reutValue = (Double) q.getSingleResult();
        if (reutValue == null) {
            return 0d;
        }
        return reutValue;
    }
    
    public Pagos obtenerUltimoPago(Integer adqPk){
        String query = "SELECT p"
                + " FROM Adquisicion a JOIN a.pagosSet p"
                + " WHERE a.adqPk = :adqPk"
                + " ORDER BY p.pagPk DESC";
               
        Query q = super.getEm().createQuery(query);
        q.setParameter("adqPk", adqPk);
        
        List<Pagos> retorno = q.getResultList();
        
        Pagos pagoRetorno;
        
        if(!retorno.isEmpty()){
            pagoRetorno = retorno.get(0);
        }else{
            pagoRetorno = new Pagos();
        }
        
        return pagoRetorno;
    }
}
