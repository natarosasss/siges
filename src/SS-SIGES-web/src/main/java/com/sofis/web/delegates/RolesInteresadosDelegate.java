package com.sofis.web.delegates;

import com.sofis.business.ejbs.RolesInteresadosBean;
import com.sofis.entities.data.RolesInteresados;
import com.sofis.exceptions.GeneralException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;

/**
 *
 * @author Usuario
 */
public class RolesInteresadosDelegate implements Serializable {

    @Inject
    RolesInteresadosBean rolesInteresadosBean;

    public List<RolesInteresados> obtenerTodos() throws GeneralException {
        return rolesInteresadosBean.obtenerTodos();
    }

    public List<RolesInteresados> obtenerRolPorOrganizacionId(Integer orgaId) throws GeneralException {
        return rolesInteresadosBean.obtenerRolPorOrgPk(orgaId);
    }
    
    public RolesInteresados obtenerRolesInteresadosPorId(Integer rolId){
        return rolesInteresadosBean.obtenerRolesInteresadosPorId(rolId);
    }

    public void eliminarRol(Integer rolPk) {
        rolesInteresadosBean.eliminarRol(rolPk);
    }

    public List<RolesInteresados> busquedaRolFiltro(Integer orgPk, Map<String, Object> mapFiltro, String elementoOrdenacion, int ascendente) {
        return rolesInteresadosBean.busquedaRolFiltro(orgPk, mapFiltro, elementoOrdenacion, ascendente);
    }

    public RolesInteresados guardarRol(RolesInteresados areaEnEdicion) {
        return rolesInteresadosBean.guardarRol(areaEnEdicion);
    }
}
