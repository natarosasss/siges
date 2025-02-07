package com.sofis.entities.data;

import com.sofis.entities.annotations.AtributoCodigo;
import com.sofis.entities.annotations.AtributoDescripcion;
import com.sofis.entities.annotations.AtributoHabilitado;
import com.sofis.entities.annotations.AtributoUltimaModificacion;
import com.sofis.entities.annotations.AtributoUltimaOrigen;
import com.sofis.entities.annotations.AtributoUltimoUsuario;
import com.sofis.entities.codigueras.SsRolCodigos;
import com.sofis.generico.utils.generalutils.CollectionsUtils;
import com.sofis.generico.utils.generalutils.StringsUtils;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.envers.Audited;

/**
 *
 * @author Usuario
 */
@Entity
@Audited
@Table(name = "ss_usuario")
@XmlRootElement
@NamedQueries({
	@NamedQuery(name = "SsUsuario.findAll", query = "SELECT s FROM SsUsuario s")
	,
    @NamedQuery(name = "SsUsuario.findByUsuId", query = "SELECT s FROM SsUsuario s WHERE s.usuId = :usuId")
	,
    @NamedQuery(name = "SsUsuario.findByUsuAdministrador", query = "SELECT s FROM SsUsuario s WHERE s.usuAdministrador = :usuAdministrador")
	,
    @NamedQuery(name = "SsUsuario.findByUsuCod", query = "SELECT s FROM SsUsuario s WHERE s.usuCod = :usuCod")
	,
    @NamedQuery(name = "SsUsuario.findByUsuCorreoElectronico", query = "SELECT s FROM SsUsuario s WHERE s.usuCorreoElectronico = :usuCorreoElectronico")
	,
    @NamedQuery(name = "SsUsuario.findByUsuCuentaBloqueada", query = "SELECT s FROM SsUsuario s WHERE s.usuCuentaBloqueada = :usuCuentaBloqueada")
	,
    @NamedQuery(name = "SsUsuario.findByUsuFechaPassword", query = "SELECT s FROM SsUsuario s WHERE s.usuFechaPassword = :usuFechaPassword")
	,
    @NamedQuery(name = "SsUsuario.findByUsuFechaUuid", query = "SELECT s FROM SsUsuario s WHERE s.usuFechaUuid = :usuFechaUuid")
	,
    @NamedQuery(name = "SsUsuario.findByUsuIntentosFallidos", query = "SELECT s FROM SsUsuario s WHERE s.usuIntentosFallidos = :usuIntentosFallidos")
	,
    @NamedQuery(name = "SsUsuario.findByUsuNroDoc", query = "SELECT s FROM SsUsuario s WHERE s.usuNroDoc = :usuNroDoc")
	,
    @NamedQuery(name = "SsUsuario.findByUsuOficinaPorDefecto", query = "SELECT s FROM SsUsuario s WHERE s.usuOficinaPorDefecto = :usuOficinaPorDefecto")
	,
    @NamedQuery(name = "SsUsuario.findByUsuPassword", query = "SELECT s FROM SsUsuario s WHERE s.usuPassword = :usuPassword")
	,
    @NamedQuery(name = "SsUsuario.findByUsuPrimerApellido", query = "SELECT s FROM SsUsuario s WHERE s.usuPrimerApellido = :usuPrimerApellido")
	,
    @NamedQuery(name = "SsUsuario.findByUsuPrimerNombre", query = "SELECT s FROM SsUsuario s WHERE s.usuPrimerNombre = :usuPrimerNombre")
	,
    @NamedQuery(name = "SsUsuario.findByUsuRegistrado", query = "SELECT s FROM SsUsuario s WHERE s.usuRegistrado = :usuRegistrado")
	,
    @NamedQuery(name = "SsUsuario.findByUsuSegundoApellido", query = "SELECT s FROM SsUsuario s WHERE s.usuSegundoApellido = :usuSegundoApellido")
	,
    @NamedQuery(name = "SsUsuario.findByUsuSegundoNombre", query = "SELECT s FROM SsUsuario s WHERE s.usuSegundoNombre = :usuSegundoNombre")
	,
    @NamedQuery(name = "SsUsuario.findByUsuTelefono", query = "SELECT s FROM SsUsuario s WHERE s.usuTelefono = :usuTelefono")
	,
    @NamedQuery(name = "SsUsuario.findByUsuUserCode", query = "SELECT s FROM SsUsuario s WHERE s.usuUserCode = :usuUserCode")
	,
    @NamedQuery(name = "SsUsuario.findByUsuUuidDes", query = "SELECT s FROM SsUsuario s WHERE s.usuUuidDes = :usuUuidDes")
	,
    @NamedQuery(name = "SsUsuario.findByUsuVersion", query = "SELECT s FROM SsUsuario s WHERE s.usuVersion = :usuVersion")
	,
    @NamedQuery(name = "SsUsuario.findByUsuVigente", query = "SELECT s FROM SsUsuario s WHERE s.usuVigente = :usuVigente")})
//@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SsUsuario implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "usu_id")
	private Integer usuId;
	@Column(name = "usu_administrador")
	private Boolean usuAdministrador;
	@Column(name = "usu_cambio_estado_desc")
	private String usuCambioEstadoDesc;
	@Size(max = 255)
	@Column(name = "usu_cod", length = 255)
	@AtributoCodigo
	private String usuCod;
	@Size(max = 255)
	@Column(name = "usu_correo_electronico", length = 255)
	private String usuCorreoElectronico;
	@Column(name = "usu_cuenta_bloqueada")
	private Boolean usuCuentaBloqueada;
	@Column(name = "usu_descripcion")
	@AtributoDescripcion
	private String usuDescripcion;
	@Column(name = "usu_direccion")
	private String usuDireccion;
	@Basic(optional = false)
	@Column(name = "usu_fecha_password")
	@Temporal(TemporalType.TIMESTAMP)
	private Date usuFechaPassword;
	@Column(name = "usu_fecha_uuid")
	@Temporal(TemporalType.TIMESTAMP)
	private Date usuFechaUuid;
//    @Lob
	@Column(name = "usu_foto")
	private byte[] usuFoto;
	@Column(name = "usu_intentos_fallidos")
	private Integer usuIntentosFallidos;
	@Basic(optional = true)
	@Column(name = "usu_nro_doc", length = 255)
	private String usuNroDoc;
	@Column(name = "usu_oficina_por_defecto")
	private Integer usuOficinaPorDefecto;
	@Basic(optional = true)
	@Column(name = "usu_origen")
	private String usuOrigen;
	@Size(max = 255)
	@Column(name = "usu_password", length = 255)
	private String usuPassword;
	@Basic(optional = false)
	@Column(name = "usu_primer_apellido", length = 255)
	private String usuPrimerApellido;
	@Basic(optional = false)
	@Column(name = "usu_primer_nombre", length = 255)
	private String usuPrimerNombre;
	@Column(name = "usu_registrado")
	private Boolean usuRegistrado;
	@Size(max = 255)
	@Column(name = "usu_segundo_apellido", length = 255)
	private String usuSegundoApellido;
	@Size(max = 255)
	@Column(name = "usu_segundo_nombre", length = 255)
	private String usuSegundoNombre;
	@Size(max = 255)
	@Column(name = "usu_telefono", length = 255)
	private String usuTelefono;
	@Size(max = 255)
	@Column(name = "usu_celular", length = 255)
	private String usuCelular;
	@Basic(optional = false)
	@Column(name = "usu_user_code")
	private int usuUserCode;
	@Column(name = "usu_aprob_facturas")
	private Boolean usuAprobFacturas;
	@Size(max = 255)
	@Column(name = "usu_uuid_des", length = 255)
	private String usuUuidDes;
	@Basic(optional = false)
	@Column(name = "usu_vigente")
	@AtributoHabilitado
	private boolean usuVigente;

	@Basic(optional = true)
	@Column(name = "usu_token", length = 100)
	private String usuToken;
	@Basic(optional = true)
	@Column(name = "usu_token_act")
	@Temporal(javax.persistence.TemporalType.TIMESTAMP)
	private Date usuTokenAct;

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "usuOfiRolesUsuario", orphanRemoval = true)
	@Fetch(FetchMode.SELECT)
	private List<SsUsuOfiRoles> ssUsuOfiRolesCollection;

	//Audit
//    @Column(name = "usu_habilitado")
//    @AtributoHabilitado
//    private Boolean usuHabilitado;
	@Column(name = "usu_ult_usuario")
	@AtributoUltimoUsuario
	private String usuUltUsuario;
	@Column(name = "usu_ult_mod")
	@Temporal(javax.persistence.TemporalType.TIMESTAMP)
	@AtributoUltimaModificacion
	private Date usuUltMod;
	@Column(name = "usu_ult_origen")
	@AtributoUltimaOrigen
	private String usuUltOrigen;
	@Column(name = "usu_version")
	@Version
	private Integer usuVersion;

	@Column(name = "usu_ldap_user", nullable = true)
	@Basic(optional = true)
	private String usuLdapUser;

	@Transient
	private String usuRePassword;

	public SsUsuario(Integer usuId) {
		this.usuId = usuId;
	}

	public SsUsuario(Integer usuId, Date usuFechaPassword, String usuNroDoc, String usuOrigen, String usuPrimerApellido, String usuPrimerNombre, int usuUserCode, boolean usuVigente) {
		this.usuId = usuId;
		this.usuFechaPassword = usuFechaPassword;
		this.usuNroDoc = usuNroDoc;
		this.usuOrigen = usuOrigen;
		this.usuPrimerApellido = usuPrimerApellido;
		this.usuPrimerNombre = usuPrimerNombre;
		this.usuUserCode = usuUserCode;
		this.usuVigente = usuVigente;
	}

	public SsUsuario(Integer usuId, Date usuFechaPassword, String usuNroDoc, String usuOrigen, String usuPrimerApellido, String usuSegundoApellido,
                    String usuPrimerNombre, String usuSegundoNombre, int usuUserCode, boolean usuVigente) {
		this.usuId = usuId;
		this.usuFechaPassword = usuFechaPassword;
		this.usuNroDoc = usuNroDoc;
		this.usuOrigen = usuOrigen;
		this.usuPrimerApellido = usuPrimerApellido;
		this.usuSegundoApellido = usuSegundoApellido;
		this.usuPrimerNombre = usuPrimerNombre;
		this.usuSegundoNombre = usuSegundoNombre;
		this.usuUserCode = usuUserCode;
		this.usuVigente = usuVigente;
	}

        public SsUsuario(Integer usuId, Date usuFechaPassword, String usuNroDoc, String usuOrigen, String usuPrimerApellido, String usuSegundoApellido, String usuPrimerNombre,
                    String usuSegundoNombre, int usuUserCode, boolean usuVigente, String usuCorreoElectronico, SsUsuOfiRoles ssUsuOfiRole) {
                this.usuId = usuId;
                this.usuFechaPassword = usuFechaPassword;
                this.usuNroDoc = usuNroDoc;
                this.usuOrigen = usuOrigen;
                this.usuPrimerApellido = usuPrimerApellido;
                this.usuSegundoApellido = usuSegundoApellido;
                this.usuPrimerNombre = usuPrimerNombre;
                this.usuSegundoNombre = usuSegundoNombre;
                this.usuUserCode = usuUserCode;
                this.usuVigente = usuVigente;

                        // Se agrega este parámetro para poder tomar el correo electrónico del usuario al momento de requerir una solicitud de cambio de fase.
                        this.usuCorreoElectronico = usuCorreoElectronico;

                this.ssUsuOfiRolesCollection = new ArrayList<SsUsuOfiRoles>();
                this.ssUsuOfiRolesCollection.add(ssUsuOfiRole);
    }
        

	public SsUsuario(Integer usuId, Date usuFechaPassword, String usuNroDoc, String usuOrigen, String usuPrimerApellido, 
                    String usuSegundoApellido, String usuPrimerNombre, String usuSegundoNombre, int usuUserCode, boolean usuVigente, SsUsuOfiRoles ssUsuOfiRole) {
		this.usuId = usuId;
		this.usuFechaPassword = usuFechaPassword;
		this.usuNroDoc = usuNroDoc;
		this.usuOrigen = usuOrigen;
		this.usuPrimerApellido = usuPrimerApellido;
		this.usuSegundoApellido = usuSegundoApellido;
		this.usuPrimerNombre = usuPrimerNombre;
		this.usuSegundoNombre = usuSegundoNombre;
		this.usuUserCode = usuUserCode;
		this.usuVigente = usuVigente;
		this.ssUsuOfiRolesCollection = new ArrayList<SsUsuOfiRoles>();
		this.ssUsuOfiRolesCollection.add(ssUsuOfiRole);
	}        

	public Integer getUsuId() {
		return usuId;
	}

	public void setUsuId(Integer usuId) {
		this.usuId = usuId;
	}

	public Boolean getUsuAdministrador() {
		return usuAdministrador;
	}

	public boolean isUsuAdministrador() {
		return usuAdministrador != null ? usuAdministrador : false;
	}

	public void setUsuAdministrador(Boolean usuAdministrador) {
		this.usuAdministrador = usuAdministrador;
	}

	public String getUsuCambioEstadoDesc() {
		return usuCambioEstadoDesc;
	}

	public void setUsuCambioEstadoDesc(String usuCambioEstadoDesc) {
		this.usuCambioEstadoDesc = usuCambioEstadoDesc;
	}

	public String getUsuCod() {
		return usuCod;
	}

	public void setUsuCod(String usuCod) {
		this.usuCod = usuCod;
	}

	public String getUsuCorreoElectronico() {
		return usuCorreoElectronico;
	}

	public void setUsuCorreoElectronico(String usuCorreoElectronico) {
		this.usuCorreoElectronico = usuCorreoElectronico;
	}

	public Boolean getUsuCuentaBloqueada() {
		return usuCuentaBloqueada;
	}

	public void setUsuCuentaBloqueada(Boolean usuCuentaBloqueada) {
		this.usuCuentaBloqueada = usuCuentaBloqueada;
	}

	public String getUsuDescripcion() {
		return usuDescripcion;
	}

	public void setUsuDescripcion(String usuDescripcion) {
		this.usuDescripcion = usuDescripcion;
	}

	public String getUsuDireccion() {
		return usuDireccion;
	}

	public void setUsuDireccion(String usuDireccion) {
		this.usuDireccion = usuDireccion;
	}

	public Date getUsuFechaPassword() {
		return usuFechaPassword;
	}

	public void setUsuFechaPassword(Date usuFechaPassword) {
		this.usuFechaPassword = usuFechaPassword;
	}

	public Date getUsuFechaUuid() {
		return usuFechaUuid;
	}

	public void setUsuFechaUuid(Date usuFechaUuid) {
		this.usuFechaUuid = usuFechaUuid;
	}

	public byte[] getUsuFoto() {
		return usuFoto;
	}

	public void setUsuFoto(byte[] usuFoto) {
		this.usuFoto = usuFoto;
	}

	public Integer getUsuIntentosFallidos() {
		return usuIntentosFallidos;
	}

	public void setUsuIntentosFallidos(Integer usuIntentosFallidos) {
		this.usuIntentosFallidos = usuIntentosFallidos;
	}

	public String getUsuNroDoc() {
		return usuNroDoc;
	}

	public void setUsuNroDoc(String usuNroDoc) {
		this.usuNroDoc = usuNroDoc;
	}

	public Integer getUsuOficinaPorDefecto() {
		return usuOficinaPorDefecto;
	}

	public void setUsuOficinaPorDefecto(Integer usuOficinaPorDefecto) {
		this.usuOficinaPorDefecto = usuOficinaPorDefecto;
	}

	public String getUsuOrigen() {
		return usuOrigen;
	}

	public void setUsuOrigen(String usuOrigen) {
		this.usuOrigen = usuOrigen;
	}

	public String getUsuPassword() {
		return usuPassword;
	}

	public void setUsuPassword(String usuPassword) {
		this.usuPassword = usuPassword;
	}

	public String getUsuPrimerApellido() {
		return usuPrimerApellido;
	}

	public void setUsuPrimerApellido(String usuPrimerApellido) {
		this.usuPrimerApellido = usuPrimerApellido;
	}

	public String getUsuPrimerNombre() {
		return usuPrimerNombre;
	}

	public void setUsuPrimerNombre(String usuPrimerNombre) {
		this.usuPrimerNombre = usuPrimerNombre;
	}

	public Boolean getUsuRegistrado() {
		return usuRegistrado;
	}

	public void setUsuRegistrado(Boolean usuRegistrado) {
		this.usuRegistrado = usuRegistrado;
	}

	public String getUsuSegundoApellido() {
		return usuSegundoApellido;
	}

	public void setUsuSegundoApellido(String usuSegundoApellido) {
		this.usuSegundoApellido = usuSegundoApellido;
	}

	public String getUsuSegundoNombre() {
		return usuSegundoNombre;
	}

	public void setUsuSegundoNombre(String usuSegundoNombre) {
		this.usuSegundoNombre = usuSegundoNombre;
	}

	public String getUsuTelefono() {
		return usuTelefono;
	}

	public void setUsuTelefono(String usuTelefono) {
		this.usuTelefono = usuTelefono;
	}

	public String getUsuCelular() {
		return usuCelular;
	}

	public void setUsuCelular(String usuCelular) {
		this.usuCelular = usuCelular;
	}

	public int getUsuUserCode() {
		return usuUserCode;
	}

	public void setUsuUserCode(int usuUserCode) {
		this.usuUserCode = usuUserCode;
	}

	public String getUsuUuidDes() {
		return usuUuidDes;
	}

	public void setUsuUuidDes(String usuUuidDes) {
		this.usuUuidDes = usuUuidDes;
	}

	public Integer getUsuVersion() {
		return usuVersion;
	}

	public void setUsuVersion(Integer usuVersion) {
		this.usuVersion = usuVersion;
	}

	public boolean getUsuVigente() {
		return usuVigente;
	}

	public void setUsuVigente(boolean usuVigente) {
		this.usuVigente = usuVigente;
	}

	public String getUsuToken() {
		return usuToken;
	}

	public void setUsuToken(String usuToken) {
		this.usuToken = usuToken;
	}

	public Date getUsuTokenAct() {
		return usuTokenAct;
	}

	public void setUsuTokenAct(Date usuTokenAct) {
		this.usuTokenAct = usuTokenAct;
	}

	public Boolean getUsuAprobFacturas() {
		return usuAprobFacturas;
	}

	public boolean isUsuAprobFact() {
		return usuAprobFacturas != null ? usuAprobFacturas : false;
	}

	public void setUsuAprobFacturas(Boolean usuAprobFacturas) {
		this.usuAprobFacturas = usuAprobFacturas;
	}

	public String getUsuUltUsuario() {
		return usuUltUsuario;
	}

	public void setUsuUltUsuario(String usuUltUsuario) {
		this.usuUltUsuario = usuUltUsuario;
	}

	public Date getUsuUltMod() {
		return usuUltMod;
	}

	public void setUsuUltMod(Date usuUltMod) {
		this.usuUltMod = usuUltMod;
	}

	public String getUsuUltOrigen() {
		return usuUltOrigen;
	}

	public void setUsuUltOrigen(String usuUltOrigen) {
		this.usuUltOrigen = usuUltOrigen;
	}

	@XmlTransient
	public List<SsUsuOfiRoles> getSsUsuOfiRolesCollection() {
		return ssUsuOfiRolesCollection;
	}

	/**
	 * Retorna los roles activos que tenga el usuario en los diferentes
	 * organismos.
	 *
	 * @return Set
	 */
	public Set<SsUsuOfiRoles> getSsUsuOfiRolesCollectionActivos() {
		if (CollectionsUtils.isNotEmpty(ssUsuOfiRolesCollection)) {
			Set<SsUsuOfiRoles> result = new HashSet<SsUsuOfiRoles>();
			for (SsUsuOfiRoles uor : ssUsuOfiRolesCollection) {
				if (uor.isUsuOfiRolesActivo()) {
					result.add(uor);
				}
			}
			return result;
		}
		return null;
	}

	public SsUsuOfiRoles getSsUsuOfiRoles(Integer orgPk) {
		return this.getSsUsuOfiRoles(orgPk, false);
	}

	public boolean isSsUsuOfiActivo(Integer orgPk) {
		return getSsUsuOfiRoles(orgPk, Boolean.TRUE) != null;
	}

	public SsUsuOfiRoles getSsUsuOfiRoles(Integer orgPk, Boolean activo) {
		if (orgPk != null) {
			for (SsUsuOfiRoles uor : ssUsuOfiRolesCollection) {
				if (uor.getUsuOfiRolesOficina().getOfiId().equals(orgPk)
						&& uor.getUsuOfiRolesRol().isRolTipoUsuario()) {
					if (activo != null) {
						return uor.isUsuOfiRolesActivo() == activo ? uor : null;
					} else {
						return uor;
					}
				}
			}
		}
		return null;
	}

	public void setSsUsuOfiRolesCollection(List<SsUsuOfiRoles> ssUsuOfiRolesCollection) {
		this.ssUsuOfiRolesCollection = ssUsuOfiRolesCollection;
	}

	public String getUsuRePassword() {
		return usuRePassword;
	}

	public void setUsuRePassword(String usuRePassword) {
		this.usuRePassword = usuRePassword;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (usuId != null ? usuId.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof SsUsuario)) {
			return false;
		}
		SsUsuario other = (SsUsuario) object;
		if ((this.usuId == null && other.usuId != null) || (this.usuId != null && !this.usuId.equals(other.usuId))) {
			return false;
		}
		return true;
	}

	public String getUsuNombreApellido() {
		return StringsUtils.toString(this.usuPrimerNombre).trim() + " " + StringsUtils.toString(this.usuPrimerApellido).trim();
	}

	public String getUsuNombreApe(int largo) {
		return StringsUtils.recortarTexto(getUsuNombreApellido(), largo);
	}

	public boolean isRol(String rolCod, Integer orgPk) {
		if (rolCod != null && this.ssUsuOfiRolesCollection != null) {
			for (SsUsuOfiRoles ssUsuOfiRoles : this.ssUsuOfiRolesCollection) {
				if (ssUsuOfiRoles.getUsuOfiRolesRol().getRolCod().equals(rolCod)
						&& ssUsuOfiRoles.isUsuOfiRolesActivo()
						&& (rolCod.equalsIgnoreCase(SsRolCodigos.ADMINISTRADOR)
						|| ssUsuOfiRoles.getUsuOfiRolesOficina().getOfiId().equals(orgPk))) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean isAdministrador() {
		return isRol(SsRolCodigos.ADMINISTRADOR, null);
	}

	public boolean isUsuarioDirector(Integer orgPk) {
		return isRol(SsRolCodigos.DIRECTOR, orgPk);
	}

	/**
	 * Si el usuario es PMOF o PMOT para el organismo aportado.
	 *
	 * @param orgPk
	 * @return boolean.
	 */
	public boolean isUsuarioPMO(Integer orgPk) {
		return isUsuarioPMOF(orgPk) || isUsuarioPMOT(orgPk);
	}

	public boolean isUsuarioPMOF(Integer orgPk) {
		return isRol(SsRolCodigos.PMO_FEDERADA, orgPk);
	}

	public boolean isUsuarioPMOT(Integer orgPk) {
		return isRol(SsRolCodigos.PMO_TRANSVERSAL, orgPk);
	}

	public boolean isUsuarioComun(Integer orgPk) {
		return isRol(SsRolCodigos.USUARIO_COMUN, orgPk);
	}

	public boolean isUsuarioCargaHoras(Integer orgPk) {
		return isRol(SsRolCodigos.REGISTRO_HORAS, orgPk);
	}

	public boolean isUsuarioEditor(Integer orgPk) {
		return isRol(SsRolCodigos.EDITOR_GRAL, orgPk);
	}

	public String getOrganismosStr() {
		String result = "";
		int first = 0;

		if (CollectionsUtils.isNotEmpty(this.ssUsuOfiRolesCollection)) {
			Map<Integer, SsOficina> oficinas = new HashMap<Integer, SsOficina>();
			for (SsUsuOfiRoles ofRol : this.ssUsuOfiRolesCollection) {
				if (ofRol.getUsuOfiRolesOficina() != null
						&& ofRol.isUsuOfiRolesActivo()
						&& !oficinas.containsKey(ofRol.getUsuOfiRolesOficina().getOfiId())) {
					oficinas.put(ofRol.getUsuOfiRolesOficina().getOfiId(), ofRol.getUsuOfiRolesOficina());
				}
			}

			for (Map.Entry<Integer, SsOficina> ofi : oficinas.entrySet()) {
				if (ofi.getValue() != null
						&& !StringsUtils.isEmpty(ofi.getValue().getOfiNombre())) {
					if (first > 0) {
						result = result.concat(", ");
					}
					result = result.concat(ofi.getValue().getOfiNombre());
					first++;
				}
			}
			result = first > 0 ? result.concat(".") : result;
		}
		return result;
	}

	public SsRol getRolUsuario(Integer orgPk) {
		if (this.getSsUsuOfiRolesCollection() != null
				&& !this.getSsUsuOfiRolesCollection().isEmpty()) {
			for (SsUsuOfiRoles roles : this.getSsUsuOfiRolesCollection()) {
				if (roles.getUsuOfiRolesRol().getRolTipoUsuario() != null
						&& roles.isUsuOfiRolesActivo()
						&& roles.getUsuOfiRolesRol().getRolTipoUsuario()
						&& (orgPk != null && roles.getUsuOfiRolesOficina().getOfiId().equals(orgPk))) {
					return roles.getUsuOfiRolesRol();
				}
			}
		}
		return null;
	}

	public boolean perteneceAlOrg(Integer orgPk) {
		if (orgPk != null) {
			if (this.ssUsuOfiRolesCollection != null) {
				for (SsUsuOfiRoles ofRol : this.ssUsuOfiRolesCollection) {
					if (ofRol.getUsuOfiRolesOficina() != null
							&& ofRol.getUsuOfiRolesOficina().getOfiId().equals(orgPk)
							&& ofRol.isUsuOfiRolesActivo()) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public Areas getUsuArea(Integer orgPk) {
		if (CollectionsUtils.isNotEmpty(ssUsuOfiRolesCollection) && orgPk != null) {
			for (SsUsuOfiRoles ofRol : this.ssUsuOfiRolesCollection) {
				if (ofRol.getUsuOfiRolesOficina() != null
						&& ofRol.getUsuOfiRolesOficina().getOfiId().equals(orgPk)) {
					return ofRol.getUsuOfiRolesUsuArea();
				}
			}
		}
		return null;
	}

	public void setUsuArea(Areas area, Integer orgPk) {
		if (CollectionsUtils.isNotEmpty(ssUsuOfiRolesCollection) && orgPk != null) {
			for (SsUsuOfiRoles ofRol : this.ssUsuOfiRolesCollection) {
				if (ofRol.getUsuOfiRolesOficina() != null
						&& ofRol.getUsuOfiRolesOficina().getOfiId().equals(orgPk)) {
					ofRol.setUsuOfiRolesUsuArea(area);
					break;
				}
			}
		}
	}

	@Override
	public String toString() {
		return "com.sofis.gestemp.entities.SsUsuario[ usuId=" + usuId + " ]";
	}

	@PrePersist
	@PreUpdate
	public void preGrabar() {
//       this.usuUltMod = new Date();
	}

	public String getNombreApellido() {
		return (this.usuPrimerNombre != null ? this.usuPrimerNombre : "") + " " + (this.usuPrimerApellido != null ? this.usuPrimerApellido : "");
	}

	public boolean isNuevo() {
		return this.usuId == null;
	}

	/**
	 * @return the usuLdapUser
	 */
	public String getUsuLdapUser() {
		return usuLdapUser;
	}

	/**
	 * @param usuLdapUser the usuLdapUser to set
	 */
	public void setUsuLdapUser(String usuLdapUser) {
		this.usuLdapUser = usuLdapUser;
	}

}
