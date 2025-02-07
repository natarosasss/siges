package com.sofis.business.ejbs;

import com.sofis.business.interceptors.LoggedInterceptor;
import com.sofis.business.validations.AreaTematicaValidacion;
import com.sofis.data.daos.AreasTagsDAO;
import com.sofis.data.utils.DAOUtils;
import com.sofis.entities.constantes.ConstanteApp;
import com.sofis.entities.constantes.MensajesNegocio;
import com.sofis.entities.data.AreasTags;
import com.sofis.exceptions.BusinessException;
import com.sofis.exceptions.TechnicalException;
import com.sofis.generico.utils.generalutils.CollectionsUtils;
import com.sofis.generico.utils.generalutils.StringsUtils;
import com.sofis.persistence.dao.exceptions.DAOGeneralException;
import com.sofis.sofisform.to.CriteriaTO;
import com.sofis.sofisform.to.MatchCriteriaTO;
import com.sofis.utils.CriteriaTOUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
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
@Stateless(name = "AreaTematicaBean")
@LocalBean
@Interceptors({LoggedInterceptor.class})
public class AreaTematicaBean {

    @PersistenceContext(unitName = ConstanteApp.PERSISTENCE_CONTEXT_UNIT_NAME)
    private EntityManager em;
    private static final Logger logger = Logger.getLogger(AreaTematicaBean.class.getName());
    @Inject
    private DatosUsuario du;

    //private String usuario;
    //private String origen;
    @PostConstruct
    public void init() {
        //usuario = du.getCodigoUsuario();
        //origen = du.getOrigen();
    }

    public AreasTags guardar(AreasTags at) {
        if (at != null) {
            at.setAreatagExcluyente(Boolean.TRUE);
            at.setAreatagTematica(at.getAreatagNombre());

            AreaTematicaValidacion.validar(at);
            validarDuplicado(at);

            AreasTagsDAO dao = new AreasTagsDAO(em);
            try {
                return dao.update(at, du.getCodigoUsuario(), du.getOrigen());
            } catch (DAOGeneralException ex) {
                logger.log(Level.SEVERE, ex.getMessage(), ex);
                BusinessException be = new BusinessException();
                be.addError(ex.getMessage());
                throw be;
            }
        }
        return null;
    }

    public AreasTags obtenerAreaTemPorPk(Integer atPk) {
        if (atPk != null) {
            AreasTagsDAO dao = new AreasTagsDAO(em);
            try {
                return dao.findById(AreasTags.class, atPk);
            } catch (DAOGeneralException ex) {
                logger.log(Level.SEVERE, null, ex);
                BusinessException be = new BusinessException();
                be.addError(MensajesNegocio.ERROR_AREAS_TEMATICAS_OBTENER);
                throw be;
            }
        }
        return null;
    }

    public List<AreasTags> busquedaAreaTemFiltro(Integer orgPk, String filtroNombre, String elementoOrdenacion, int ascendente) {
        if (orgPk != null) {
            List<CriteriaTO> criterios = new ArrayList<>();

            MatchCriteriaTO criterioOrg = CriteriaTOUtils.createMatchCriteriaTO(
                    MatchCriteriaTO.types.EQUALS, "areatagOrgFk.orgPk", orgPk);
            criterios.add(criterioOrg);

            if (!StringsUtils.isEmpty(filtroNombre)) {
//		MatchCriteriaTO criterio = CriteriaTOUtils.createMatchCriteriaTO(MatchCriteriaTO.types.CONTAINS, "areatagNombre", filtroNombre);
                CriteriaTO criterio = DAOUtils.createMatchCriteriaTOString("areatagNombre", filtroNombre);
                criterios.add(criterio);
            }

            CriteriaTO condicion;
            if (criterios.size() == 1) {
                condicion = criterios.get(0);
            } else {
                condicion = CriteriaTOUtils.createANDTO(criterios.toArray(new CriteriaTO[0]));
            }

            String[] orderBy = {};
            boolean[] asc = {};
            if (!StringsUtils.isEmpty(elementoOrdenacion)) {
                orderBy = new String[]{elementoOrdenacion};
                asc = new boolean[]{(ascendente == 1)};
            }

            AreasTagsDAO dao = new AreasTagsDAO(em);
            try {
                return dao.findEntityByCriteria(AreasTags.class, condicion, orderBy, asc, null, null);
            } catch (DAOGeneralException ex) {
                logger.log(Level.SEVERE, null, ex);
                BusinessException be = new BusinessException();
                be.addError(MensajesNegocio.ERROR_AREAS_TEMATICAS_OBTENER);
                throw be;
            }
        }
        return null;
    }

    public void eliminarAreaTematica(Integer atPk) {
        if (atPk != null) {
            AreasTagsDAO dao = new AreasTagsDAO(em);
            try {
                AreasTags a = obtenerAreaTemPorPk(atPk);
                dao.delete(a);
            } catch (DAOGeneralException ex) {
                logger.log(Level.SEVERE, null, ex);

                BusinessException be = new BusinessException();
                be.addError(MensajesNegocio.ERROR_AREAS_TEMATICAS_ELIMINAR);
                if (ex.getCause() instanceof javax.persistence.PersistenceException
                        && ex.getCause().getCause() instanceof org.hibernate.exception.ConstraintViolationException) {
                    be.addError(MensajesNegocio.ERROR_AREAS_TEMATICAS_CONST_VIOLATION);
                }
                throw be;
            }
        }
    }

    public List<AreasTags> obtenerAreasTematicasPorOrg(int orgPk) {
        AreasTagsDAO areasDao = new AreasTagsDAO(em);
        try {
//            List<AreasTags> resultado = areasDao.findByOneProperty(AreasTags.class, "areatagOrgFk.orgPk", orgPk);
            List<AreasTags> resultado = areasDao.findByOneProperty(AreasTags.class, "areatagOrgFk.orgPk", orgPk, "areatagNombre");
            return resultado;
        } catch (DAOGeneralException ex) {
            logger.log(Level.SEVERE, null, ex);
            TechnicalException te = new TechnicalException(ex);
            te.addError(ex.getMessage());
            throw te;
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public List<AreasTags> obtenerAreasTematicasPorFichaPk(Integer fichaPf, Integer tipoFicha) {
        AreasTagsDAO dao = new AreasTagsDAO(em);
        try {
            List<AreasTags> resultado = dao.obtenerAreasTematicasPorFichaPk(fichaPf, tipoFicha);
            return resultado;
        } catch (DAOGeneralException ex) {
            logger.log(Level.SEVERE, ex.getMessage());
            TechnicalException te = new TechnicalException(ex);
            te.addError(ex.getMessage());
            throw te;
        }
    }

    private void validarDuplicado(AreasTags at) {
        List<AreasTags> list = obtenerAreasPorNombre(at.getAreatagNombre(), at.getAreatagOrgFk().getOrgPk());
        if (CollectionsUtils.isNotEmpty(list)) {
            for (AreasTags areasTags : list) {
                if (!areasTags.getArastagPk().equals(at.getArastagPk())
                        && (areasTags.getAreatagPadreFk() != null && at.getAreatagPadreFk() != null && areasTags.getAreatagPadreFk().getArastagPk() == at.getAreatagPadreFk().getArastagPk())
                        && areasTags.getAreatagNombre().equals(at.getAreatagNombre())) {
                    BusinessException be = new BusinessException();
                    be.addError(MensajesNegocio.ERROR_AREAS_TEMATICAS_NOMBRE_DUPLICADO);
                    throw be;
                }
            }
        }
    }

    public List<AreasTags> obtenerAreasPorNombre(String areaNombre, Integer orgPk) {
        if (orgPk != null) {
            List<CriteriaTO> criterios = new ArrayList<>();

            MatchCriteriaTO criterioOrg = CriteriaTOUtils.createMatchCriteriaTO(
                    MatchCriteriaTO.types.EQUALS, "areatagOrgFk.orgPk", orgPk);
            criterios.add(criterioOrg);

            MatchCriteriaTO criterioNombre = CriteriaTOUtils.createMatchCriteriaTO(MatchCriteriaTO.types.EQUALS, "areatagNombre", areaNombre);
//	    CriteriaTO criterioNombre = DAOUtils.createMatchCriteriaTOString("areatagNombre", areaNombre);
            criterios.add(criterioNombre);

            CriteriaTO criteria = CriteriaTOUtils.createANDTO(criterios.toArray(new CriteriaTO[0]));

            AreasTagsDAO areasDao = new AreasTagsDAO(em);

            try {
                return areasDao.findEntityByCriteria(AreasTags.class, criteria, new String[]{"areatagNombre"}, new boolean[]{true}, null, null);

            } catch (DAOGeneralException ex) {
                logger.log(Level.SEVERE, null, ex);
                BusinessException te = new BusinessException();
                te.addError(MensajesNegocio.ERROR_AREAS_TEMATICAS_OBTENER);
                throw te;
            }
        }
        return null;
    }
}
