package com.sofis.business.ejbs;

import com.sofis.business.interceptors.LoggedInterceptor;
import com.sofis.business.utils.DatosAuditoria;
import com.sofis.business.validations.PgeConfiguracionValidacion;
import com.sofis.data.daos.PgeConfiguracionDAO;
import com.sofis.entities.constantes.ConstanteApp;
import com.sofis.entities.constantes.ConstantesErrores;
import com.sofis.entities.constantes.ConstantesEstandares;
import com.sofis.entities.data.PgeConfiguraciones;
import com.sofis.exceptions.BusinessException;
import com.sofis.exceptions.GeneralException;
import com.sofis.exceptions.TechnicalException;
import com.sofis.persistence.dao.exceptions.DAOGeneralException;
import com.sofis.sofisform.to.CriteriaTO;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Usuario
 */
@Named
@Stateless(name = "PgeConfiguracionBean")
@LocalBean
@Interceptors({LoggedInterceptor.class})
public class PgeConfiguracionBean {

    @PersistenceContext(unitName = ConstanteApp.PERSISTENCE_CONTEXT_UNIT_NAME)
    private EntityManager em;
    @Inject
    private DatosUsuario du;
    @Inject
    private ConsultaHistorico<PgeConfiguraciones> ch;
    private static final Logger logger
            = Logger.getLogger(PgeConfiguracionBean.class.getName());

    
    //private String usuario;
    //private String origen;
    
    @PostConstruct
    public void init(){
        //usuario = du.getCodigoUsuario();
        //origen = du.getOrigen();
    }
    
    /**
     * Este método guarda un elemento de tipo PgeConfiguraciones. Se aplica para
     * la creación de la entidad y para la modificación de una entidad
     * existente.
     *
     * @param cnf
     * @throws GeneralException Devuelve los códigos de error de la validación
     */
    public PgeConfiguraciones guardar(PgeConfiguraciones pcn) throws GeneralException {
        logger.log(Level.INFO, this.getClass().getName(), pcn);
        try {
            //Validar el elemento a guardar. Si no valida, se lanza una excepcion
            if (PgeConfiguracionValidacion.validar(pcn)) {
                DatosAuditoria da = new DatosAuditoria();
                logger.log(Level.INFO, "codigo usuario={0}, origen={1}", new Object[]{du.getCodigoUsuario(),du.getOrigen()});
                pcn = da.registrarDatosAuditoria(pcn, du.getCodigoUsuario(),du.getOrigen());
                PgeConfiguracionDAO njuDao = new PgeConfiguracionDAO(em);
                if (pcn.getCnfId() == null) {
                    pcn = njuDao.create(pcn, du.getCodigoUsuario(),du.getOrigen());
                } else {
                    //Si el dato ya fue guardado, se determina que haya cambiado alguno de los valores.
                    //En caso contrario no se guarda
                    //logger.info("Version = "+pcn.getCnfVersion()+","+pcn.getCnfId());
                    PgeConfiguraciones valorAnterior = null;
                    try {
                        valorAnterior = ch.obtenerEnVersion(pcn.getClass(), pcn.getCnfVersion(), pcn.getCnfId().intValue(), "cnfVersion");
                    } catch (Exception ex) {
                        //No hay valor en la auditoria
                        //logger.info("**************************************************");
                        ex.printStackTrace();
                        //logger.info("**************************************************");
                    }

                    if (valorAnterior == null || !PgeConfiguracionValidacion.compararParaGrabar(valorAnterior, pcn)) {
                        pcn = njuDao.update(pcn, du.getCodigoUsuario(),du.getOrigen());
                    }
                }
            }
            return pcn;
        } catch (BusinessException be) {
            //Si es de tipo negocio envía la misma excepción
            be.printStackTrace();
            throw be;
        } catch (Exception ex) {
            //Las demás excepciones se consideran técnicas
            ex.printStackTrace();
            logger.log(Level.SEVERE, ex.getMessage(), ex);
            TechnicalException ge = new TechnicalException(ex);
            ge.addError(ex.getMessage());
            throw ge;
        }

    }

    /**
     * Devuelve el elemento configuracion por el ID
     *
     * @param id
     * @return
     * @throws GeneralException
     */
    public PgeConfiguraciones obtenerPgeConfiguracionesPorId(Integer id) throws GeneralException {
        logger.log(Level.INFO, this.getClass().getName(), id);
        PgeConfiguracionDAO tpdDao = new PgeConfiguracionDAO(em);
        try {
            return tpdDao.findById(PgeConfiguraciones.class, id);
        } catch (DAOGeneralException ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
            TechnicalException te = new TechnicalException(ex);
            te.addError(ex.getMessage());
            throw te;
        }
    }

    /**
     * Devuelve el elemento de configuracion según el código Si no hay ningún
     * elemento con ese código devuevle null
     *
     * @param codigo
     * @return
     * @throws GeneralException
     */
    public PgeConfiguraciones obtenerPgeConfiguracionesPorCodigo(String codigo) throws GeneralException {
        logger.log(Level.INFO, this.getClass().getName(), codigo);
        PgeConfiguracionDAO cnfDao = new PgeConfiguracionDAO(em);
        try {
            List<PgeConfiguraciones> resultado = cnfDao.findByOneProperty(PgeConfiguraciones.class, "pcnCodigo", codigo);
            if (resultado.size() == 1) {
                return resultado.get(0);
            } else if (resultado.isEmpty()) {
                return null;
            } else {
                BusinessException be = new BusinessException();
                be.addError(ConstantesErrores.ERROR_DEMASIADOS_RESULTADOS);
                throw be;
            }
        } catch (DAOGeneralException ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
            TechnicalException te = new TechnicalException(ex);
            te.addError(ex.getMessage());
            throw te;
        }
    }

    /**
     * Elimina un elemento según su id
     *
     * @return
     * @throws GeneralException
     */
    public void eliminar(Integer id) throws GeneralException {
        logger.log(Level.INFO, this.getClass().getName(), "");

        PgeConfiguracionDAO cnfDao = new PgeConfiguracionDAO(em);
        try {
            PgeConfiguraciones pge = cnfDao.findById(PgeConfiguraciones.class, id);
            em.remove(pge);
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
            TechnicalException te = new TechnicalException(ex);
            te.addError(ex.getMessage());
            throw te;
        }
    }

    /**
     * Devuelve todos los elementos de tipo configuracion
     *
     * @return
     * @throws GeneralException
     */
    public List<PgeConfiguraciones> obtenerTodos() throws GeneralException {
        logger.log(Level.INFO, this.getClass().getName(), "");

        PgeConfiguracionDAO cnfDao = new PgeConfiguracionDAO(em);
        try {
            return cnfDao.findAll(PgeConfiguraciones.class);
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
            TechnicalException te = new TechnicalException(ex);
            te.addError(ex.getMessage());
            throw te;
        }
    }

    /**
     * Devuelve los elementos que satisfacen el criterio ingresado
     *
     * @param cto
     * @param orderBy
     * @param ascending
     * @param startPosition
     * @param cantidad
     * @return
     * @throws GeneralException
     */
    public List<PgeConfiguraciones> obtenerPorCriteria(CriteriaTO cto, String[] orderBy, boolean[] ascending, Long startPosition, Long cantidad) throws GeneralException {
        logger.log(Level.INFO, this.getClass().getName(), "");

        PgeConfiguracionDAO cnfDao = new PgeConfiguracionDAO(em);
        try {
            return cnfDao.findEntityByCriteria(PgeConfiguraciones.class, cto, orderBy, ascending, startPosition, cantidad);
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
            TechnicalException te = new TechnicalException(ex);
            te.addError(ex.getMessage());
            throw te;
        }
    }
}
