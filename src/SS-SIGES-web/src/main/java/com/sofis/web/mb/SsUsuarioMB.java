package com.sofis.web.mb;

import com.sofis.entities.constantes.ConstanteApp;
import com.sofis.entities.data.Areas;
import com.sofis.entities.data.Organismos;
import com.sofis.entities.data.SsOficina;
import com.sofis.entities.data.SsRol;
import com.sofis.entities.data.SsUsuOfiRoles;
import com.sofis.entities.data.SsUsuario;
import com.sofis.entities.validations.FormUsuarioValidacion;
import com.sofis.exceptions.BusinessException;
import com.sofis.exceptions.GeneralException;
import com.sofis.exceptions.MailException;
import com.sofis.generico.utils.generalutils.CollectionsUtils;
import com.sofis.web.componentes.SofisPopupUI;
import com.sofis.web.delegates.AreasDelegate;
import com.sofis.web.delegates.ConfiguracionDelegate;
import com.sofis.web.delegates.ConsultaHistoricoDelegate;
import com.sofis.web.delegates.SsRolDelegate;
import com.sofis.web.delegates.SsUsuarioDelegate;
import com.sofis.web.properties.Labels;
import com.sofis.web.utils.JSFUtils;
import com.sofis.web.utils.SofisCombo;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;

/**
 *
 * @author SSGenerador
 */
@ManagedBean(name = "ssUsuarioMB")
@ViewScoped
public class SsUsuarioMB implements Serializable {

	/**
	 * @return the authLdapEnable
	 */
	public Boolean getAuthLdapEnable() {
		return authLdapEnable;
	}

	/**
	 * @param authLdapEnable the authLdapEnable to set
	 */
	public void setAuthLdapEnable(Boolean authLdapEnable) {
		this.authLdapEnable = authLdapEnable;
	}

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(SsUsuarioMB.class.getName());
	private static final String BUSQUEDA_USUARIO_MSG_ID = "busquedaUsuarioMsg";
	private static final String USUARIO_POPUP_MSG_ID = "usuarioPopupMsg";

	@ManagedProperty(value = "#{inicioMB}")
	private InicioMB inicioMB;
	@ManagedProperty("#{aplicacionMB}")
	private AplicacionMB aplicacionMB;
	@Inject
	private ConsultaHistoricoDelegate<SsUsuario> histDelegate;
	@Inject
	private SsUsuarioDelegate usuarioDelegate;
	@Inject
	private SofisPopupUI renderPopupEdicion;
	@Inject
	private SofisPopupUI renderPopupHistorial;
	@Inject
	private AreasDelegate areasDelegate;
	@Inject
	private SsRolDelegate ssRolDelegate;
	@Inject
	private SsUsuarioDelegate ssUsuarioDelegate;
	@Inject
	private ConfiguracionDelegate configuracionDelegate;

//    private String bCodigo;
//    private String bDescripcion;
	private String bMail;
	private String bNombre;
	private String bApellido;
	private List<SsUsuario> listaResultado = new ArrayList<>();
	private Boolean renderResultado = false;
	private SsUsuario usuarioEnEdicion = new SsUsuario();
	private List<Organismos> organismosUsuario = new ArrayList<>();
	private List<SsUsuario> listaHitorial = new ArrayList<>();
	private String cantElementosPorPagina = "25";
	private String elementoOrdenacion = "usuCorreoElectronico";
	private String ascendente = "Ascendente";
	private Boolean popupSsUsuario = Boolean.FALSE;
	private String codigoSsUsuario = "";
	private String contenidoSsUsuario = "";
	private List<Areas> listaAreas;
	private SofisCombo listaAreasOrganismoCombo = new SofisCombo();
	private List<SsRol> listaRolUsuario;
	private SofisCombo listaRolUsuarioCombo = new SofisCombo();
	private boolean disableInputPopupForm = true;

	private Boolean authLdapEnable = false;

	/**
	 * Creates a new instance of SsUsuarioMB
	 */
	public SsUsuarioMB() {
	}

	public void setAplicacionMB(AplicacionMB aplicacionMB) {
		this.aplicacionMB = aplicacionMB;
	}

	/**
	 * Creates a new instance of SsUsuarioMB
	 */
	private void reset() {
		bMail = "";
		bNombre = "";
		bApellido = "";
		listaResultado = new ArrayList<SsUsuario>();
		renderResultado = false;
	}

	@PostConstruct
	public void init() {
		inicioMB.cargarOrganismoSeleccionado();
		String authLdapEnableString = configuracionDelegate.obtenerCnfValorPorCodigo("AUTH_LDAP_ENABLE", null);
		authLdapEnable = authLdapEnableString != null && "true".equals(authLdapEnableString.toLowerCase());
		buscar();
	}

	// <editor-fold defaultstate="collapsed" desc="eventos">
	public String buscar() {

		listaResultado = ssUsuarioDelegate.busquedaUsuFiltro(inicioMB.getOrganismo(), inicioMB.getUsuario(), bMail, bNombre, bApellido);
		renderResultado = true;

		return null;
	}

	public String limpiar() {
		reset();
		return null;
	}

	public String agregar() {
		usuarioEnEdicion = new SsUsuario();
		cargarCombos();
		usuarioEnEdicion.setUsuVigente(true);
		disableInputPopupForm = true;
		renderPopupEdicion.abrir();
		return null;
	}

	public String editar(Integer id) {
		try {
			usuarioEnEdicion = usuarioDelegate.obtenerSsUsuarioPorId(id);
			usuarioEnEdicion.setUsuRePassword(usuarioEnEdicion.getUsuPassword());
			cargarCombos();
			disableInputPopupForm = false;
			renderPopupEdicion.abrir();
		} catch (GeneralException ex) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
		}
		return null;
	}

	public String resetearContraseña() {
		try {
                    ssUsuarioDelegate.resetearContrasenia(usuarioEnEdicion.getUsuCorreoElectronico(), inicioMB.getOrganismo().getOrgPk());
                    JSFUtils.agregarMsg(null, Labels.getValue("info_usuario_reset_pass"), null);
		} catch (BusinessException be) {
                    /*
                    *  19-06-2018 Inspección de código.
                    */
                    //JSFUtils.agregarMsg(null, Labels.getValue("error_usuario_reset_pass"), null);
                    //JSFUtils.agregarMsgs(null, be.getErrores());

                    JSFUtils.agregarMsgError("", Labels.getValue("error_usuario_reset_pass"), null);                
                    for(String iterStr : be.getErrores()){
                        JSFUtils.agregarMsgError("", Labels.getValue(iterStr), null);                
                    } 
		}
		return null;
	}

	public String agregarAlOrg(SsUsuario usu) {
		Integer orgPk = inicioMB.getOrganismo().getOrgPk();
		SsUsuOfiRoles ofiRol = usu.getSsUsuOfiRoles(orgPk, null);
		if (ofiRol == null) {
			SsRol rol = ssRolDelegate.obtenerRolesPorCod("USU");
			ofiRol = new SsUsuOfiRoles("Usuario Gestion", usu.getUsuId(), rol, usu, new SsOficina(orgPk));
			usu.getSsUsuOfiRolesCollection().add(ofiRol);
		} else {
			ofiRol.setUsuOfiRolesActivo(true);
		}
		usuarioDelegate.guardarUsuario(usu, inicioMB.getOrganismo().getOrgPk());
		buscar();
		aplicacionMB.cargarUsuariosPorOrganismo(orgPk);
		return null;
	}

	public String quitarDelOrg(SsUsuario usu) {
		Organismos org = inicioMB.getOrganismo();

		for (SsUsuOfiRoles ofiRol : usu.getSsUsuOfiRolesCollection()) {
			if (ofiRol.getUsuOfiRolesOficina().getOfiId().equals(org.getOrgPk())) {
				ofiRol.setUsuOfiRolesActivo(false);
			}
		}

		usuarioDelegate.guardarUsuario(usu, org.getOrgPk());
		buscar();
		aplicacionMB.cargarUsuariosPorOrganismo(org.getOrgPk());
		return null;
	}

	private void cargarCombos() {
		Integer orgPk = inicioMB.getOrganismo().getOrgPk();
		listaAreas = areasDelegate.obtenerAreasPorOrganismo(orgPk, false);
//        listaAreas = aplicacionMB.obtenerAreasPorOrganismo(orgPk);
		if (CollectionsUtils.isNotEmpty(listaAreas)) {
			listaAreasOrganismoCombo = new SofisCombo((List) listaAreas, "areaNombre");
			listaAreasOrganismoCombo.addEmptyItem(Labels.getValue("comboEmptyItem"));
		}

		listaRolUsuario = ssRolDelegate.obtenerRolesUsuarios();
		if (CollectionsUtils.isNotEmpty(listaRolUsuario)) {
			listaRolUsuarioCombo = new SofisCombo((List) listaRolUsuario, "rolNombre");
			listaRolUsuarioCombo.addEmptyItem(Labels.getValue("comboEmptyItem"));
		}

		if (usuarioEnEdicion.getUsuId() != null) {
			if (usuarioEnEdicion.getUsuArea(orgPk) != null) {
				listaAreasOrganismoCombo.setSelected(usuarioEnEdicion.getUsuArea(orgPk).getAreaPk());
			}
			SsRol rolUsu = usuarioEnEdicion.getRolUsuario(orgPk);
			if (rolUsu != null) {
				listaRolUsuarioCombo.setSelected(rolUsu.getRolId());
			}
		}
	}

	public String consultarHistorial(Integer id) {
		try {
			listaHitorial = histDelegate.obtenerHistorialPorId(SsUsuario.class, id, "usuVersion");
			renderPopupHistorial.abrir();
		} catch (GeneralException ex) {
			logger.log(Level.SEVERE, ex.getMessage());
		}
		return null;
	}

	public String cerrarPopupHistorial() {
		renderPopupHistorial.cerrar();
		return null;
	}

	public String guardar() {
		SsRol ssRol = (SsRol) listaRolUsuarioCombo.getSelectedObject();
		Organismos org = inicioMB.getOrganismo();
		if (ssRol != null) {
			ssUsuarioDelegate.cargarRolUsuario(usuarioEnEdicion, ssRol, org);
		}
		cargarCombosSeleccionados();

		Integer orgPk = inicioMB.getOrganismo().getOrgPk();
		try {
			FormUsuarioValidacion.validar(usuarioEnEdicion, orgPk, (listaAreas != null));

			usuarioEnEdicion = usuarioDelegate.guardarUsuario(usuarioEnEdicion, orgPk);
			if (usuarioEnEdicion != null) {
				renderPopupEdicion.cerrar();
				disableInputPopupForm = true;
				buscar();
			}

		} catch (BusinessException be) {
			logger.log(Level.SEVERE, be.getMessage(), be);
			JSFUtils.agregarMsgs(USUARIO_POPUP_MSG_ID, be.getErrores());
		} catch (MailException me) {
			logger.log(Level.WARNING, me.getMessage(), me);
			JSFUtils.agregarMsgs(USUARIO_POPUP_MSG_ID, me.getErrores());
		} catch (Exception ex) {
			logger.log(Level.SEVERE, ex.getMessage(), ex);
			JSFUtils.agregarMsg("error_usuario_guardar");
		}
		aplicacionMB.cargarUsuariosPorOrganismo(orgPk);
		return null;
	}

	private void cargarCombosSeleccionados() {
		usuarioEnEdicion.setUsuArea((Areas) listaAreasOrganismoCombo.getSelectedObject(), inicioMB.getOrganismo().getOrgPk());
		if (usuarioEnEdicion.getUsuOficinaPorDefecto() == null) {
			usuarioEnEdicion.setUsuOficinaPorDefecto(inicioMB.getOrganismo().getOrgPk());
		}
	}

	public void cancelar() {
//        reset();
		renderPopupEdicion.cerrar();
	}

	public void cambiarCantPaginas(ValueChangeEvent evt) {
		buscar();
	}

	public void cambiarCriterioOrdenacion(ValueChangeEvent evt) {
		elementoOrdenacion = evt.getNewValue().toString();
		buscar();
	}

	public void cambiarAscendenteOrdenacion(ValueChangeEvent evt) {
		ascendente = evt.getNewValue().toString();
		buscar();
	}

	public String activarSsUsuario(String codigo) {
		SsUsuario contenido = usuarioDelegate.obtenerSsUsuarioPorCodigo(codigo);
		if (contenido != null) {
			codigoSsUsuario = codigo;
			contenidoSsUsuario = contenido.getUsuDescripcion();
			popupSsUsuario = Boolean.TRUE;
		}
		return null;
	}

	public String cerrarSsUsuario() {
		popupSsUsuario = Boolean.FALSE;
		return null;
	}

	public void checkMailEntry() {
		String mail = usuarioEnEdicion.getUsuCorreoElectronico();
		SsUsuario usuario = ssUsuarioDelegate.obtenerSsUsuarioPorMail(mail);
		if (usuario != null) {
			editar(usuario.getUsuId());
		} else {
			usuarioEnEdicion = new SsUsuario();
			usuarioEnEdicion.setUsuCorreoElectronico(mail);
		}
		disableInputPopupForm = false;
	}

//  </editor-fold>
	// <editor-fold defaultstate="collapsed" desc="getter-setter">
	public SsUsuario getUsuarioEnEdicion() {
		return usuarioEnEdicion;
	}

	public void setUsuarioEnEdicion(SsUsuario thoEnEdicion) {
		this.usuarioEnEdicion = thoEnEdicion;
	}

	public List<Organismos> getOrganismosUsuario() {
		return organismosUsuario;
	}

	public void setOrganismosUsuario(List<Organismos> organismosUsuario) {
		this.organismosUsuario = organismosUsuario;
	}

	public String getAscendente() {
		return ascendente;
	}

	public void setAscendente(String ascendente) {
		this.ascendente = ascendente;
	}

	public String getElementoOrdenacion() {
		return elementoOrdenacion;
	}

	public void setElementoOrdenacion(String elementoOrdenacion) {
		this.elementoOrdenacion = elementoOrdenacion;
	}

	public String getbMail() {
		return bMail;
	}

	public void setbMail(String bMail) {
		this.bMail = bMail;
	}

	public String getbNombre() {
		return bNombre;
	}

	public void setbNombre(String bNombre) {
		this.bNombre = bNombre;
	}

	public String getbApellido() {
		return bApellido;
	}

	public void setbApellido(String bApellido) {
		this.bApellido = bApellido;
	}

	public List<SsUsuario> getListaResultado() {
		return listaResultado;
	}

	public void setListaResultado(List<SsUsuario> listaResultado) {
		this.listaResultado = listaResultado;
	}

	public Boolean getRenderResultado() {
		return renderResultado;
	}

	public void setRenderResultado(Boolean renderResultado) {
		this.renderResultado = renderResultado;
	}

	public SofisPopupUI getRenderPopupEdicion() {
		return renderPopupEdicion;
	}

	public void setRenderPopupEdicion(SofisPopupUI renderPopupEdicion) {
		this.renderPopupEdicion = renderPopupEdicion;
	}

	public List<SsUsuario> getListaHitorial() {
		return listaHitorial;
	}

	public void setListaHitorial(List<SsUsuario> listaHitorial) {
		this.listaHitorial = listaHitorial;
	}

	public SofisPopupUI getRenderPopupHistorial() {
		return renderPopupHistorial;
	}

	public void setRenderPopupHistorial(SofisPopupUI renderPopupHistorial) {
		this.renderPopupHistorial = renderPopupHistorial;
	}

	public String getCantElementosPorPagina() {
		return cantElementosPorPagina;
	}

	public void setCantElementosPorPagina(String cantElementosPorPagina) {
		this.cantElementosPorPagina = cantElementosPorPagina;
	}

	public Boolean getPopupSsUsuario() {
		return popupSsUsuario;
	}

	public void setPopupSsUsuario(Boolean popupSsUsuario) {
		this.popupSsUsuario = popupSsUsuario;
	}

	public String getCodigoSsUsuario() {
		return codigoSsUsuario;
	}

	public void setCodigoSsUsuario(String codigoSsUsuario) {
		this.codigoSsUsuario = codigoSsUsuario;
	}

	public String getContenidoSsUsuario() {
		return contenidoSsUsuario;
	}

	public void setContenidoSsUsuario(String contenidoSsUsuario) {
		this.contenidoSsUsuario = contenidoSsUsuario;
	}

	public List<Areas> getListaAreas() {
		return listaAreas;
	}

	public void setListaAreas(List<Areas> listaAreas) {
		this.listaAreas = listaAreas;
	}

	public SofisCombo getListaAreasOrganismoCombo() {
		return listaAreasOrganismoCombo;
	}

	public void setListaAreasOrganismoCombo(SofisCombo listaAreasOrganismoCombo) {
		this.listaAreasOrganismoCombo = listaAreasOrganismoCombo;
	}

	public List<SsRol> getListaRolUsuario() {
		return listaRolUsuario;
	}

	public void setListaRolUsuario(List<SsRol> listaRolUsuario) {
		this.listaRolUsuario = listaRolUsuario;
	}

	public SofisCombo getListaRolUsuarioCombo() {
		return listaRolUsuarioCombo;
	}

	public void setListaRolUsuarioCombo(SofisCombo listaRolUsuarioCombo) {
		this.listaRolUsuarioCombo = listaRolUsuarioCombo;
	}

	public boolean isDisableInputPopupForm() {
		return disableInputPopupForm;
	}

	public void setDisableInputPopupForm(boolean disableInputPopupForm) {
		this.disableInputPopupForm = disableInputPopupForm;
	}

	public void setInicioMB(InicioMB inicioMB) {
		this.inicioMB = inicioMB;
	}
	// </editor-fold>
}
