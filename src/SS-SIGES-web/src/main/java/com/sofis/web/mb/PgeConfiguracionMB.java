package com.sofis.web.mb;

import com.sofis.entities.data.PgeConfiguraciones;
import com.sofis.exceptions.GeneralException;
import com.sofis.generico.utils.generalutils.StringsUtils;
import com.sofis.persistence.dao.reference.EntityReference;
import com.sofis.sofisform.to.CriteriaTO;
import com.sofis.sofisform.to.MatchCriteriaTO;
import com.sofis.utils.CriteriaTOUtils;
import com.sofis.web.delegates.ConsultaHistoricoDelegate;
import com.sofis.web.delegates.PgeConfiguracionDelegate;
import com.sofis.web.genericos.constantes.ConstantesPresentacion;
import com.sofis.web.utils.EntityReferenceDataProvider;
import com.sofis.web.utils.LazyLoadingList;
import com.sofis.web.utils.ProcesarMensajes;
import com.sofis.web.utils.SofisCombo;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;

/**
 *
 * @author Usuario
 */
@ManagedBean(name = "pgeConfiguracionMB")
@ViewScoped
public class PgeConfiguracionMB implements Serializable {

    @Inject
    private ConsultaHistoricoDelegate<PgeConfiguraciones> histDelegate;
    @Inject
    private PgeConfiguracionDelegate pgeDelegate;

    private String bCodigo;
    private String bDescripcion;
    private SofisCombo comboCategoria = new SofisCombo();
    private List<EntityReference<Integer>> listaResultado = new ArrayList<EntityReference<Integer>>();
    private Boolean renderResultado = false;
    private PgeConfiguraciones cnfEnEdicion = new PgeConfiguraciones();
    private Boolean renderPopupEdicion = false;
    private List<PgeConfiguraciones> listaHitorial = new ArrayList<PgeConfiguraciones>();
    private Boolean renderPopupHistorial = false;
    private String cantElementosPorPagina = "25";
    private String elementoOrdenacion = "cnfClave";
    private String ascendente = "Ascendente";

    /**
     * Creates a new instance of PgeConfiguracionesMB
     */
    public PgeConfiguracionMB() {
    }

    private void reset() {
        bCodigo = "";
        bDescripcion = "";
        listaResultado = new ArrayList<EntityReference<Integer>>();
        renderResultado = false;
    }

    // <editor-fold defaultstate="collapsed" desc="eventos">
    public String buscar() {
        renderResultado = true;
        List<CriteriaTO> criterios = new ArrayList<CriteriaTO>();

        if (!StringsUtils.isEmpty(bCodigo)) {
            MatchCriteriaTO criterio = CriteriaTOUtils.createMatchCriteriaTO(
                    MatchCriteriaTO.types.CONTAINS, "cnfClave",
                    bCodigo.toUpperCase().trim());
            criterios.add(criterio);
        }

        if (!StringsUtils.isEmpty(bDescripcion)) {
            MatchCriteriaTO criterio = CriteriaTOUtils.createMatchCriteriaTO(
                    MatchCriteriaTO.types.CONTAINS, "cnfValor",
                    bDescripcion.toUpperCase().trim());
            criterios.add(criterio);
        }
        CriteriaTO condicion = null;
        if (!criterios.isEmpty()) {
            if (criterios.size() == 1) {
                condicion = criterios.get(0);
            } else {
                condicion = CriteriaTOUtils.createANDTO(criterios
                        .toArray(new CriteriaTO[0]));
            }
        } else {
            // condicion dummy para que el count by criteria funcione
            condicion = CriteriaTOUtils.createMatchCriteriaTO(
                    MatchCriteriaTO.types.NOT_NULL, "cnfId", 1);
        }
        String[] propiedades = {"cnfId", "cnfClave", "cnfValor"};
        String className = PgeConfiguraciones.class.getName();
        String[] orderBy = {elementoOrdenacion};
        boolean[] asc = {"Ascendente".equals(ascendente)};

        EntityReferenceDataProvider cd = new EntityReferenceDataProvider(
                propiedades, className, condicion, orderBy, asc);
        listaResultado = new LazyLoadingList(cd, ConstantesPresentacion.CANTIDAD_PAGINACION, ConstantesPresentacion.PAGINAS_BUFFERED, false);
        return null;
    }

    public String limpiar() {
        reset();
        return null;
    }

    public String agregar() {
        cnfEnEdicion = new PgeConfiguraciones();
        renderPopupEdicion = true;
        return null;
    }

    public String editar(Integer id) {
        try {
            cnfEnEdicion = pgeDelegate.obtenerPgeConfiguracionesPorId(id);
            renderPopupEdicion = true;
        } catch (GeneralException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String consultarHistorial(Integer id) {
        try {
            listaHitorial = histDelegate.obtenerHistorialPorId(PgeConfiguraciones.class, id, "cnfVersion");
            renderPopupHistorial = true;
        } catch (GeneralException ex) {
            ex.printStackTrace();
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String cerrarPopupHistorial() {
        renderPopupHistorial = false;
        return null;
    }

    public String guardar() {
        try {
            pgeDelegate.guardar(cnfEnEdicion);
            renderPopupEdicion = false;
            buscar();
        } catch (GeneralException ex) {
            for (FacesMessage s : ProcesarMensajes.obtenerMensajes(ex.getErrores())) {
                FacesContext.getCurrentInstance().addMessage("", s);
            }
        } catch (Exception ex) {

            FacesContext.getCurrentInstance().addMessage("", new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), ex.getMessage()));

        }
        return null;
    }

    public String cancelar() {
//        reset();
        renderPopupEdicion = false;
        return null;
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

    public void eliminar(Integer id) {
        try {
            pgeDelegate.eliminar(id);
            buscar();
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage("", new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), ex.getMessage()));
        }
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="getter-setter">
    public PgeConfiguraciones getCnfEnEdicion() {
        return cnfEnEdicion;
    }

    public void setCnfEnEdicion(PgeConfiguraciones cnfEnEdicion) {
        this.cnfEnEdicion = cnfEnEdicion;
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

    public String getbCodigo() {
        return bCodigo;
    }

    public void setbCodigo(String bCodigo) {
        this.bCodigo = bCodigo;
    }

    public String getbDescripcion() {
        return bDescripcion;
    }

    public void setbDescripcion(String bDescripcion) {
        this.bDescripcion = bDescripcion;
    }

    public List<EntityReference<Integer>> getListaResultado() {
        return listaResultado;
    }

    public void setListaResultado(List<EntityReference<Integer>> listaResultado) {
        this.listaResultado = listaResultado;
    }

    public Boolean getRenderResultado() {
        return renderResultado;
    }

    public void setRenderResultado(Boolean renderResultado) {
        this.renderResultado = renderResultado;
    }

    public Boolean getRenderPopupEdicion() {
        return renderPopupEdicion;
    }

    public void setRenderPopupEdicion(Boolean renderPopupEdicion) {
        this.renderPopupEdicion = renderPopupEdicion;
    }

    public List<PgeConfiguraciones> getListaHitorial() {
        return listaHitorial;
    }

    public void setListaHitorial(List<PgeConfiguraciones> listaHitorial) {
        this.listaHitorial = listaHitorial;
    }

    public Boolean getRenderPopupHistorial() {
        return renderPopupHistorial;
    }

    public void setRenderPopupHistorial(Boolean renderPopupHistorial) {
        this.renderPopupHistorial = renderPopupHistorial;
    }

    public String getCantElementosPorPagina() {
        return cantElementosPorPagina;
    }

    public void setCantElementosPorPagina(String cantElementosPorPagina) {
        this.cantElementosPorPagina = cantElementosPorPagina;
    }
    // </editor-fold>
}
