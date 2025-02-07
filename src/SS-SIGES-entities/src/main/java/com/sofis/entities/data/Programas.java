package com.sofis.entities.data;

import com.sofis.entities.annotations.AtributoUltimaModificacion;
import com.sofis.entities.annotations.AtributoUltimoUsuario;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

/**
 *
 * @author Usuario
 */
@Entity
//@ Audited
@Table(name = "programas")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Programas.findAll", query = "SELECT p FROM Programas p"),
    @NamedQuery(name = "Programas.findByProgPk", query = "SELECT p FROM Programas p WHERE p.progPk = :progPk"),
    @NamedQuery(name = "Programas.findByProgNombre", query = "SELECT p FROM Programas p WHERE p.progNombre = :progNombre"),
    @NamedQuery(name = "Programas.findByProgObjetivo", query = "SELECT p FROM Programas p WHERE p.progObjetivo = :progObjetivo"),
    @NamedQuery(name = "Programas.findByProgObjPublico", query = "SELECT p FROM Programas p WHERE p.progObjPublico = :progObjPublico"),
    @NamedQuery(name = "Programas.findByProgGrp", query = "SELECT p FROM Programas p WHERE p.progGrp = :progGrp"),
    @NamedQuery(name = "Programas.progSemaforoRojo", query = "SELECT p FROM Programas p WHERE p.progSemaforoRojo = :progSemaforoRojo"),
    @NamedQuery(name = "Programas.findByProgSemaforoAmarillo", query = "SELECT p FROM Programas p WHERE p.progSemaforoAmarillo = :progSemaforoAmarillo"),
    @NamedQuery(name = "Programas.findByIds", query = "SELECT p FROM Programas p WHERE p.progPk IN (:ids)")
})
@NamedNativeQueries({
    @NamedNativeQuery(name = "Programas.findByProgPkAndProgNombre", query = "SELECT p.prog_pk FROM programas AS p WHERE p.prog_pk LIKE :codigo AND p.prog_nombre LIKE :nombre AND p.prog_org_fk = :org AND p.prog_activo = true")
})
public class Programas implements Serializable {

    public static final int NOMBRE_LENGHT = 100;
    public static final int DESCRIPCION_LENGHT = 4000;
    public static final int OBJETIVO_LENGHT = 4000;
    public static final int OBJ_PUBLICO_LENGHT = 4000;

    private static final long serialVersionUID = 1L;

    private static final Logger logger = Logger.getLogger(Programas.class.getName());

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "prog_pk")
    private Integer progPk;
    @Column(name = "prog_nombre")
    private String progNombre;
    @Column(name = "prog_descripcion")
    private String progDescripcion;
    @Column(name = "prog_objetivo")
    private String progObjetivo;
    @Column(name = "prog_obj_publico")
    private String progObjPublico;
    @Column(name = "prog_factor_impacto")
    private String progFactorImpacto;
    @Column(name = "prog_grp")
    private String progGrp;
    @Column(name = "prog_semaforo_amarillo")
    private Integer progSemaforoAmarillo;
    @Column(name = "prog_semaforo_rojo")
    private Integer progSemaforoRojo;
    @JoinTable(name = "prog_tags", joinColumns = {
        @JoinColumn(name = "progtag_prog_pk", referencedColumnName = "prog_pk")}, inverseJoinColumns = {
        @JoinColumn(name = "progtag_area_pk", referencedColumnName = "arastag_pk")})
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Fetch(FetchMode.SELECT)
    private Set<AreasTags> areasTematicasSet;
    @JoinTable(name = "prog_lectura_area", joinColumns = {
        @JoinColumn(name = "proglectarea_prog_pk", referencedColumnName = "prog_pk")}, inverseJoinColumns = {
        @JoinColumn(name = "proglectarea_area_pk", referencedColumnName = "area_pk")})
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Fetch(FetchMode.SELECT)
    private Set<Areas> areasRestringidasSet;
    @JoinTable(name = "prog_docs", joinColumns = {
        @JoinColumn(name = "progdocs_prog_pk", referencedColumnName = "prog_pk")}, inverseJoinColumns = {
        @JoinColumn(name = "progdocs_doc_pk", referencedColumnName = "docs_pk")})
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Fetch(FetchMode.SELECT)
    private Set<Documentos> documentosSet;
    @JoinTable(name = "prog_int", joinColumns = {
        @JoinColumn(name = "progint_prog_pk", referencedColumnName = "prog_pk")}, inverseJoinColumns = {
        @JoinColumn(name = "progint_int_pk", referencedColumnName = "int_pk")})
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Fetch(FetchMode.SELECT)
    private List<Interesados> interesadosList;

    @JoinColumn(name = "prog_pre_fk", referencedColumnName = "pre_pk")
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Presupuesto progPreFk;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "proyProgFk")
    @Fetch(FetchMode.SELECT)
    private Set<Proyectos> proyectosSet;
    @JoinColumn(name = "prog_usr_pmofed_fk", referencedColumnName = "usu_id")
    @ManyToOne(optional = true)
    private SsUsuario progUsrPmofedFk;
    @JoinColumn(name = "prog_usr_sponsor_fk", referencedColumnName = "usu_id")
    @ManyToOne(optional = false)
    private SsUsuario progUsrSponsorFk;
    @JoinColumn(name = "prog_usr_adjunto_fk", referencedColumnName = "usu_id")
    @ManyToOne(optional = false)
    private SsUsuario progUsrAdjuntoFk;
    @JoinColumn(name = "prog_usr_gerente_fk", referencedColumnName = "usu_id")
    @ManyToOne(optional = false)
    private SsUsuario progUsrGerenteFk;
    @JoinColumn(name = "prog_est_fk", referencedColumnName = "est_pk")
    @ManyToOne(optional = false)
    private Estados progEstFk;
    @JoinColumn(name = "prog_est_pendiente_fk", referencedColumnName = "est_pk")
    @ManyToOne(optional = true)
    private Estados progEstPendienteFk;
    @JoinColumn(name = "prog_org_fk", referencedColumnName = "org_pk")
    @ManyToOne(optional = false)
    private Organismos progOrgFk;
    @JoinColumn(name = "prog_area_fk", referencedColumnName = "area_pk")
    @ManyToOne(optional = false)
    private Areas progAreaFk;
    /**
     * Indica si el Programa está activo o eliminado logicamente.
     */
    @Column(name = "prog_activo")
    private Boolean activo;
    @Column(name = "prog_habilitado")
    private Boolean progHabilitado = true;

    @JoinColumn(name = "prog_cro_fk", referencedColumnName = "cro_pk")
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Cronogramas progCroFk;

    @JoinColumn(name = "prog_progindices_fk", referencedColumnName = "progind_pk")
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private ProgIndices progIndices;
    @Column(name = "prog_fecha_crea")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date progFechaCrea;
    @Column(name = "prog_fecha_act")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date progFechaAct;
    //Audit
    @Column(name = "prog_ult_usuario")
    @AtributoUltimoUsuario
    private String progUltUsuario;
    @Column(name = "prog_ult_mod")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @AtributoUltimaModificacion
    private Date progUltMod;
    @Column(name = "prog_ult_origen")
    private String progUltOrigen;
    @Column(name = "prog_version")
    // @Version
    private Integer progVersion;

    @JoinColumn(name = "prog_obj_est_fk", referencedColumnName = "obj_est_pk")
    @ManyToOne(fetch = FetchType.EAGER, optional = true)
    private ObjetivoEstrategico objetivoEstrategico;

    public Programas() {
    }

    public Programas(Integer progPk) {
        this.progPk = progPk;
    }

    public Programas(Integer progPk, String progNombre) {
        this.progPk = progPk;
        this.progNombre = progNombre;
    }

    public Programas(String progNombre, String progObjetivo, String progObjPublico,
            String progGrp, Integer progSemaforoAmarillo, Integer progSemaforoRojo,
            Set<Areas> areasRestringidasSet, List<Interesados> interesadosList,
            Set<Proyectos> proyectosSet, SsUsuario progUsrPmofedFk,
            SsUsuario progUsrSponsorFk, SsUsuario progUsrAdjuntoFk, SsUsuario progUsrGerenteFk,
            Estados progEstFk, Organismos progOrgFk, Areas progAreaFk) {
        this.progNombre = progNombre;
        this.progObjetivo = progObjetivo;
        this.progObjPublico = progObjPublico;
        this.progGrp = progGrp;
        this.progSemaforoAmarillo = progSemaforoAmarillo;
        this.progSemaforoRojo = progSemaforoRojo;
        this.areasRestringidasSet = areasRestringidasSet;
        this.interesadosList = interesadosList;
        this.proyectosSet = proyectosSet;
        this.progUsrPmofedFk = progUsrPmofedFk;
        this.progUsrSponsorFk = progUsrSponsorFk;
        this.progUsrAdjuntoFk = progUsrAdjuntoFk;
        this.progUsrGerenteFk = progUsrGerenteFk;
        this.progEstFk = progEstFk;
        this.progOrgFk = progOrgFk;
        this.progAreaFk = progAreaFk;
    }

    public String getNombreComboFicha() {
        return this.progNombre + " (" + this.progPk + ")";
    }

    public Integer getProgPk() {
        return progPk;
    }

    public void setProgPk(Integer progPk) {
        this.progPk = progPk;
    }

    public String getProgNombre() {
        return progNombre;
    }

    public void setProgNombre(String progNombre) {
        this.progNombre = progNombre;
    }

    public String getProgDescripcion() {
        return progDescripcion;
    }

    public void setProgDescripcion(String progDescripcion) {
        if (progDescripcion != null) {
            this.progDescripcion = Jsoup.clean(progDescripcion, Whitelist.basic());
        }
    }

    public String getProgObjetivo() {
        return progObjetivo;
    }

    public void setProgObjetivo(String progObjetivo) {
        if (progObjetivo != null) {
            this.progObjetivo = Jsoup.clean(progObjetivo, Whitelist.basic());
        }
    }

    public String getProgObjPublico() {
        return progObjPublico;
    }

    public void setProgObjPublico(String progObjPublico) {
        if (progObjPublico != null) {
            this.progObjPublico = Jsoup.clean(progObjPublico, Whitelist.basic());
        }
    }

    public String getProgFactorImpacto() {
        return progFactorImpacto;
    }

    public void setProgFactorImpacto(String progFactorImpacto) {
        if (progFactorImpacto != null) {
            this.progFactorImpacto = Jsoup.clean(progFactorImpacto, Whitelist.basic());
        }
    }

    public String getProgGrp() {
        return progGrp;
    }

    public void setProgGrp(String progGrp) {
        this.progGrp = progGrp;
    }

    public Set<AreasTags> getAreasTematicasSet() {
        return areasTematicasSet;
    }

    public void setAreasTematicasSet(Set<AreasTags> areasTematicasSet) {
        this.areasTematicasSet = areasTematicasSet;
    }

    @XmlTransient
    public Set<Areas> getAreasRestringidasSet() {
        return areasRestringidasSet;
    }

    public void setAreasRestringidasSet(Set<Areas> areasSet) {
        this.areasRestringidasSet = areasSet;
    }

    public List<Interesados> getInteresadosList() {
        return interesadosList;
    }

    public void setInteresadosList(List<Interesados> interesadosList) {
        this.interesadosList = interesadosList;
    }

    public Presupuesto getProgPreFk() {
        return progPreFk;
    }

    public void setProgPreFk(Presupuesto progPreFk) {
        this.progPreFk = progPreFk;
    }

    public Set<Documentos> getDocumentosSet() {
        return documentosSet;
    }

    public void setDocumentosSet(Set<Documentos> documentosSet) {
        this.documentosSet = documentosSet;
    }

    @XmlTransient
    public Set<Proyectos> getProyectosSet() {
        return proyectosSet;
    }

    public void setProyectosSet(Set<Proyectos> proyectosSet) {
        this.proyectosSet = proyectosSet;
    }

    public Estados getProgEstFk() {
        return progEstFk;
    }

    public void setProgEstFk(Estados progEstFk) {
        this.progEstFk = progEstFk;
    }

    public Estados getProgEstPendienteFk() {
        return progEstPendienteFk;
    }

    public void setProgEstPendienteFk(Estados progEstPendienteFk) {
        this.progEstPendienteFk = progEstPendienteFk;
    }

    public boolean isEstPendienteFk() {
        return progEstPendienteFk != null;
    }

    public Organismos getProgOrgFk() {
        return progOrgFk;
    }

    public void setProgOrgFk(Organismos progOrgFk) {
        this.progOrgFk = progOrgFk;
    }

    public Areas getProgAreaFk() {
        return progAreaFk;
    }

    public void setProgAreaFk(Areas progAreaFk) {
        this.progAreaFk = progAreaFk;
    }

    public SsUsuario getProgUsrPmofedFk() {
        return progUsrPmofedFk;
    }

    public void setProgUsrPmofedFk(SsUsuario progUsrPmofedFk) {
        this.progUsrPmofedFk = progUsrPmofedFk;
    }

    public SsUsuario getProgUsrSponsorFk() {
        return progUsrSponsorFk;
    }

    public void setProgUsrSponsorFk(SsUsuario progUsrSponsorFk) {
        this.progUsrSponsorFk = progUsrSponsorFk;
    }

    public SsUsuario getProgUsrAdjuntoFk() {
        return progUsrAdjuntoFk;
    }

    public void setProgUsrAdjuntoFk(SsUsuario progUsrAdjuntoFk) {
        this.progUsrAdjuntoFk = progUsrAdjuntoFk;
    }

    public SsUsuario getProgUsrGerenteFk() {
        return progUsrGerenteFk;
    }

    public void setProgUsrGerenteFk(SsUsuario progUsrGerenteFk) {
        this.progUsrGerenteFk = progUsrGerenteFk;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (progPk != null ? progPk.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Programas)) {
            return false;
        }
        Programas other = (Programas) object;
        if ((this.progPk == null && other.progPk != null) || (this.progPk != null && !this.progPk.equals(other.progPk))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sofis.entities.data.Programas[ progPk=" + progPk + " ]";
    }

    public Integer getProgSemaforoRojo() {
        return progSemaforoRojo;
    }

    public void setProgSemaforoRojo(Integer progSemaforoRojo) {
        this.progSemaforoRojo = progSemaforoRojo;
    }

    public Integer getProgSemaforoAmarillo() {
        return progSemaforoAmarillo;
    }

    public void setProgSemaforoAmarillo(Integer progSemaforoAmarillo) {
        this.progSemaforoAmarillo = progSemaforoAmarillo;
    }

    public Boolean getActivo() {
        return activo;
    }

    public Date getProgFechaCrea() {
        return progFechaCrea;
    }

    public void setProgFechaCrea(Date progFechaCrea) {
        this.progFechaCrea = progFechaCrea;
    }

    public Date getProgFechaAct() {
        return progFechaAct;
    }

    public void setProgFechaAct(Date progFechaAct) {
        this.progFechaAct = progFechaAct;
    }

    public Cronogramas getProgCroFk() {
        return progCroFk;
    }

    public void setProgCroFk(Cronogramas progCroFk) {
        this.progCroFk = progCroFk;
    }

    public ProgIndices getProgIndices() {
        return progIndices;
    }

    public void setProgIndices(ProgIndices progIndices) {
        this.progIndices = progIndices;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public String getProgUltUsuario() {
        return progUltUsuario;
    }

    public void setProgUltUsuario(String progUltUsuario) {
        this.progUltUsuario = progUltUsuario;
    }

    public Date getProgUltMod() {
        return progUltMod;
    }

    public void setProgUltMod(Date progUltMod) {
        this.progUltMod = progUltMod;
    }

    public String getProgUltOrigen() {
        return progUltOrigen;
    }

    public void setProgUltOrigen(String progUltOrigen) {
        this.progUltOrigen = progUltOrigen;
    }

    public Integer getProgVersion() {
        return progVersion;
    }

    public void setProgVersion(Integer progVersion) {
        this.progVersion = progVersion;
    }

    public void toSystemOut() {
        logger.info("-- Programas --");
        logger.log(Level.INFO, "Pk:{0}", this.progPk);
        logger.log(Level.INFO, "Nombre:{0}", this.progNombre);
        logger.log(Level.INFO, "Estado:{0}", this.progEstFk);
    }

    /**
     * Compara el estado aportado con el del programa.
     *
     * @param e
     * @return true si es el estado aportado.
     */
    public boolean isEstado(Integer estPk) {
        return this.getProgEstFk().isEstado(estPk);
    }

    public boolean isActivo() {
        return activo != null ? activo : false;
    }

    /**
     * Indica si el programa esta en la fase de pendiente. Ej.: PENDIENTE,
     * PENDIENTE_PMOT, PENDIENTE_PMOF.
     *
     * @return true si el estado es uno de los estados pendiente.
     */
    public boolean isEstadoPendientes() {
        return this.getProgEstFk().isPendientes();

    }

    /**
     * @return the objetivoEstrategico
     */
    public ObjetivoEstrategico getObjetivoEstrategico() {
        return objetivoEstrategico;
    }

    /**
     * @param objetivoEstrategico the objetivoEstrategico to set
     */
    public void setObjetivoEstrategico(ObjetivoEstrategico objetivoEstrategico) {
        this.objetivoEstrategico = objetivoEstrategico;
    }

    public Boolean getProgHabilitado() {
        return progHabilitado;
    }

    public void setProgHabilitado(Boolean progHabilitado) {
        this.progHabilitado = progHabilitado;
    }
}
