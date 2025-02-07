package com.sofis.entities.data;

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
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Usuario
 */
@Entity
@Table(name = "ent_hist_linea_base")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "EntHistLineaBase.findAll", query = "SELECT e FROM EntHistLineaBase e"),
    @NamedQuery(name = "EntHistLineaBase.findByEnthistPk", query = "SELECT e FROM EntHistLineaBase e WHERE e.enthistPk = :enthistPk"),
    @NamedQuery(name = "EntHistLineaBase.findByEnthistInicioLineaBase", query = "SELECT e FROM EntHistLineaBase e WHERE e.enthistInicioLineaBase = :enthistInicioLineaBase"),
    @NamedQuery(name = "EntHistLineaBase.findByEnthistFinLineaBase", query = "SELECT e FROM EntHistLineaBase e WHERE e.enthistFinLineaBase = :enthistFinLineaBase")})
public class EntHistLineaBase implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "enthist_pk")
    private Integer enthistPk;
    @JoinColumn(name = "enthist_ent_fk", referencedColumnName = "ent_pk")
    @ManyToOne(fetch = FetchType.EAGER)
    private Entregables enthistEntregableFk;
    @Column(name = "enthist_fecha")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date enthistFecha;
    @Basic(optional = false)
    @NotNull
    @Column(name = "enthist_inicio_linea_base")
    private Long enthistInicioLineaBase;
    @Column(name = "enthist_fin_linea_base")
    private Long enthistFinLineaBase;
    @Column(name = "enthist_duracion")
    private Integer enthistDuracion;
    @JoinColumn(name = "enthist_replan_fk", referencedColumnName = "proyreplan_pk")
    @ManyToOne(fetch = FetchType.EAGER)
    private ProyReplanificacion enthistReplanFk;

    public EntHistLineaBase() {
    }

    public EntHistLineaBase(Integer enthistPk) {
        this.enthistPk = enthistPk;
    }

    public Integer getEnthistPk() {
        return enthistPk;
    }

    public void setEnthistPk(Integer enthistPk) {
        this.enthistPk = enthistPk;
    }

    public Entregables getEnthistEntregableFk() {
        return enthistEntregableFk;
    }

    public void setEnthistEntregableFk(Entregables enthistEntregableFk) {
        this.enthistEntregableFk = enthistEntregableFk;
    }

    public Date getEnthistFecha() {
        return enthistFecha;
    }

    public void setEnthistFecha(Date enthistFecha) {
        this.enthistFecha = enthistFecha;
    }

    public Long getEnthistInicioLineaBase() {
        return enthistInicioLineaBase;
    }

    public Date getEnthistInicioLineaBaseDate() {
        return enthistInicioLineaBase != null ? new Date(enthistInicioLineaBase) : null;
    }

    public void setEnthistInicioLineaBase(Long enthistInicioLineaBase) {
        this.enthistInicioLineaBase = enthistInicioLineaBase;
    }

    public Long getEnthistFinLineaBase() {
        return enthistFinLineaBase;
    }

    public Date getEnthistFinLineaBaseDate() {
        return enthistFinLineaBase != null ? new Date(enthistFinLineaBase) : null;
    }

    public void setEnthistFinLineaBase(Long enthistFinLineaBase) {
        this.enthistFinLineaBase = enthistFinLineaBase;
    }

    public Integer getEnthistDuracion() {
        return enthistDuracion;
    }

    public void setEnthistDuracion(Integer enthistDuracion) {
        this.enthistDuracion = enthistDuracion;
    }

    public ProyReplanificacion getEnthistReplanFk() {
        return enthistReplanFk;
    }

    public void setEnthistReplanFk(ProyReplanificacion enthistReplanFk) {
        this.enthistReplanFk = enthistReplanFk;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (enthistPk != null ? enthistPk.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        
        if (!(object instanceof EntHistLineaBase)) {
            return false;
        }
        EntHistLineaBase other = (EntHistLineaBase) object;
        if ((this.enthistPk == null && other.enthistPk != null) || (this.enthistPk != null && !this.enthistPk.equals(other.enthistPk))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sofis.entities.data.EntHistLineaBase[ enthistPk=" + enthistPk + " ]";
    }
}
