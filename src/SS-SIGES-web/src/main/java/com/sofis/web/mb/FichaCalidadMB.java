package com.sofis.web.mb;

import com.sofis.business.utils.CalidadUtils;
import com.sofis.business.utils.ComboItemTOUtils;
import com.sofis.business.utils.EntregablesUtils;
import com.sofis.business.utils.ProductosUtils;
import com.sofis.business.utils.TemasCalidadUtils;
import com.sofis.entities.constantes.MensajesNegocio;
import com.sofis.entities.data.Calidad;
import com.sofis.entities.data.Entregables;
import com.sofis.entities.data.Productos;
import com.sofis.entities.data.Proyectos;
import com.sofis.entities.data.TemasCalidad;
import com.sofis.entities.enums.TipoCalidadEnum;
import com.sofis.entities.tipos.ComboItemTO;
import com.sofis.entities.tipos.FiltroCalidadTO;
import com.sofis.exceptions.BusinessException;
import com.sofis.generico.utils.generalutils.CollectionsUtils;
import com.sofis.web.delegates.CalidadDelegate;
import com.sofis.web.delegates.EntregablesDelegate;
import com.sofis.web.delegates.ProductosDelegate;
import com.sofis.web.delegates.TemasCalidadDelegate;
import com.sofis.web.delegates.ValorCalidadCodigosDelegate;
import com.sofis.web.properties.Labels;
import com.sofis.web.utils.JSFUtils;
import com.sofis.web.utils.SofisComboG;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;

/**
 *
 * @author Usuario
 */
@ManagedBean(name = "fichaCalidadMB")
@ViewScoped
public class FichaCalidadMB implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(FichaCalidadMB.class.getName());
    private static final String MSG_FORMULARIO = "msgCalidad";
    private static final String MSG_BTN_GUARDAR = "msgCalidad";
    private static final String MSG_CALIDAD = "msgCalidad";

    //Inject
    @ManagedProperty("#{fichaMB}")
    private FichaMB fichaMB;
    @Inject
    private ValorCalidadCodigosDelegate valorCalidadCodigosDelegate;
    @Inject
    private TemasCalidadDelegate temasCalidadDelegate;
    @Inject
    private EntregablesDelegate entregablesDelegate;
    @Inject
    private ProductosDelegate productosDelegate;
    @Inject
    private CalidadDelegate calidadDelegate;

    // Variables
    private Double indiceCalidad;
    private String indiceCalidadColor;
    private List<Calidad> listaResumen;
    private List<Calidad> listaResultado;
    private SofisComboG<ComboItemTO> listaValorCalidadCombo;
    private SofisComboG<ComboItemTO> listaTipoCalidadCombo;
    private SofisComboG<TemasCalidad> listaAgregarTemaCalidaCombo;
    private SofisComboG<Entregables> listaAgregarEntregablesCombo;
    private SofisComboG<Productos> listaAgregarProductosCombo;

    public FichaCalidadMB() {
    }

    @PostConstruct
    public void init() {
        cargarCombos();
        cargarResumen();
        calcularIndiceCalidad();
//        buscarCalidadAction();
    }

    // <editor-fold defaultstate="collapsed" desc="getter-setter">
    public FichaMB getFichaMB() {
        return fichaMB;
    }

    public void setFichaMB(FichaMB fichaMB) {
        this.fichaMB = fichaMB;
    }

    public Double getIndiceCalidad() {
        return indiceCalidad;
    }

    public void setIndiceCalidad(Double indiceCalidad) {
        this.indiceCalidad = indiceCalidad;
    }

    public String getIndiceCalidadColor() {
        return indiceCalidadColor;
    }

    public void setIndiceCalidadColor(String indiceCalidadColor) {
        this.indiceCalidadColor = indiceCalidadColor;
    }

    public List<Calidad> getListaResumen() {
        return listaResumen;
    }

    public void setListaResumen(List<Calidad> listaResumen) {
        this.listaResumen = listaResumen;
    }

    public List<Calidad> getListaResultado() {
        return listaResultado;
    }

    public void setListaResultado(List<Calidad> listaResultado) {
        this.listaResultado = listaResultado;
    }

    public SofisComboG<TemasCalidad> getListaAgregarTemaCalidaCombo() {
        return listaAgregarTemaCalidaCombo;
    }

    public void setListaAgregarTemaCalidaCombo(SofisComboG<TemasCalidad> listaAgregarTemaCalidaCombo) {
        this.listaAgregarTemaCalidaCombo = listaAgregarTemaCalidaCombo;
    }

    public SofisComboG<Entregables> getListaAgregarEntregablesCombo() {
        return listaAgregarEntregablesCombo;
    }

    public void setListaAgregarEntregablesCombo(SofisComboG<Entregables> listaAgregarEntregablesCombo) {
        this.listaAgregarEntregablesCombo = listaAgregarEntregablesCombo;
    }

    public SofisComboG<Productos> getListaAgregarProductosCombo() {
        return listaAgregarProductosCombo;
    }

    public void setListaAgregarProductosCombo(SofisComboG<Productos> listaAgregarProductosCombo) {
        this.listaAgregarProductosCombo = listaAgregarProductosCombo;
    }

    public SofisComboG<ComboItemTO> getListaValorCalidadCombo() {
        return listaValorCalidadCombo;
    }

    public void setListaValorCalidadCombo(SofisComboG<ComboItemTO> listaValorCalidadCombo) {
        this.listaValorCalidadCombo = listaValorCalidadCombo;
    }

    public SofisComboG<ComboItemTO> getListaTipoCalidadCombo() {
        return listaTipoCalidadCombo;
    }

    public void setListaTipoCalidadCombo(SofisComboG<ComboItemTO> listaTipoCalidadCombo) {
        this.listaTipoCalidadCombo = listaTipoCalidadCombo;
    }

    // </editor-fold>
    public String mostrarFrameCalidad() {
        fichaMB.miMostrar(7L);
        cargarCombos();
//        cargarResumen();
        calcularIndiceCalidad();
        buscarCalidadAction();

        return null;
    }

    public String buscarCalidadAction() {
        ComboItemTO ciTipoCalidad = listaTipoCalidadCombo.getSelectedT();
        ComboItemTO ciValorCalidad = listaValorCalidadCombo.getSelectedT();

        FiltroCalidadTO filtro = new FiltroCalidadTO();
        filtro.setProyPk(fichaMB.getFichaTO().getFichaFk());
        if (ciTipoCalidad != null) {
            filtro.setTipo((Integer) ciTipoCalidad.getItemObject());
        }
        if (ciValorCalidad != null) {
            filtro.setValor((Integer) ciValorCalidad.getItemObject());
        }

        listaResultado = calidadDelegate.buscarPorFiltro(filtro, fichaMB.getInicioMB().getOrganismo().getOrgPk());
        listaResultado = CalidadUtils.sortByActualizacion(listaResultado, true);

        return null;
    }

    private Calidad agregarCalidad(Calidad cal) {
        if (cal != null) {
            cal.setCalProyFk(new Proyectos(fichaMB.getFichaTO().getFichaFk()));
            try {
                cal = calidadDelegate.guardar(cal, fichaMB.getInicioMB().getOrganismo().getOrgPk(), fichaMB.getInicioMB().getUsuario());
                if (cal != null) {
                    JSFUtils.agregarMsg(MSG_FORMULARIO, MensajesNegocio.INFO_CALIDAD_GUARDADO, null);
                    calcularIndiceCalidad();
                    cargarResumen();
                    resetAgregar();
                    buscarCalidadAction();
                }
            } catch (BusinessException be) {
                logger.log(Level.SEVERE, be.getMessage(), be);

                for(String iterStr : be.getErrores()){
                    JSFUtils.agregarMsgError(MSG_FORMULARIO, Labels.getValue(iterStr), null);                
                }                

            }
        }
        return cal;
    }

    public String agregarTemaCalidadAction() {
        TemasCalidad tca = listaAgregarTemaCalidaCombo.getSelectedT();
        if (tca != null) {
            Calidad cal = new Calidad();
            cal.setCalTcaFk(tca);
            agregarCalidad(cal);
        }
        resetAgregar();
        return null;
    }

    public String agregarEntregableAction() {
        Entregables ent = listaAgregarEntregablesCombo.getSelectedT();
        if (ent != null) {
            Calidad cal = new Calidad();
            cal.setCalEntFk(ent);
            agregarCalidad(cal);
        }
        resetAgregar();
        return null;
    }

    public String agregarProductosAction() {
        Productos prod = listaAgregarProductosCombo.getSelectedT();
        if (prod != null) {
            Calidad cal = new Calidad();
            cal.setCalProdFk(prod);
            agregarCalidad(cal);
        }
        resetAgregar();
        return null;
    }

    public String eliminarAction(Integer calPk) {
        if (calPk != null) {
            try {
                calidadDelegate.eliminar(calPk);

                List<Calidad> listAux = new ArrayList<>();
                for (Calidad c : listaResultado) {
                    if (!c.getCalPk().equals(calPk)) {
                        listAux.add(c);
                    }
                }
                listaResultado = listAux;

                calcularIndiceCalidad();
                cargarResumen();
                JSFUtils.agregarMsg(MSG_CALIDAD, MensajesNegocio.INFO_CALIDAD_ELIMINADO, null);
            } catch (BusinessException be) {
                logger.log(Level.SEVERE, be.getMessage(), be);

                for(String iterStr : be.getErrores()){
                    JSFUtils.agregarMsgError(MSG_CALIDAD, Labels.getValue(iterStr), null);                
                }                
                
            }
        }
        return null;
    }

    public String guardarTablaAction() {
        if (CollectionsUtils.isNotEmpty(listaResultado)) {
            try {
                listaResultado = calidadDelegate.guardar(listaResultado, 
						fichaMB.getInicioMB().getOrganismo().getOrgPk(), fichaMB.getInicioMB().getUsuario());

				calcularIndiceCalidad();
                JSFUtils.agregarMsg(MSG_CALIDAD, MensajesNegocio.INFO_CALIDAD_GUARDADO, null);
            } catch (BusinessException be) {
                logger.log(Level.SEVERE, null, be);
                
                for(String iterStr : be.getErrores()){
                    JSFUtils.agregarMsgError(MSG_CALIDAD, Labels.getValue(iterStr), null);                
                }                    

            }
        }
        return null;
    }

    private void cargarCombos() {
        List<ComboItemTO> listaValor = valorCalidadCodigosDelegate.obtenerTodosParaCombo();
        listaValor = ComboItemTOUtils.sortByTextoCombo(listaValor, true);
        if (listaValor != null) {
            listaValorCalidadCombo = new SofisComboG<>(listaValor, "itemNombre");
            listaValorCalidadCombo.addEmptyItem(Labels.getValue("comboTodos"));
        }

        List<ComboItemTO> listaTipoCal = new ArrayList<>();
        listaTipoCal.add(new ComboItemTO(TipoCalidadEnum.GENERAL.ordinal(), Labels.getValue("tca_general")));
        listaTipoCal.add(new ComboItemTO(TipoCalidadEnum.ENTREGABLE.ordinal(), Labels.getValue("tca_entregable")));
        listaTipoCal.add(new ComboItemTO(TipoCalidadEnum.PRODUCTO.ordinal(), Labels.getValue("tca_producto")));
        listaTipoCalidadCombo = new SofisComboG<>(listaTipoCal, "itemNombre");
        listaTipoCalidadCombo.addEmptyItem(Labels.getValue("comboTodos"));

        List<TemasCalidad> listTca = temasCalidadDelegate.obtenerPorOrg(fichaMB.getInicioMB().getOrganismo().getOrgPk());
        listTca = TemasCalidadUtils.sortByNombre(listTca, true);
        if (listTca != null) {
            listaAgregarTemaCalidaCombo = new SofisComboG<>(listTca, "tcaNombre");
            listaAgregarTemaCalidaCombo.addEmptyItem(Labels.getValue("comboEmptyItem"));
        }

        List<Entregables> listEnt = new ArrayList<>();
        Integer proyId = fichaMB.getFichaTO().getFichaFk();
        listEnt = entregablesDelegate.obtenerEntPorProyPk(proyId);
        listEnt = EntregablesUtils.cargarCamposCombos(listEnt);
        if (listEnt != null) {
            listaAgregarEntregablesCombo = new SofisComboG<>(listEnt, "nivelNombreCombo");
            listaAgregarEntregablesCombo.addEmptyItem(Labels.getValue("comboEmptyItem"));
        }

        List<Productos> listProd = productosDelegate.obtenerProdPorProyPk(proyId);
        listProd = ProductosUtils.sortByEntProdNombre(listProd, true);
        if (listProd != null) {
            listaAgregarProductosCombo = new SofisComboG<Productos>(listProd, "entProdNombre");
            listaAgregarProductosCombo.addEmptyItem(Labels.getValue("comboEmptyItem"));
        }
    }

    private void cargarResumen() {
        listaResumen = calidadDelegate.obtenerResumenCalidad(fichaMB.getFichaTO().getFichaFk(), 5);
    }

    private void calcularIndiceCalidad() {
        Integer proyId = fichaMB.getFichaTO().getFichaFk();
        Integer orgPk = fichaMB.getInicioMB().getOrganismo().getOrgPk();
        indiceCalidad = calidadDelegate.calcularIndiceCalidad(proyId, orgPk);
        indiceCalidadColor = calidadDelegate.calcularIndiceCalidadColor(indiceCalidad, orgPk);
    }

    private void resetAgregar() {
        listaAgregarTemaCalidaCombo.setSelected(-1);
        listaAgregarEntregablesCombo.setSelected(-1);
        listaAgregarProductosCombo.setSelected(-1);
    }

    public String tipoCalidadStr(Integer tipo) {
        return calidadDelegate.tipoCalidadStr(tipo, 
			fichaMB.getInicioMB().getOrganismoSeleccionado());
    }

    public String valorColorTabla(String cod) {
        return calidadDelegate.valorColorTabla(cod);
    }
}
