package com.sofis.entities.tipos;

import com.sofis.generico.utils.generalutils.CollectionsUtils;
import com.sofis.generico.utils.generalutils.StringsUtils;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * TO utilizado para represntar los datos en el reporte de Productos y
 * Presupuesto.
 *
 */
public class ReporteAcumuladoTO implements Serializable {

    private static final long serialVersionUID = 1L;
    private String nombre;
    private String moneda;
    private String descPlan;
    private String descReal;
    private String descProyectado;
    private String titlePlan;
    private String titleReal;
    private String titleProyectado;
    private Boolean tieneProyectado = false;
    private List<ReporteAcumuladoMesTO> meses;
    /**
     * Titulo para la tabla de acumulados.
     */
    private String titulo;

    public ReporteAcumuladoTO() {
        this.meses = new ArrayList<ReporteAcumuladoMesTO>();
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public String getNombreRecortado(int largo) {
        return StringsUtils.recortarTexto(nombre, largo);
    }

    public String getDescPlan() {
        return descPlan;
    }

    public void setDescPlan(String descPlan) {
        this.descPlan = descPlan;
    }

    public String getDescReal() {
        return descReal;
    }

    public void setDescReal(String descReal) {
        this.descReal = descReal;
    }

    public String getDescProyectado() {
        return descProyectado;
    }

    public void setDescProyectado(String descProyectado) {
        this.descProyectado = descProyectado;
    }

    public String getTitlePlan() {
        return titlePlan;
    }

    public void setTitlePlan(String titlePlan) {
        this.titlePlan = titlePlan;
    }

    public String getTitleReal() {
        return titleReal;
    }

    public void setTitleReal(String titleReal) {
        this.titleReal = titleReal;
    }

    public String getTitleProyectado() {
        return titleProyectado;
    }

    public void setTitleProyectado(String titleProyectado) {
        this.titleProyectado = titleProyectado;
    }

    public Boolean getTieneProyectado() {
        return tieneProyectado;
    }

    public void setTieneProyectado(Boolean tieneProyectado) {
        this.tieneProyectado = tieneProyectado;
    }

    public List<ReporteAcumuladoMesTO> getMeses() {
        return meses;
    }

    public void setMeses(List<ReporteAcumuladoMesTO> meses) {
        this.meses = meses;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public ReporteAcumuladoMesTO getMesAnio(int anio, int mes) {
        if (CollectionsUtils.isNotEmpty(this.meses)) {
            for (ReporteAcumuladoMesTO acuMes : meses) {
                if (acuMes.getAnio() == anio && acuMes.getMes() == mes) {
                    return acuMes;
                }
            }
        }
        return null;
    }

    /**
     *
     * @param anio
     * @param mes
     * @param tipo 0=Plan, 1=Real.
     * @return
     */
//    public Double getMesAnio(int anio, int mes, int tipo) {
//        ReporteAcumuladoMesTO acuMes = getMesAnio(anio, mes);
//        if (acuMes != null) {
//            if (tipo == 0) {
//                return acuMes.getValorPlan();
//            } else if (tipo == 1) {
//                return acuMes.getValorRealFinalizado();
//            }
//        }
//        return null;
//    }

    public void setMes(short anio, short mes, Integer objPk, Double plan, Double real, String colorR) {
        ReporteAcumuladoMesTO acuMes = new ReporteAcumuladoMesTO(anio, mes, objPk, plan, real, colorR);
        this.getMeses().add(acuMes);
    }

    public void setMes(short anio, short mes, Integer objPk, Double plan, Double real, Double proyectado, Double proyectadoAtrasado, String colorR) {
        ReporteAcumuladoMesTO acuMes = new ReporteAcumuladoMesTO(anio, mes, objPk, plan, real, proyectado, proyectadoAtrasado, null, colorR);
        this.getMeses().add(acuMes);
    }

    public void setMes(short anio, short mes, Integer objPk, Double plan, Double real, Double proyectado, Double proyectadoAtrasado, Boolean proyectadoNegativo, String colorR) {
        ReporteAcumuladoMesTO acuMes = new ReporteAcumuladoMesTO(anio, mes, objPk, plan, real, proyectado, proyectadoAtrasado, proyectadoNegativo, colorR);
        this.getMeses().add(acuMes);
    }
}
