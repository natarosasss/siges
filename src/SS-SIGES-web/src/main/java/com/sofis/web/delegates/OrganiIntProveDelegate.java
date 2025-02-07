package com.sofis.web.delegates;

import com.sofis.business.ejbs.OrganiIntProveBean;
import com.sofis.entities.data.OrganiIntProve;
import com.sofis.exceptions.GeneralException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;

/**
 *
 * @author Usuario
 */
public class OrganiIntProveDelegate implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    OrganiIntProveBean organiIntProveBean;

    public List<OrganiIntProve> obtenerProveedores(Integer orgPk) {
        return organiIntProveBean.obtenerProveedores(orgPk);
    }
    
    public List<OrganiIntProve> obtenerOrgani(Integer orgPk) {
        return organiIntProveBean.obtenerOrgani(orgPk);
    }
    
    public List<OrganiIntProve> obtenerInteresados(Integer orgPk) {
        return organiIntProveBean.obtenerInteresados(orgPk);
    }
 
    public List<OrganiIntProve> obtenerOrganiIntProvePorOrgPk(Integer orgPk, Boolean proveedor) throws GeneralException {
        return organiIntProveBean.obtenerOrganiIntProvePorOrgPk(orgPk, proveedor);
    }
    
    public OrganiIntProve obtenerOrganiIntProvePorId(Integer id) throws GeneralException {
        return organiIntProveBean.obtenerOrganiIntProvePorId(id);
    }

    public void eliminarOrga(Integer orgaPk) {
        organiIntProveBean.eliminarOrga(orgaPk);
    }

    public List<OrganiIntProve> busquedaOrgaFiltro(Integer orgPk, Map<String, Object> mapFiltro, String elementoOrdenacion, int ascendente) {
        return organiIntProveBean.busquedaOrgaFiltro(orgPk, mapFiltro, elementoOrdenacion, ascendente);
    }

    public OrganiIntProve guardarOrga(OrganiIntProve orgaEnEdicion) {
        return organiIntProveBean.guardar(orgaEnEdicion);
    }
}
