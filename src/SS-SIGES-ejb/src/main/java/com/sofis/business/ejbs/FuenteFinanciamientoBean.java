package com.sofis.business.ejbs;

import com.sofis.business.interceptors.LoggedInterceptor;
import com.sofis.business.validations.FuenteFinanciamientoValidacion;
import com.sofis.data.daos.FuenteFinanciamientoDAO;
import com.sofis.data.utils.DAOUtils;
import com.sofis.entities.constantes.ConstanteApp;
import com.sofis.entities.constantes.MensajesNegocio;
import com.sofis.entities.data.FuenteFinanciamiento;
import com.sofis.exceptions.BusinessException;
import com.sofis.exceptions.GeneralException;
import com.sofis.generico.utils.generalutils.CollectionsUtils;
import com.sofis.generico.utils.generalutils.StringsUtils;
import com.sofis.persistence.dao.exceptions.DAOGeneralException;
import com.sofis.sofisform.to.CriteriaTO;
import com.sofis.sofisform.to.MatchCriteriaTO;
import com.sofis.utils.CriteriaTOUtils;
import java.util.ArrayList;
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
@Stateless(name = "FuenteFinanciamientoBean")
@LocalBean
@Interceptors({ LoggedInterceptor.class })
public class FuenteFinanciamientoBean {

	@PersistenceContext(unitName = ConstanteApp.PERSISTENCE_CONTEXT_UNIT_NAME)
	private EntityManager em;
	private static final Logger logger = Logger.getLogger(FuenteFinanciamientoBean.class.getName());
	@Inject
	private DatosUsuario du;

	// private String usuario;
	// private String origen;

	@PostConstruct
	public void init() {
		// usuario = du.getCodigoUsuario();
		// origen = du.getOrigen();
	}

	/**
	 * Obtener una lista de Fuentes de Financiamiento deacuero al organismo
	 * aportado.
	 *
	 * @param orgId
	 * @return Lista FuenteFinanciamiento
	 * @throws GeneralException
	 */
	public List<FuenteFinanciamiento> obtenerFuentesPorOrgId(Integer orgId) throws GeneralException {
		FuenteFinanciamientoDAO fueDao = new FuenteFinanciamientoDAO(em);
		try {
			// List<FuenteFinanciamiento> result =
			// fueDao.findByOneProperty(FuenteFinanciamiento.class, "fueOrgFk.orgPk",
			// orgId);
			List<FuenteFinanciamiento> result = fueDao.obtenerPorOrganismo(orgId);
			return result;

		} catch (DAOGeneralException ex) {
			logger.log(Level.SEVERE, ex.getMessage(), ex);
			BusinessException be = new BusinessException();
			be.addError(MensajesNegocio.ERROR_FUENTE_FINANC_OBTENER);
			throw be;
		}
	}

	public FuenteFinanciamiento guardarFuente(FuenteFinanciamiento fuenteEnEdicion) {
		if (fuenteEnEdicion != null) {
			FuenteFinanciamientoValidacion.validar(fuenteEnEdicion);
			validarDuplicado(fuenteEnEdicion);

			FuenteFinanciamientoDAO dao = new FuenteFinanciamientoDAO(em);
			try {
				return dao.update(fuenteEnEdicion, du.getCodigoUsuario(), du.getOrigen());
			} catch (DAOGeneralException ex) {
				logger.log(Level.SEVERE, ex.getMessage(), ex);
				BusinessException be = new BusinessException();
				be.addError(MensajesNegocio.ERROR_FUENTE_FINANC_GUARDAR);
				throw be;
			}
		}
		return null;
	}

	public FuenteFinanciamiento obtenerFuentePorPk(Integer fuentePk) {
		FuenteFinanciamientoDAO dao = new FuenteFinanciamientoDAO(em);

		try {
			FuenteFinanciamiento fuente = dao.findById(FuenteFinanciamiento.class, fuentePk);
			return fuente;
		} catch (DAOGeneralException ex) {
			logger.log(Level.SEVERE, ex.getMessage(), ex);
			BusinessException be = new BusinessException();
			be.addError(MensajesNegocio.ERROR_FUENTE_FINANC_OBTENER);
			throw be;
		}
	}

	public List<FuenteFinanciamiento> busquedaFuenteFiltro(Integer orgPk, Map<String, Object> mapFiltro,
			String elementoOrdenacion, int ascendente) {
		if (orgPk != null) {
			List<CriteriaTO> criterios = new ArrayList<>();

			MatchCriteriaTO criterioOrg = CriteriaTOUtils.createMatchCriteriaTO(MatchCriteriaTO.types.EQUALS,
					"fueOrgFk.orgPk", orgPk);
			criterios.add(criterioOrg);

			String nombre = mapFiltro != null ? (String) mapFiltro.get("nombre") : null;
			if (!StringsUtils.isEmpty(nombre)) {

				criterios.add(DAOUtils.createMatchCriteriaTOString("fueNombre", nombre));
			}

			if (mapFiltro.get("habilitada") != null) {
				criterios.add(CriteriaTOUtils.createMatchCriteriaTO(MatchCriteriaTO.types.EQUALS, "fueHabilitada", (Boolean) mapFiltro.get("habilitada"))); 
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
				orderBy = new String[] { elementoOrdenacion };
				asc = new boolean[] { (ascendente == 1) };
			}

			FuenteFinanciamientoDAO dao = new FuenteFinanciamientoDAO(em);

			try {
				return dao.findEntityByCriteria(FuenteFinanciamiento.class, condicion, orderBy, asc, null, null);
			} catch (DAOGeneralException ex) {
				logger.log(Level.SEVERE, null, ex);
				BusinessException be = new BusinessException();
				be.addError(MensajesNegocio.ERROR_FUENTE_FINANC_OBTENER);
				throw be;
			}
		}
		return null;
	}

	public void eliminarFuente(Integer fuentePk) {
		if (fuentePk != null) {
			FuenteFinanciamientoDAO dao = new FuenteFinanciamientoDAO(em);
			try {
				FuenteFinanciamiento f = obtenerFuentePorPk(fuentePk);
				dao.delete(f);
			} catch (DAOGeneralException ex) {
				logger.log(Level.SEVERE, null, ex);

				BusinessException be = new BusinessException();
				be.addError(MensajesNegocio.ERROR_FUENTE_FINANC_ELIMINAR);
				if (ex.getCause() instanceof javax.persistence.PersistenceException
						&& ex.getCause().getCause() instanceof org.hibernate.exception.ConstraintViolationException) {
					be.addError(MensajesNegocio.ERROR_FUENTE_FINANC_CONST_VIOLATION);
				}
				throw be;
			}
		}
	}

	private void validarDuplicado(FuenteFinanciamiento fuente) {
		Map<String, Object> filtroMap = new HashMap<>();
		filtroMap.put("nombre", fuente.getFueNombre());
		List<FuenteFinanciamiento> list = busquedaFuenteFiltro(fuente.getFueOrgFk().getOrgPk(), filtroMap, null, 0);
		if (CollectionsUtils.isNotEmpty(list)) {
			for (FuenteFinanciamiento f : list) {
				if (!f.getFuePk().equals(fuente.getFuePk()) && f.getFueNombre().equals(fuente.getFueNombre())) {
					BusinessException be = new BusinessException();
					be.addError(MensajesNegocio.ERROR_FUENTE_FINANC_NOMBRE_DUPLICADO);
					throw be;
				}
			}
		}
	}
}
