package com.sofis.web.mb;

import com.sofis.entities.constantes.ConstanteApp;
import com.sofis.entities.data.AreasTags;
import com.sofis.exceptions.BusinessException;
import com.sofis.generico.utils.generalutils.CollectionsUtils;
import com.sofis.web.componentes.SofisPopupUI;
import com.sofis.web.delegates.AreaTematicaDelegate;
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
 * @author Usuario
 */
@ManagedBean(name = "areaTematicaMB")
@ViewScoped
public class AreaTematicaMB implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(AreaTematicaMB.class.getName());
    //private static final String BUSQUEDA_MSG = "busquedaMsg";
    private static final String POPUP_MSG = "popupMsg";
    private static final String AREAS_TAGS_NOMBRE = "areatagNombre";

    @ManagedProperty("#{inicioMB}")
    private InicioMB inicioMB;
    @Inject
    private SofisPopupUI renderPopupEdicion;
    @Inject
    private AreaTematicaDelegate areaTematicaDelegate;

    // Variables
    private String cantElementosPorPagina = "25";
    private String elementoOrdenacion = AREAS_TAGS_NOMBRE;
    // 0=descendente, 1=ascendente.
    private int ascendente = 1;
    private String filtroNombre;
    private List<AreasTags> listaResultado;
    private AreasTags areaTemEnEdicion;
    private List<AreasTags> listAreasTags;
    private SofisCombo listaAreasTagsCombo;

    public AreaTematicaMB() {
    }

    public void setInicioMB(InicioMB inicioMB) {
        this.inicioMB = inicioMB;
    }

    @PostConstruct
    public void init() {

        /*
        *   30-05-2018 Nico: Se sacan las variables que se inicializan del constructor y se pasan al PostConstruct
        */        
        
        filtroNombre = "";
        listaResultado = new ArrayList<AreasTags>();
        areaTemEnEdicion = new AreasTags();
        listAreasTags = new ArrayList<AreasTags>();
        listaAreasTagsCombo = new SofisCombo();        
        
        inicioMB.cargarOrganismoSeleccionado();

        buscar();
    }

    public String getFiltroNombre() {
        return filtroNombre;
    }

    public void setFiltroNombre(String filtroNombre) {
        this.filtroNombre = filtroNombre;
    }

    public List<AreasTags> getListaResultado() {
        return listaResultado;
    }

    public void setListaResultado(List<AreasTags> listaResultado) {
        this.listaResultado = listaResultado;
    }

    public AreasTags getAreaTemEnEdicion() {
        return areaTemEnEdicion;
    }

    public void setAreaTemEnEdicion(AreasTags areaTemEnEdicion) {
        this.areaTemEnEdicion = areaTemEnEdicion;
    }

    public SofisPopupUI getRenderPopupEdicion() {
        return renderPopupEdicion;
    }

    public void setRenderPopupEdicion(SofisPopupUI renderPopupEdicion) {
        this.renderPopupEdicion = renderPopupEdicion;
    }

    public List<AreasTags> getListAreasTags() {
        return listAreasTags;
    }

    public void setListAreasTags(List<AreasTags> listAreasTags) {
        this.listAreasTags = listAreasTags;
    }

    public SofisCombo getListaAreasTagsCombo() {
        return listaAreasTagsCombo;
    }

    public void setListaAreasTagsCombo(SofisCombo listaAreasTagsCombo) {
        this.listaAreasTagsCombo = listaAreasTagsCombo;
    }

    public String getCantElementosPorPagina() {
        return cantElementosPorPagina;
    }

    public void setCantElementosPorPagina(String cantElementosPorPagina) {
        this.cantElementosPorPagina = cantElementosPorPagina;
    }

    public String getElementoOrdenacion() {
        return elementoOrdenacion;
    }

    public void setElementoOrdenacion(String elementoOrdenacion) {
        this.elementoOrdenacion = elementoOrdenacion;
    }

    public int getAscendente() {
        return ascendente;
    }

    public void setAscendente(int ascendente) {
        this.ascendente = ascendente;
    }

    /**
     * Action agregar.
     *
     * @return
     */
    public String agregar() {
        areaTemEnEdicion = new AreasTags();

        listAreasTags = areaTematicaDelegate.busquedaAreaTemFiltro(inicioMB.getOrganismo().getOrgPk(), null, elementoOrdenacion, ascendente);
        if (listaResultado != null) {
            listaAreasTagsCombo = new SofisCombo((List) listAreasTags, AREAS_TAGS_NOMBRE);
            listaAreasTagsCombo.addEmptyItem(Labels.getValue("comboEmptyItem"));
        }

        renderPopupEdicion.abrir();
        return null;
    }

    /**
     * Action eliminar un area tematica.
     *
     * @return
     */
    public String eliminar(Integer atPk) {
        if (atPk != null) {
            try {
                areaTematicaDelegate.eliminarAreaTematica(atPk);
                for (AreasTags at : listaResultado) {
                    if (at.getArastagPk().equals(atPk)) {
                        listaResultado.remove(at);
                        break;
                    }
                }
            } catch (BusinessException e) {
                logger.log(Level.SEVERE, null, e);
                
                
                /*
                *  18-06-2018 Inspección de código.
                */

                //JSFUtils.agregarMsgs(BUSQUEDA_MSG, e.getErrores());

                for(String iterStr : e.getErrores()){
                    JSFUtils.agregarMsgError("", Labels.getValue(iterStr), null);                
                }
                inicioMB.setRenderPopupMensajes(Boolean.TRUE);
            }
        }
        return null;
    }

    /**
     * Action Buscar.
     *
     * @return
     */
    public String buscar() {
        listaResultado = areaTematicaDelegate.busquedaAreaTemFiltro(inicioMB.getOrganismo().getOrgPk(), filtroNombre, elementoOrdenacion, ascendente);

        return null;
    }

    public String editar(Integer atPk) {
        try {
            areaTemEnEdicion = areaTematicaDelegate.obtenerAreaTemPorPk(atPk);
        } catch (BusinessException ex) {
            logger.log(Level.SEVERE, null, ex);
                
            /*
            *  18-06-2018 Inspección de código.
            */

            //JSFUtils.agregarMsgs(POPUP_MSG, ex.getErrores());

            for(String iterStr : ex.getErrores()){
                JSFUtils.agregarMsgError(POPUP_MSG, Labels.getValue(iterStr), null);                
            }
            
        }

        listAreasTags = areaTematicaDelegate.busquedaAreaTemFiltro(inicioMB.getOrganismo().getOrgPk(), null, elementoOrdenacion, ascendente);
        quitarAreaDeLista(listAreasTags, areaTemEnEdicion);
        if (listaResultado != null) {
            listaAreasTagsCombo = new SofisCombo((List) listAreasTags, AREAS_TAGS_NOMBRE);
            listaAreasTagsCombo.addEmptyItem(Labels.getValue("comboEmptyItem"));
        }
        listaAreasTagsCombo.setSelectedObject(areaTemEnEdicion.getAreatagPadreFk());

        renderPopupEdicion.abrir();
        return null;
    }

    public String guardar() {
        AreasTags areasTagsSelected = (AreasTags) listaAreasTagsCombo.getSelectedObject();

        areaTemEnEdicion.setAreatagOrgFk(inicioMB.getOrganismo());
        areaTemEnEdicion.setAreatagPadreFk(areasTagsSelected);

        try {
            areaTemEnEdicion = areaTematicaDelegate.guardarAreaTematica(areaTemEnEdicion);

            if (areaTemEnEdicion != null) {
                renderPopupEdicion.cerrar();
                buscar();
            }
        } catch (BusinessException be) {
            logger.log(Level.SEVERE, be.getMessage(), be);
  
            /*
            *  18-06-2018 Inspección de código.
            */

            //JSFUtils.agregarMsgs(BUSQUEDA_MSG, be.getErrores());

            for(String iterStr : be.getErrores()){
                JSFUtils.agregarMsgError(POPUP_MSG, Labels.getValue(iterStr), null);                
            }
            
        }
        return null;
    }

    public void cancelar() {
        renderPopupEdicion.cerrar();
    }

    /**
     * Action limpiar formulario de busqueda.
     *
     * @return
     */
    public String limpiar() {
        filtroNombre = null;
        listaResultado = null;
        elementoOrdenacion = AREAS_TAGS_NOMBRE;
        ascendente = 1;

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
        ascendente = Integer.valueOf(evt.getNewValue().toString());
        buscar();
    }

    private void quitarAreaDeLista(List<AreasTags> listAreasTags, AreasTags areaTemEnEdicion) {
        if (CollectionsUtils.isNotEmpty(listAreasTags) && areaTemEnEdicion != null) {
            for (AreasTags at : listAreasTags) {
                if (at.getArastagPk().equals(areaTemEnEdicion.getArastagPk())) {
                    listAreasTags.remove(at);
                    break;
                }
            }
        }
    }
}
