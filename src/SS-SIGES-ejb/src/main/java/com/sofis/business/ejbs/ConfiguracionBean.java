package com.sofis.business.ejbs;

import com.sofis.business.interceptors.LoggedInterceptor;
import com.sofis.business.validations.ConfiguracionValidacion;
import com.sofis.data.daos.ConfiguracionDAO;
import com.sofis.data.utils.DAOUtils;
import com.sofis.entities.codigueras.ConfiguracionCodigos;
import com.sofis.entities.constantes.ConstanteApp;
import com.sofis.entities.constantes.MensajesNegocio;
import com.sofis.entities.data.Configuracion;
import com.sofis.entities.data.Organismos;
import com.sofis.exceptions.BusinessException;
import com.sofis.exceptions.GeneralException;
import com.sofis.exceptions.TechnicalException;
import com.sofis.generico.utils.generalutils.StringsUtils;
import com.sofis.persistence.dao.exceptions.DAOGeneralException;
import com.sofis.sofisform.to.CriteriaTO;
import com.sofis.sofisform.to.MatchCriteriaTO;
import com.sofis.utils.CriteriaTOUtils;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
@Stateless(name = "ConfiguracionBean")
@LocalBean
@Interceptors({LoggedInterceptor.class})
public class ConfiguracionBean {

    @PersistenceContext(unitName = ConstanteApp.PERSISTENCE_CONTEXT_UNIT_NAME)
    private EntityManager em;
    private static final Logger logger = Logger.getLogger(ConstanteApp.LOGGER_NAME);

    @Inject
    private DatosUsuario du;
    @Inject
    private ConsultaHistorico<Configuracion> ch;
    @Inject
    private OrganismoBean organismoBean;

//    private static final Object CONF_ORG_MAP_MUTEX = new Object();
//    private static final Object CONF_MAP_MUTEX = new Object();
//    
//    private static Map<Integer, Map<String, Configuracion>> CONF_ORG_MAP;
//    private static Map<String, Configuracion> CONF_MAP;

    //private String usuario;
    //private String origen;

    @PostConstruct
    public void init() {
        //usuario = du.getCodigoUsuario();
        //origen = du.getOrigen();
    }

    /**
     * Este método guarda un elemento de tipo Configuracion. Se aplica para la
     * creación de la entidad y para la modificación de una entidad existente.
     *
     * @param cnf
     * @return Configuracion
     * @throws GeneralException Devuelve los códigos de error de la validación
     */
    public Configuracion guardar(Configuracion cnf) throws GeneralException {
        logger.log(Level.SEVERE, "guardar");
        try {
            //Validar el elemento a guardar. Si no valida, se lanza una excepcion
            if (ConfiguracionValidacion.validar(cnf)) {
                ConfiguracionDAO cnfDao = new ConfiguracionDAO(em);
                if (cnf.getId() == null) {
                    Integer orgPk = cnf.getCnfOrgFk() != null ? cnf.getCnfOrgFk().getOrgPk() : null;
                    Configuracion cnfPersit = obtenerCnfPorCodigoYOrg(cnf.getCnfCodigo(), orgPk);
                    if (cnfPersit != null) {
                        BusinessException be = new BusinessException();
                        be.addError(MensajesNegocio.ERROR_CONFIG_COD_DUPLICADO);
                        throw be;
                    }
                    cnf = cnfDao.create(cnf, du.getCodigoUsuario(),du.getOrigen());

                } else {
                    //Si el dato ya fue guardado, se determina que haya cambiado alguno de los valores.
                    //En caso contrario no se guarda
                    // BRUNO 12-04-2017: se comenta este control
//                    Configuracion valorAnterior = ch.obtenerConfiguracionEnVersion(cnf.getCnfVersion(), cnf.getId());
//                    if (valorAnterior == null || !ConfiguracionValidacion.compararParaGrabar(valorAnterior, cnf)) {
                        cnf = cnfDao.update(cnf, du.getCodigoUsuario(),du.getOrigen());
//                    }
                }
            }
            return cnf;
        } catch (BusinessException be) {
            //Si es de tipo negocio envía la misma excepción
            logger.log(Level.SEVERE, be.getMessage(), be);
            throw be;
        } catch (Exception ex) {
            //Las demás excepciones se consideran técnicas
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
    public Configuracion obtenerCnfPorId(Integer id) throws GeneralException {
        ConfiguracionDAO cnfDao = new ConfiguracionDAO(em);
        try {
            return cnfDao.findById(Configuracion.class, id);
        } catch (DAOGeneralException ex) {
//           logger.log(Level.SEVERE, ex.getMessage() , ex);
            TechnicalException te = new TechnicalException(ex);
            te.addError(ex.getMessage());
            throw te;
        }
    }

    public Map<String, Configuracion> obtenerCnfPorCodigoYOrg(Integer orgPk, String... codigo) throws GeneralException {
        if (orgPk != null && codigo != null && codigo.length > 0) {
            ConfiguracionDAO dao = new ConfiguracionDAO(em);
            return dao.obtenerCnfPorCodigoYOrg(orgPk, codigo);
        }
        return null;
    }

    public Map<String, Configuracion> obtenerCnfPorCodigoYOrg(Integer orgPk, Map<String, Configuracion> confs, String... codigo) throws GeneralException {
        if (orgPk != null && codigo != null && codigo.length > 0) {
            if (confs == null) {
                confs = new HashMap<>();
            }

            for (String cod : codigo) {
                if (!confs.containsKey(cod)) {
                    confs.put(cod, obtenerCnfPorCodigoYOrg(cod, orgPk));
                }
            }
            return confs;
        }
        return null;
    }

    public String obtenerCnfValorPorCodigo(String codigo, Integer orgPk) {
        Configuracion conf = obtenerCnfPorCodigoYOrg(codigo, orgPk);
        return conf != null ? conf.getCnfValor() : "";
    }

    public Configuracion obtenerCnfPorCodigoYOrg(String codigo, Integer orgPk) throws GeneralException {
        if (!StringsUtils.isEmpty(codigo)) {
            ConfiguracionDAO dao = new ConfiguracionDAO(em);
            try {
                CriteriaTO criteria;
                CriteriaTO criteriaCod = CriteriaTOUtils.createMatchCriteriaTO(MatchCriteriaTO.types.EQUALS, "cnfCodigo", codigo);
                if (orgPk != null) {
                    CriteriaTO criteriaOrg = CriteriaTOUtils.createMatchCriteriaTO(MatchCriteriaTO.types.EQUALS, "cnfOrgFk.orgPk", orgPk);
                    criteria = CriteriaTOUtils.createANDTO(criteriaOrg, criteriaCod);
                } else {
                    CriteriaTO criteriaOrg = CriteriaTOUtils.createMatchCriteriaTO(MatchCriteriaTO.types.NULL, "cnfOrgFk", 1);
                    criteria = CriteriaTOUtils.createANDTO(criteriaOrg, criteriaCod);
                }

                List<Configuracion> resultado = dao.findEntityByCriteria(Configuracion.class, criteria, new String[]{}, new boolean[]{}, null, null);
                return (Configuracion) DAOUtils.obtenerSingleResult(resultado);
            } catch (DAOGeneralException ex) {
                logger.log(Level.SEVERE, ex.getMessage(), ex);
                TechnicalException te = new TechnicalException(ex);
                te.addError(ex.getMessage());
                throw te;
            }
        }
        return null;
    }

    /**
     * Devuelve todos los elementos de tipo configuracion
     *
     * @return
     * @throws GeneralException
     */
    @Deprecated
    public List<Configuracion> obtenerTodos() throws GeneralException {
        ConfiguracionDAO cnfDao = new ConfiguracionDAO(em);
        try {
            return cnfDao.findAll(Configuracion.class);
        } catch (Exception ex) {
//            logger.log(Level.SEVERE, ex.getMessage() , ex);
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
    public List<Configuracion> obtenerPorCriteria(CriteriaTO cto, String[] orderBy, boolean[] ascending, Long startPosition, Long cantidad) throws GeneralException {
        ConfiguracionDAO cnfDao = new ConfiguracionDAO(em);
        try {
            return cnfDao.findEntityByCriteria(Configuracion.class, cto, orderBy, ascending, startPosition, cantidad);
        } catch (Exception ex) {
//            logger.log(Level.SEVERE, ex.getMessage() , ex);
            TechnicalException te = new TechnicalException(ex);
            te.addError(ex.getMessage());
            throw te;
        }
    }

    public List<Configuracion> obtenerTodosPorOrg(Integer orgPk) {
        ConfiguracionDAO cnfDao = new ConfiguracionDAO(em);
        try {
            if (orgPk != null) {
                return cnfDao.findByOneProperty(Configuracion.class, "cnfOrgFk.orgPk", orgPk);
            } else {
                return cnfDao.obtenerConfSinOrg();
            }
        } catch (DAOGeneralException ex) {
            Logger.getLogger(ConfiguracionBean.class.getName()).log(Level.SEVERE, null, ex);
            BusinessException be = new BusinessException();
            be.addError(MensajesNegocio.ERROR_CONFIG_OBTENER);
            throw be;
        }
    }

    public void controlarCnfFaltantes() {
        List<Organismos> organismos = organismoBean.obtenerTodos();
        if (organismos != null) {
            for (Organismos org : organismos) {
                List<Configuracion> confList = obtenerTodosPorOrg(org.getOrgPk());
                Map<String, Configuracion> confMap = new HashMap<>();
                if (confList != null) {
                    for (Configuracion conf : confList) {
                        confMap.put(conf.getCnfCodigo(), conf);
                    }
                }

                Configuracion[] confArr = new Configuracion[]{
                    //                new Configuracion(ConfiguracionCodigos.CURRENT_LOCALE_LANGUAGE, "", "es", org, new Date(), 1),
                    //                new Configuracion(ConfiguracionCodigos.CURRENT_LOCALE_COUNTRY, "", "ES", org, new Date(), 1),
                    new Configuracion(ConfiguracionCodigos.FILTRO_INICIO_POR_AREAS, "Agrupar resultado incio por areas", "false", org, new Date(), 1),
                    new Configuracion(ConfiguracionCodigos.RIESGO_INDICE_LIMITE_AMARILLO, "", "1.2", org, new Date(), 1),
                    new Configuracion(ConfiguracionCodigos.RIESGO_INDICE_LIMITE_ROJO, "", "4", org, new Date(), 1),
                    new Configuracion(ConfiguracionCodigos.RIESGO_TIEMPO_LIMITE_AMARILLO, "", "10", org, new Date(), 1),
                    new Configuracion(ConfiguracionCodigos.RIESGO_TIEMPO_LIMITE_ROJO, "", "20", org, new Date(), 1),
                    new Configuracion(ConfiguracionCodigos.DOCUMENTO_PORCENTAJE_LIMITE_AMARILLO, "", "30", org, new Date(), 1),
                    new Configuracion(ConfiguracionCodigos.DOCUMENTO_PORCENTAJE_LIMITE_ROJO, "", "70", org, new Date(), 1),
                    new Configuracion(ConfiguracionCodigos.TAMANIO_MAX_ARCHIVO_DOCUMENTO, "", "10485760", org, new Date(), 1),
                    new Configuracion(ConfiguracionCodigos.TAMANIO_MAX_ARCHIVO_MULTIMEDIA, "", "512000", org, new Date(), 1),
                    new Configuracion(ConfiguracionCodigos.COSTO_ACTUAL_LIMITE_AMARILLO, "", "10", org, new Date(), 1),
                    new Configuracion(ConfiguracionCodigos.COSTO_ACTUAL_LIMITE_ROJO, "", "20", org, new Date(), 1),
                    new Configuracion(ConfiguracionCodigos.ESTADO_INICIO_LIMITE_AMARILLO, "Semaforo estado Inicio amarillo", "10", org, new Date(), 1),
                    new Configuracion(ConfiguracionCodigos.ESTADO_INICIO_LIMITE_ROJO, "Semaforo estado Inicio rojo", "15", org, new Date(), 1),
                    new Configuracion(ConfiguracionCodigos.ESTADO_PLANIFICACION_LIMITE_AMARILLO, "Semaforo estado Planificacion amarillo", "15", org, new Date(), 1),
                    new Configuracion(ConfiguracionCodigos.ESTADO_PLANIFICACION_LIMITE_ROJO, "Semaforo estado Planificacion rojo", "20", org, new Date(), 1),
                    new Configuracion(ConfiguracionCodigos.TAMANIO_MAX_LOGO_ORGANISMO, "Tamaño máximo en bytes del logo del Organismo", "262144", org, new Date(), 1),
                    new Configuracion(ConfiguracionCodigos.MAIL_FROM, "Dirección desde donde se envían los mails", "mail@agesic.gub.uy", org, new Date(), 1),
                    new Configuracion(ConfiguracionCodigos.MAIL_TLS, "Configuración TLS en envío de mail", "false", org, new Date(), 1),
                    new Configuracion(ConfiguracionCodigos.MAIL_DEBUG, "Debug del envío de mail", "false", org, new Date(), 1),
                    new Configuracion(ConfiguracionCodigos.MAIL_ENCODING, "Encoding de los mails a enviar", "utf8", org, new Date(), 1),
                    new Configuracion(ConfiguracionCodigos.CON_CORREO, "Si se envía correo o no", "true", org, new Date(), 1),
                    new Configuracion(ConfiguracionCodigos.CON_CONTROL_ACCESO, "Si se usa el control de acceso de Agesic o no", "false", org, new Date(), 1),
                    new Configuracion(ConfiguracionCodigos.PRODUCTO_INDICE_LIMITE_AMARILLO, "Limite semaforo amarillo para Productos", "10", org, new Date(), 1),
                    new Configuracion(ConfiguracionCodigos.PRODUCTO_INDICE_LIMITE_ROJO, "Limite semaforo rojo para Productos", "20", org, new Date(), 1),
                    new Configuracion(ConfiguracionCodigos.ALCANCE_INDICE_LIMITE_AMARILLO, "Limite semaforo amarillo para Alcance", "90", org, new Date(), 1),
                    new Configuracion(ConfiguracionCodigos.ALCANCE_INDICE_LIMITE_ROJO, "Limite semaforo rojo para Alcance", "70", org, new Date(), 1),
                    new Configuracion(ConfiguracionCodigos.ADJUNTO_MODIFICA_PRESUPUESTO, "Adjunto puede modificar presupuesto", "false", org, new Date(), 1),
                    new Configuracion(ConfiguracionCodigos.ENVIO_NOTIFICACIONES, "Determina si se envían notificaciones", "false", org, new Date(), 1),
                    new Configuracion(ConfiguracionCodigos.CALIDAD_LIMITE_AMARILLO, "Semaforo limite amarillo calidad", "70", org, new Date(), 1),
                    new Configuracion(ConfiguracionCodigos.CALIDAD_LIMITE_ROJO, "Semaforo limite rojo calidad", "30", org, new Date(), 1),
                    new Configuracion(ConfiguracionCodigos.CALIDAD_GERENTE_MODIFICA, "Permitir al Gerente modificar items calidad", "false", org, new Date(), 1),
                    new Configuracion(ConfiguracionCodigos.FOLDER_MEDIA, "Carpeta donde se almacenan los archivos Multimedia", "/srv/siges/media_files/", org, new Date(), 1),
                    new Configuracion(ConfiguracionCodigos.VISUALIZADOR_PUBLICARSERVICIO_SOAPACTION, "PGE SOAP Action", "http://127.0.0.1:8080/SigesVisualizadorPrivado/PublicarProyecto", org, new Date(), 1),
                    new Configuracion(ConfiguracionCodigos.VISUALIZADOR_PUBLICARSERVICIO_WSDL, "PGE WSDL", "http://127.0.0.1:8080/SigesVisualizadorPrivado/PublicarProyecto?WSDL", org, new Date(), 1),
                    new Configuracion(ConfiguracionCodigos.VISUALIZADOR_EXPORTACION_POR_PGE, "Realizar la exportación al Visualizador por la PGE", "false", org, new Date(), 1),
                    new Configuracion(ConfiguracionCodigos.PROYECTO_GANTT_PERIODO, "true/false: habilitar configuración de período de proyecto en entregables", "false", org, new Date(), 1)
                };

                for (Configuracion conf : confArr) {
                    if (!confMap.containsKey(conf.getCnfCodigo())) {
                        conf.setCnfOrgFk(org);
                        guardar(conf);
                        logger.log(Level.INFO, "Se agregó la configuración '" + conf.getCnfCodigo() + "' para el org " + org.getOrgPk());
                    }
                }
            }

            Configuracion[] confArrGeneral = new Configuracion[]{ //                new Configuracion(ConfiguracionCodigos.VISUALIZADOR_PUBLICARSERVICIO_URLLOGICA, "PGE URL Logica", "x", null, new Date(), 1),
            //                new Configuracion(ConfiguracionCodigos.VISUALIZADOR_PUBLICARSERVICIO_NAMESPACE, "PGE Namespace", "x", null, new Date(), 1),
            //                new Configuracion(ConfiguracionCodigos.VISUALIZADOR_PUBLICARSERVICIO_SERVICENAME, "PGE Service Name", "x", null, new Date(), 1),
            //                new Configuracion(ConfiguracionCodigos.VISUALIZADOR_PUBLICARSERVICIO_ROL, "PGE Rol", "x", null, new Date(), 1),
            //                new Configuracion(ConfiguracionCodigos.STS_URL, "PGE STS URL", "x", null, new Date(), 1),
            //                new Configuracion(ConfiguracionCodigos.STS_POLITICA, "PGE STS Politica", "x", null, new Date(), 1),
            //                new Configuracion(ConfiguracionCodigos.STS_EMISOR, "PGE STS Emisor", "x", null, new Date(), 1),
            //                new Configuracion(ConfiguracionCodigos.KEYSTORE_ORG_PATH, "PGE Keystore Org Path", "x", null, new Date(), 1),
            //                new Configuracion(ConfiguracionCodigos.KEYSTORE_ORG_PASS, "PGE Keystore Org Pass", "x", null, new Date(), 1),
            //                new Configuracion(ConfiguracionCodigos.KEYSTORE_ORG_ALIAS, "PGE Keystore Org Alias", "x", null, new Date(), 1),
            //                new Configuracion(ConfiguracionCodigos.KEYSTORE_SSL_PATH, "PGE Keystore SSL Path", "x", null, new Date(), 1),
            //                new Configuracion(ConfiguracionCodigos.KEYSTORE_SSL_PASS, "PGE Keystore SSL Pass", "x", null, new Date(), 1),
            //                new Configuracion(ConfiguracionCodigos.KEYSTORE_SSL_ALIAS, "PGE Keystore SSL Alias", "x", null, new Date(), 1),
            //                new Configuracion(ConfiguracionCodigos.TRUSTSTORE_SSL_PATH, "PGE Truststore SSL Path", "x", null, new Date(), 1),
            //                new Configuracion(ConfiguracionCodigos.TRUSTSTORE_SSL_PASS, "PGE Truststore SSL Pass", "x", null, new Date(), 1)
            };

            List<Configuracion> confListGeneral = obtenerTodosPorOrg(null);
            Map<String, Configuracion> confMapPGE = new HashMap<>();
            if (confListGeneral != null) {
                for (Configuracion conf : confListGeneral) {
                    confMapPGE.put(conf.getCnfCodigo(), conf);
                }
                for (Configuracion conf : confArrGeneral) {
                    if (!confMapPGE.containsKey(conf.getCnfCodigo())) {
                        guardar(conf);
                        logger.log(Level.INFO, "Se agregó la configuración general '" + conf.getCnfCodigo());
                    }
                }
            }
        }
    }
}