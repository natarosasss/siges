package com.sofis.entities.data;

import com.sofis.entities.tipos.FiltroInicioItem;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Usuario
 */
@Entity
@Table(name = "proy_replanificacion")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ProyReplanificacion.findAll", query = "SELECT p FROM ProyReplanificacion p"),
    @NamedQuery(name = "ProyReplanificacion.findByProyreplanPk", query = "SELECT p FROM ProyReplanificacion p WHERE p.proyreplanPk = :proyreplanPk"),
    @NamedQuery(name = "ProyReplanificacion.findByProyreplanFecha", query = "SELECT p FROM ProyReplanificacion p WHERE p.proyreplanFecha = :proyreplanFecha"),
    @NamedQuery(name = "ProyReplanificacion.findByProyreplanDesc", query = "SELECT p FROM ProyReplanificacion p WHERE p.proyreplanDesc = :proyreplanDesc"),
    @NamedQuery(name = "ProyReplanificacion.findByProyreplanHistorial", query = "SELECT p FROM ProyReplanificacion p WHERE p.proyreplanHistorial = :proyreplanHistorial")})
public class ProyReplanificacion implements Serializable {
    
    public static final int DESCRIPCION_LENGHT = 5000;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "proyreplan_pk")
    private Integer proyreplanPk;
    @Basic(optional = false)
    @NotNull
    @Column(name = "proyreplan_fecha")
    @Temporal(TemporalType.DATE)
    private Date proyreplanFecha;
    @Basic(optional = false)
    @NotNull
    @Column(name = "proyreplan_desc")
    private String proyreplanDesc;
    @Basic(optional = false)
    @NotNull
    @Column(name = "proyreplan_historial")
    private Boolean proyreplanHistorial;
    @Basic(optional = false)
    @NotNull
    @Column(name = "proyreplan_activo")
    private Boolean proyreplanActivo;
    @JoinColumn(name = "proyreplan_proy_fk", referencedColumnName = "proy_pk")
    @ManyToOne(fetch = FetchType.EAGER)
    private Proyectos proyectoFk;
    @Basic(optional = false)
    @NotNull
    @Column(name = "proyreplan_generar_linea_base")
    private boolean proyreplanGenerarLineaBase;
    
    @Basic(optional = false)
    @NotNull
    @Column(name = "proyreplan_generar_presupuesto")
    private boolean proyreplanGenerarPresupuesto;
    @Basic(optional = false)
    @NotNull
    @Column(name = "proyreplan_generar_producto")
    private boolean proyreplanGenerarProducto;
    
    
    /* 
    *        Este valor es utilizado para no permitir la edición del presupuesto
    *    al replanificar
    */
    @Basic(optional = false)
    @NotNull
    @Column(name = "proyreplan_permit_editar")
    private boolean proyreplanPermitEditar;
    
    
    @Transient
    private FiltroInicioItem item;

    public ProyReplanificacion() {
        proyreplanFecha = new Date();
    }

    public ProyReplanificacion(Integer proyreplanPk) {
        this.proyreplanPk = proyreplanPk;
    }

    public ProyReplanificacion(Integer proyreplanPk, Date proyreplanFecha, String proyreplanDesc, boolean proyreplanHistorial, Boolean proyreplanActivo) {
        this.proyreplanPk = proyreplanPk;
        this.proyreplanFecha = proyreplanFecha;
        this.proyreplanDesc = proyreplanDesc;
        this.proyreplanHistorial = proyreplanHistorial;
        this.proyreplanActivo = proyreplanActivo;
    }

    public Integer getProyreplanPk() {
        return proyreplanPk;
    }

    public void setProyreplanPk(Integer proyreplanPk) {
        this.proyreplanPk = proyreplanPk;
    }

    public Date getProyreplanFecha() {
        return proyreplanFecha;
    }

    public void setProyreplanFecha(Date proyreplanFecha) {
        this.proyreplanFecha = proyreplanFecha;
    }

    public String getProyreplanDesc() {
        return proyreplanDesc;
    }

    public void setProyreplanDesc(String proyreplanDesc) {
        this.proyreplanDesc = proyreplanDesc;
    }

    public Boolean getProyreplanHistorial() {
        return proyreplanHistorial != null && proyreplanHistorial;
    }

    public void setProyreplanHistorial(Boolean proyreplanHistorial) {
        this.proyreplanHistorial = proyreplanHistorial;
    }

    public Boolean getProyreplanActivo() {
        return proyreplanActivo != null && proyreplanActivo;
    }

    public void setProyreplanActivo(Boolean proyreplanActivo) {
        this.proyreplanActivo = proyreplanActivo;
    }

    public Proyectos getProyectoFk() {
        return proyectoFk;
    }

    public void setProyectoFk(Proyectos proyectoFk) {
        this.proyectoFk = proyectoFk;
    }

    public FiltroInicioItem getItem() {
        return item;
    }

    public void setItem(FiltroInicioItem item) {
        this.item = item;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (proyreplanPk != null ? proyreplanPk.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        
        if (!(object instanceof ProyReplanificacion)) {
            return false;
        }
        ProyReplanificacion other = (ProyReplanificacion) object;
        if ((this.proyreplanPk == null && other.proyreplanPk != null) || (this.proyreplanPk != null && !this.proyreplanPk.equals(other.proyreplanPk))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sofis.entities.data.ProyReplanificacion[ proyreplanPk=" + proyreplanPk + " ]";
    }

    /**
     * @return the proyreplanGenerarLineaBase
     */
    public boolean isProyreplanGenerarLineaBase() {
        return proyreplanGenerarLineaBase;
    }

    /**
     * @param proyreplanGenerarLineaBase the proyreplanGenerarLineaBase to set
     */
    public void setProyreplanGenerarLineaBase(boolean proyreplanGenerarLineaBase) {
        this.proyreplanGenerarLineaBase = proyreplanGenerarLineaBase;
    }

    public boolean isProyreplanGenerarPresupuesto() {
        return proyreplanGenerarPresupuesto;
    }

    public void setProyreplanGenerarPresupuesto(boolean proyreplanGenerarPresupuesto) {
        this.proyreplanGenerarPresupuesto = proyreplanGenerarPresupuesto;
    }

    public boolean isProyreplanGenerarProducto() {
        return proyreplanGenerarProducto;
    }

    public void setProyreplanGenerarProducto(boolean proyreplanGenerarProducto) {
        this.proyreplanGenerarProducto = proyreplanGenerarProducto;
    }

    public boolean isProyreplanPermitEditar() {
        return proyreplanPermitEditar;
    }

    public void setProyreplanPermitEditar(boolean proyreplanPermitEditar) {
        this.proyreplanPermitEditar = proyreplanPermitEditar;
    }
    
    
}
