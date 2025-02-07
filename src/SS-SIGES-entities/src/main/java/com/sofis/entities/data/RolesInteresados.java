package com.sofis.entities.data;

import java.io.Serializable;
import java.util.Set;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

/**
 *
 * @author Usuario
 */
@Entity
@Table(name = "roles_interesados")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RolesInteresados.findAll", query = "SELECT r FROM RolesInteresados r"),
    @NamedQuery(name = "RolesInteresados.findByOrganismo", query = "SELECT r FROM RolesInteresados r WHERE r.rolintOrgFk.orgPk = :org_pk ORDER BY r.rolintNombre"),
})
public class RolesInteresados implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rolint_pk")
    private Integer rolintPk;
    @Size(max = 45)
    @Column(name = "rolint_nombre")
    private String rolintNombre;
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "intRolintFk")
    @Fetch(FetchMode.SELECT)
    private Set<Interesados> interesadosSet;
    @JoinColumn(name = "rolint_org_fk", referencedColumnName = "org_pk")
    @ManyToOne(optional = false)
    private Organismos rolintOrgFk;

    public RolesInteresados() {
    }

    public RolesInteresados(Integer rolintPk) {
        this.rolintPk = rolintPk;
    }

    public RolesInteresados(Integer rolintPk, String rolintNombre) {
        this.rolintPk = rolintPk;
        this.rolintNombre = rolintNombre;
    }

    public Integer getRolintPk() {
        return rolintPk;
    }

    public void setRolintPk(Integer rolintPk) {
        this.rolintPk = rolintPk;
    }

    public String getRolintNombre() {
        return rolintNombre;
    }

    public void setRolintNombre(String rolintNombre) {
        this.rolintNombre = rolintNombre;
    }

    public Organismos getRolintOrgFk() {
        return rolintOrgFk;
    }

    public void setRolintOrgFk(Organismos rolintOrgFk) {
        this.rolintOrgFk = rolintOrgFk;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (rolintPk != null ? rolintPk.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof RolesInteresados)) {
            return false;
        }
        RolesInteresados other = (RolesInteresados) object;
        if ((this.rolintPk == null && other.rolintPk != null) || (this.rolintPk != null && !this.rolintPk.equals(other.rolintPk))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sofis.entities.data.RolesInteresados[ rolintPk=" + rolintPk + " ]";
    }

    @XmlTransient
    public Set<Interesados> getInteresadosSet() {
        return interesadosSet;
    }

    public void setInteresadosSet(Set<Interesados> interesadosSet) {
        this.interesadosSet = interesadosSet;
    }
}
