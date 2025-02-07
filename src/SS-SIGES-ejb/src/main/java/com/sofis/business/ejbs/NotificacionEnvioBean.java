package com.sofis.business.ejbs;

import com.sofis.business.properties.LabelsEJB;
import com.sofis.business.utils.MailsTemplateUtils;
import com.sofis.business.utils.ProgProyUtils;
import com.sofis.data.daos.NotificacionEnvioDAO;
import com.sofis.entities.codigueras.ConfiguracionCodigos;
import com.sofis.entities.codigueras.SsRolCodigos;
import com.sofis.entities.constantes.ConstanteApp;
import com.sofis.entities.constantes.ConstantesEstandares;
import com.sofis.entities.constantes.ConstantesNotificaciones;
import com.sofis.entities.constantes.MailVariables;
import com.sofis.entities.data.Adquisicion;
import com.sofis.entities.data.Configuracion;
import com.sofis.entities.data.Entregables;
import com.sofis.entities.data.Estados;
import com.sofis.entities.data.NotificacionEnvio;
import com.sofis.entities.data.NotificacionInstancia;
import com.sofis.entities.data.Organismos;
import com.sofis.entities.data.Pagos;
import com.sofis.entities.data.Presupuesto;
import com.sofis.entities.data.Proyectos;
import com.sofis.entities.data.SsUsuario;
import com.sofis.entities.utils.SsUsuariosUtils;
import com.sofis.exceptions.MailException;
import com.sofis.generico.utils.generalutils.CollectionsUtils;
import com.sofis.generico.utils.generalutils.DatesUtils;
import com.sofis.generico.utils.generalutils.StringsUtils;
import com.sofis.sofisform.to.CriteriaTO;
import com.sofis.sofisform.to.MatchCriteriaTO;
import com.sofis.utils.CriteriaTOUtils;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.LocalBean;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import org.jboss.ejb3.annotation.TransactionTimeout;

/**
 *
 * @author Usuario
 */
@Named
@Stateless(name = "notificacionEnvioBean")
@LocalBean
public class NotificacionEnvioBean {

	@PersistenceUnit(unitName = ConstanteApp.PERSISTENCE_CONTEXT_UNIT_NAME_NONE_JTA)
	private EntityManagerFactory emNoneJTA;

        private static final Logger logger = Logger.getLogger(NotificacionEnvioBean.class.getName());
	private static final String HORA_EJECUCION = "03";
	private static final String MINUTOS_EJECUCION = "00";
//    private static final long TRANSACTION_TIMEOUT_MINUTES = 360;

	@Inject
	private DatosUsuario du;
	@Inject
	private NotificacionInstanciaBean notificacionInstanciaBean;
	@Inject
	private OrganismoBean organismoBean;
	@Inject
	private ProyectosBean proyectosBean;
	@Inject
	private RiesgosBean riesgosBean;
	@Inject
	private ConfiguracionBean configuracionBean;
	@Inject
	private MailBean mailBean;
	@Inject
	private SsUsuarioBean ssUsuarioBean;
	@Inject
	private EstadosBean estadosBean;
	@Inject
	private PresupuestoBean presupuestoBean;
	@Inject
	private PagosBean pagosBean;
	@Inject
	private AdquisicionBean adquisicionBean;

	@Schedule(hour = HORA_EJECUCION, minute = MINUTOS_EJECUCION)
//    @TransactionTimeout(value = TRANSACTION_TIMEOUT_MINUTES, unit = TimeUnit.MINUTES)
	public void scheduleEnvioNotificaciones() {
		enviarNotificaciones();
	}

//    @TransactionTimeout(value = TRANSACTION_TIMEOUT_MINUTES, unit = TimeUnit.MINUTES)
	public void enviarNotificaciones() {
		List<Organismos> orgList = organismoBean.obtenerTodosActivos();
		Integer orgPk;
		Configuracion cnfEnvioNot;
		Configuracion cnfConMail;
		Map<String, String> confMap;
		Configuracion cConfAmarillo;
		Configuracion cConfRojo;
		Proyectos proy;

		if (CollectionsUtils.isNotEmpty(orgList)) {
			for (Organismos org : orgList) {
				orgPk = org.getOrgPk();

				cnfEnvioNot = configuracionBean.obtenerCnfPorCodigoYOrg(ConfiguracionCodigos.ENVIO_NOTIFICACIONES, orgPk);
				boolean enviar = false;
				if (cnfEnvioNot != null) {
					enviar = Boolean.parseBoolean(cnfEnvioNot.getCnfValor());
				}

				cnfConMail = configuracionBean.obtenerCnfPorCodigoYOrg(ConfiguracionCodigos.CON_CORREO, orgPk);
				boolean conMail = false;
				if (cnfConMail != null) {
					conMail = Boolean.parseBoolean(cnfConMail.getCnfValor());
				}

				if (enviar && conMail) {
					final List<Integer> idsProy = proyectosBean.obtenerIdsProyPorOrgNoFinalizado(orgPk);
					logger.log(Level.INFO, "*** Notificaciones para el org:" + org.getOrgNombre());

					confMap = new HashMap<>();
					cConfAmarillo = configuracionBean.obtenerCnfPorCodigoYOrg(ConfiguracionCodigos.RIESGO_INDICE_LIMITE_AMARILLO, orgPk);
					confMap.put(ConfiguracionCodigos.RIESGO_INDICE_LIMITE_AMARILLO, cConfAmarillo.getCnfValor());
					cConfRojo = configuracionBean.obtenerCnfPorCodigoYOrg(ConfiguracionCodigos.RIESGO_INDICE_LIMITE_ROJO, orgPk);
					confMap.put(ConfiguracionCodigos.RIESGO_INDICE_LIMITE_ROJO, cConfRojo.getCnfValor());

					int count = 0;
					for (Integer proyPk : idsProy) {
						try {
							logger.log(Level.INFO, ++count + ") Proy.:" + proyPk);
							proy = proyectosBean.obtenerProyPorId(proyPk);
							enviarNotificaciones(proy, orgPk, confMap);
						} catch (Exception ex) {
							logger.log(Level.SEVERE, ex.getMessage(), ex);
						}
					}
				}
			}
                        logger.log(Level.INFO, "Fin del envío de mails");
		}

//        Utils.executeGarbageCollector();
	}

	private void enviarNotificaciones(Proyectos proy, Integer orgPk, Map<String, String> confMap) {
		if (proy != null
				&& proy.isActivo()
				&& !proy.isEstadoPendientes()
				&& !proy.isEstado(Estados.ESTADOS.FINALIZADO.estado_id)) {

			try {
				riesgosDesactualizados(proy, orgPk, confMap);
				tiempoEnInicio(proy, orgPk);
				tiempoEnPlanificacion(proy, orgPk);
				actualizacionProyAmarillo(proy, orgPk);
				actualizacionProyRojo(proy, orgPk);
				fechaPagoVencida(proy, orgPk);
				devengadoUltimosDiasMes(proy, orgPk);
				entPagoPrimerAviso(proy, orgPk);
				entPagoSegundoAviso(proy, orgPk);
				entregableVencido(proy, orgPk);
			} catch (Exception e) {
//                Utils.executeGarbageCollector();
				throw e;
//            } finally {
//                Utils.executeGarbageCollector();
			}
		}
	}

	/**
	 * Riesgos 1: Riesgos desactualizados. Se debe disparar cuando se pasa a
	 * amarillo el semáforo de actualización de riesgos (timer)
	 *
	 * @param proy
	 * @param orgPk
	 * @param confMap
	 */
	private void riesgosDesactualizados(Proyectos proy, Integer orgPk, Map<String, String> confMap) {
		String riesgoColor = riesgosBean.obtenerExposicionRiesgoColor(proy.getProyIndices().getProyindRiesgoExpo(), orgPk, confMap);

		if (riesgoColor.equals(ConstantesEstandares.SEMAFORO_AMARILLO)
				|| riesgoColor.equals(ConstantesEstandares.SEMAFORO_ROJO)) {
			enviarNotificacion(ConstantesNotificaciones.NOT_COD_RIESGOS_1, proy, orgPk);
		} else if (riesgoColor.equals(ConstantesEstandares.SEMAFORO_VERDE)) {
			notificacionSuperada(ConstantesNotificaciones.NOT_COD_RIESGOS_1, proy.getProyPk());
		}
	}

	/**
	 * Inicio: Demasiado tiempo en Inicio. Se debe disparar cuando pasa a estar
	 * rojo el semáforo que indica que hace mucho tiempo que está en inicio
	 * (timer)
	 */
	private void tiempoEnInicio(Proyectos proy, Integer orgPk) {
		if (proy.getProyEstFk().isEstado(Estados.ESTADOS.INICIO.estado_id)) {
			String estColor = estadosBean.obtenerEstadoColor(proy.getProyEstFk(), proy.getProyIndices().getProyindPeriodoInicio(), proy.getProyIndices().getProyindPeriodoFin(), null, null, null, null, orgPk);
			if (estColor.equals(ConstantesEstandares.SEMAFORO_ROJO)) {
				enviarNotificacion(ConstantesNotificaciones.NOT_COD_INICIO, proy, orgPk);
			} else if (!estColor.equals(ConstantesEstandares.SEMAFORO_ROJO)) {
				notificacionSuperada(ConstantesNotificaciones.NOT_COD_INICIO, proy.getProyPk());
			}
		} else {
			notificacionSuperada(ConstantesNotificaciones.NOT_COD_INICIO, proy.getProyPk());
		}
	}

	/**
	 * Planificación: Demasiado tiempo en Planificación. Se debe disparar cuando
	 * pasa a estar en rojo el semáforo que indica que hace mucho tiempo que
	 * está en planificación (timer)
	 */
	private void tiempoEnPlanificacion(Proyectos proy, Integer orgPk) {
		if (proy.getProyEstFk().isEstado(Estados.ESTADOS.PLANIFICACION.estado_id)) {
			String estColor = estadosBean.obtenerEstadoColor(proy.getProyEstFk(), proy.getProyIndices().getProyindPeriodoInicio(), proy.getProyIndices().getProyindPeriodoFin(), null, null, null, null, orgPk);
			if (estColor.equals(ConstantesEstandares.SEMAFORO_ROJO)) {
				enviarNotificacion(ConstantesNotificaciones.NOT_COD_PLANIFICACION, proy, orgPk);
			} else if (!estColor.equals(ConstantesEstandares.SEMAFORO_ROJO)) {
				notificacionSuperada(ConstantesNotificaciones.NOT_COD_PLANIFICACION, proy.getProyPk());
			}
		} else {
			notificacionSuperada(ConstantesNotificaciones.NOT_COD_PLANIFICACION, proy.getProyPk());
		}
	}

	/**
	 * Actualización 1: Se debe disparar cuando el semáforo de actualización del
	 * proyecto pasa a estar en amarillo(timer)
	 */
	private void actualizacionProyAmarillo(Proyectos proy, Integer orgPk) {
		String actColor = proyectosBean.obtenerUltimaActualizacionColor(proy.getProyEstFk(), proy.getProyFechaAct(), proy.getProySemaforoAmarillo(), proy.getProySemaforoRojo());

		if (actColor.equals(ConstantesEstandares.SEMAFORO_AMARILLO)) {
			enviarNotificacion(ConstantesNotificaciones.NOT_COD_ACTUALIZACION_1, proy, orgPk);
		} else if (!actColor.equals(ConstantesEstandares.SEMAFORO_AMARILLO)) {
			notificacionSuperada(ConstantesNotificaciones.NOT_COD_ACTUALIZACION_1, proy.getProyPk());
		}
	}

	/**
	 * Actualización 2: Se debe disparar cuando el semáforo de actualización del
	 * proyecto pasa a estar en rojo (timer)
	 */
	private void actualizacionProyRojo(Proyectos proy, Integer orgPk) {
		String actColor = proyectosBean.obtenerUltimaActualizacionColor(proy.getProyEstFk(), proy.getProyFechaAct(), proy.getProySemaforoAmarillo(), proy.getProySemaforoRojo());

		if (actColor.equals(ConstantesEstandares.SEMAFORO_ROJO)) {
			enviarNotificacion(ConstantesNotificaciones.NOT_COD_ACTUALIZACION_2, proy, orgPk);
		} else if (!actColor.equals(ConstantesEstandares.SEMAFORO_ROJO)) {
			notificacionSuperada(ConstantesNotificaciones.NOT_COD_ACTUALIZACION_2, proy.getProyPk());
		}
	}

	/**
	 * Presupuesto 1: Dada la fecha de pago proyectada (real), se debe disparar
	 * cuando esa fecha esté vencida y no ha sido confirmada la factura (las
	 * opciones son confirmarla o cambiar la fecha y/o el importe) (timer)
	 */
	private void fechaPagoVencida(Proyectos proy, Integer orgPk) {
		Presupuesto pre = presupuestoBean.obtenerPresupuestoPorProy(proy.getProyPk());
		if (pre != null && pre.getAdquisicionSet() != null) {
			boolean notificado = false;
			outerloop:
			for (Adquisicion adq : pre.getAdquisicionSet()) {
				if (adq.getPagosSet() != null) {
					for (Pagos pago : adq.getPagosSet()) {
						if (!pago.isPagConfirmado()
								&& DatesUtils.esMayor(new Date(), pago.getPagFechaReal())) {
							enviarNotificacion(ConstantesNotificaciones.NOT_COD_PRESUPUESTO_1, proy, orgPk);
							notificado = true;
							break outerloop;
						}
					}
				}
			}
			if (notificado) {
				notificacionSuperada(ConstantesNotificaciones.NOT_COD_PRESUPUESTO_1, proy.getProyPk());
			}
		}
	}

	/**
	 * Presupuesto 2: Devengado. Durante los últimos 5 días hábiles del mes,
	 * solamente para los proyectos que tienen cargado devengado es necesario
	 * mandar un aviso al para que actualicen el devengado dado que cuando
	 * comience el siguiente mes no se podrá tocar el actual (timer con
	 * repetición) - Este lo vemos bien luego.
	 */
	private void devengadoUltimosDiasMes(Proyectos proy, Integer orgPk) {
		Calendar cal = new GregorianCalendar();
		int ultimoDia = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		cal.set(Calendar.DAY_OF_MONTH, ultimoDia);
		cal.add(Calendar.DAY_OF_MONTH, -5);
		Calendar calNow = new GregorianCalendar();

		if (calNow.after(cal)) {
			List<Adquisicion> listAdq = adquisicionBean.obtenerAdquisicionPorProy(proy.getProyPk());
			boolean hasDevengado = false;
			for (Adquisicion adq : listAdq) {
				if (CollectionsUtils.isNotEmpty(adq.getDevengadoList())) {
					hasDevengado = true;
					break;
				}
			}
			if (hasDevengado) {
				enviarNotificacion(ConstantesNotificaciones.NOT_COD_PRESUPUESTO_2, proy, orgPk);
			}
		} else {
			notificacionSuperada(ConstantesNotificaciones.NOT_COD_PRESUPUESTO_2, proy.getProyPk());
		}
	}

	/**
	 * Presupuesto 3: Cuando un entregable está al 100% y tiene un pago asociado
	 * cuya factura no está confirmada. A los 5 días de suceder esto se podrá
	 * mandar una notificación para que consigan la factura (timer)
	 *
	 * @param proy
	 * @param orgPk
	 */
	private void entPagoPrimerAviso(Proyectos proy, Integer orgPk) {
		List<Pagos> listPagos = pagosBean.obtenerPagosDiasVenc(proy.getProyPk(), 5);
		if (CollectionsUtils.isNotEmpty(listPagos)) {
			enviarNotificacion(ConstantesNotificaciones.NOT_COD_PRESUPUESTO_3, proy, orgPk);
		} else {
			notificacionSuperada(ConstantesNotificaciones.NOT_COD_PRESUPUESTO_3, proy.getProyPk());
		}
	}

	/**
	 * Presupuesto 4: Cuando un entregable está al 100% y tiene un pago asociado
	 * cuya factura no está confirmada. A los 10 días de suceder esto se podrá
	 * mandar una notificación para que consigan la factura (timer) (como la
	 * anterior pero con más días)
	 *
	 * @param proy
	 * @param orgPk
	 */
	private void entPagoSegundoAviso(Proyectos proy, Integer orgPk) {
		List<Pagos> listPagos = pagosBean.obtenerPagosDiasVenc(proy.getProyPk(), 10);
		if (CollectionsUtils.isNotEmpty(listPagos)) {
			enviarNotificacion(ConstantesNotificaciones.NOT_COD_PRESUPUESTO_4, proy, orgPk);
		} else {
			notificacionSuperada(ConstantesNotificaciones.NOT_COD_PRESUPUESTO_4, proy.getProyPk());
		}
	}

	/**
	 * Presupuesto 5: Cuando el gerente de proyecto indica que la nueva fecha
	 * proyectada de la confirmación de una factura es durante la última
	 * quincena del año o al año siguiente se debe disponer la posibilidad de
	 * mandar una notificación (evento)
	 *
	 * @param pago
	 * @param proyPk
	 * @param orgPk
	 */
	public void fechaProyectadaFinAnio(Pagos pago, Integer proyPk, Integer orgPk) {
		Calendar calReal = new GregorianCalendar();
		calReal.setTime(pago.getPagFechaReal());
		Calendar cal = new GregorianCalendar();
		boolean ultimaQuincena = (calReal.after(new GregorianCalendar(cal.get(Calendar.YEAR), 11, 15))
				|| calReal.equals(new GregorianCalendar(cal.get(Calendar.YEAR), 11, 15)))
				&& (calReal.before(new GregorianCalendar(cal.get(Calendar.YEAR), 11, 31))
				|| calReal.equals(new GregorianCalendar(cal.get(Calendar.YEAR), 11, 31)));
		boolean proxAnio = calReal.get(Calendar.YEAR) > cal.get(Calendar.YEAR);

		if (ultimaQuincena || proxAnio) {
			Proyectos proy = proyectosBean.obtenerProyPorId(proyPk);
			enviarNotificacion(ConstantesNotificaciones.NOT_COD_PRESUPUESTO_5, proy, orgPk);
		} else {
			notificacionSuperada(ConstantesNotificaciones.NOT_COD_PRESUPUESTO_5, proyPk);
		}
	}

	/**
	 * Cronograma 1: Cuando un entregable no está al 100% de avance y tiene
	 * fecha de fin anterior al día de hoy se podrá mandar una notificación.
	 * Esto se daría solamente en caso que el proyecto está desactualizado dado
	 * que o bien el entregable está finalizado o debe finalizar con una fecha
	 * mayor o igual a la actual (timer)
	 *
	 * @param proy
	 * @param orgPk
	 */
	private void entregableVencido(Proyectos proy, Integer orgPk) {
		String actColor = proyectosBean.obtenerUltimaActualizacionColor(proy.getProyEstFk(), proy.getProyFechaAct(), proy.getProySemaforoAmarillo(), proy.getProySemaforoRojo());

		if ((actColor.equals(ConstantesEstandares.SEMAFORO_AMARILLO)
				|| actColor.equals(ConstantesEstandares.SEMAFORO_ROJO))
				&& proy.getProyCroFk() != null
				&& CollectionsUtils.isNotEmpty(proy.getProyCroFk().getEntregablesSet())) {
			Date hoy = new Date();
			boolean enviar = false;

			for (Entregables ent : proy.getProyCroFk().getEntregablesSet()) {
				if (ent.getEntProgreso() < 100
						&& DatesUtils.esMayor(hoy, ent.getEntFinDate())) {
					enviar = true;
					break;
				}
			}

			if (enviar) {
				enviarNotificacion(ConstantesNotificaciones.NOT_COD_CRONOGRAMA_1, proy, orgPk);
			} else {
				notificacionSuperada(ConstantesNotificaciones.NOT_COD_CRONOGRAMA_1, proy.getProyPk());
			}
		}
	}

	/**
	 *
	 * @param proy
	 * @param orgPk
	 * @param confMap
	 */
	public void nuevoRiesgoAlto(Proyectos proy, Integer orgPk, Map<String, String> confMap) {
		String riesgoColor = riesgosBean.obtenerExposicionRiesgoColor(proy.getProyIndices().getProyindRiesgoExpo(), orgPk, confMap);

		if (riesgoColor.equals(ConstantesEstandares.SEMAFORO_ROJO)) {
			enviarNotificacion(ConstantesNotificaciones.NOT_COD_RIESGOS_2, proy, orgPk);
		} else if (!riesgoColor.equals(ConstantesEstandares.SEMAFORO_ROJO)) {
			if (!StringsUtils.isEmpty(ConstantesNotificaciones.NOT_COD_RIESGOS_2) && proy.getProyPk() != null) {
				notificacionSuperada(ConstantesNotificaciones.NOT_COD_RIESGOS_2, proy.getProyPk(), true);
			}
		}
	}

	/**
	 * Consulta si la notificación para dicho código y proyecto ya fue enviada.
	 *
	 * @param codNot
	 * @param proyPk
	 * @return boolean true si fue enviada.
	 */
	public boolean fueNotificado(String codNot, Integer proyPk) {
		NotificacionEnvioDAO dao = new NotificacionEnvioDAO(emNoneJTA.createEntityManager());

		CriteriaTO criteriaCod = CriteriaTOUtils.createMatchCriteriaTO(MatchCriteriaTO.types.EQUALS, "notenvNotCod", codNot);
		CriteriaTO criteriaProy = CriteriaTOUtils.createMatchCriteriaTO(MatchCriteriaTO.types.EQUALS, "notenvProyFk", proyPk);
		CriteriaTO criteria = CriteriaTOUtils.createANDTO(criteriaCod, criteriaProy);

		List<NotificacionEnvio> neList = dao.obtenerEntityByCriteria(NotificacionEnvio.class, criteria, new String[]{}, new boolean[]{}, null, null, true);

		return CollectionsUtils.isNotEmpty(neList);
	}

	/**
	 * Envía la notificación según el código si es que aún no ha sido enviada.
	 *
	 * @param codNot
	 * @param proy
	 * @param orgPk
	 */
	public void enviarNotificacion(String codNot, Proyectos proy, Integer orgPk) {
		if (!fueNotificado(codNot, proy.getProyPk())) {
			NotificacionInstancia ni = notificacionInstanciaBean.obtenerNotificacionInstPorCod(codNot, proy.getProyPk(), orgPk);

			if (ni != null) {
				String subject = LabelsEJB.getValue("notif_envio_subjet", orgPk);

				List<SsUsuario> usuariosDest = new ArrayList<>();
				if (ni.getNotinstGerenteAdjunto()) {
					usuariosDest.add(proy.getProyUsrGerenteFk());
					usuariosDest.add(proy.getProyUsrAdjuntoFk());
				}
				if (ni.getNotinstSponsor()) {
					usuariosDest.add(proy.getProyUsrSponsorFk());
				}
				if (ni.getNotinstPmof()) {
					usuariosDest.add(proy.getProyUsrPmofedFk());
				}
				if (ni.getNotinstPmot()) {
					String[] orden = new String[]{"usuPrimerNombre", "usuSegundoNombre", "usuPrimerApellido", "usuSegundoApellido"};
					boolean[] asc = new boolean[]{true, true, true, true};
					usuariosDest.addAll(ssUsuarioBean.obtenerUsuariosPorRol(SsRolCodigos.PMO_TRANSVERSAL, orgPk, orden, asc));
				}
				String[] destinatarios = SsUsuariosUtils.arrayMailsUsuarios(usuariosDest);

				Map<String, String> valores = new HashMap<>();
				valores.put(MailVariables.NOMBRE_PROYECTO, proy.getProyPk() + " - '" + proy.getProyNombre() + "'");
				Organismos org = organismoBean.obtenerOrgPorId(orgPk, false);
				if (org != null) {
					valores.put(MailVariables.ORGANISMO_NOMBRE, org.getOrgNombre());
				}
				
				String urlSistema = configuracionBean.obtenerCnfValorPorCodigo(ConfiguracionCodigos.URL_SISTEMA, null);
				valores.put(MailVariables.URL_SISTEMA, urlSistema);

				String urlProyecto = ProgProyUtils.obtenerURL(urlSistema, proy);
				valores.put(MailVariables.URL_PROYECTO, urlProyecto);
				
				String mensaje = MailsTemplateUtils.instanciarConHashMap(ni.getNotinstNotFk().getNotMsg(), valores);

				final String subjectThread = subject;
				final String[] destinatariosThread = destinatarios;
				final String mensajeThread = mensaje;
				final Integer orgPkThread = orgPk;
				final String codNotThread = codNot;
				final Proyectos proyThread = proy;

				Thread t = new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							mailBean.enviarMail(subjectThread, null, null, null, destinatariosThread, mensajeThread, orgPkThread);
							agregarNotificacionEnvio(codNotThread, proyThread.getProyPk());
						} catch (MailException me) {
							for (String error : me.getErrores()) {
								logger.log(Level.WARNING, error);
							}
						}
					}
				});
				t.start();
			} else {
				logger.log(Level.WARNING, "No se envía notificación porque no existe la instancia '" + codNot + "' para el proyecto " + proy.getProyPk());
			}
		}
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	private void notificacionSuperada(String cod, Integer proyPk) {
		notificacionSuperada(cod, proyPk, true);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	private void notificacionSuperada(String cod, Integer proyPk, boolean transaction) {
		if (!StringsUtils.isEmpty(cod) && proyPk != null) {
			NotificacionEnvioDAO dao = new NotificacionEnvioDAO(emNoneJTA.createEntityManager());
			dao.superarNotEnviada(cod, proyPk, transaction);
		}
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public NotificacionEnvio agregarNotificacionEnvio(String cod, Integer proyPk) {
		NotificacionEnvio ne = new NotificacionEnvio(null, new Date(), proyPk, cod);
		NotificacionEnvioDAO dao = new NotificacionEnvioDAO(emNoneJTA.createEntityManager());
		try {
			ne = dao.guardar(ne, true);
			return ne;
		} catch (Exception ex) {
			logger.log(Level.SEVERE, null, ex);
		}
		return null;
	}

	public void superarFechaPagoVencida(Integer proyPk) {
		notificacionSuperada(ConstantesNotificaciones.NOT_COD_PRESUPUESTO_1, proyPk, false);
	}
}
