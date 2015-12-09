package interfaz;

import eventos.*;

import javax.swing.*;
import java.util.ArrayDeque;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Created by angel on 06/12/2015.
 */
public class Simulacion extends SwingWorker<String,Integer> {

    //---- parametros ----
    private final int n_muelles;
    private final long t_simulacion;

    //---- variables del sistema ----
    private int remolcadores_entrada;
    private int remolcadores_puerto;

    private int barcos_espera_entrada;
    private int barcos_espera_puerto;

    private int barcos_camino_muelle;
    private int barcos_camino_entrada;

    private int muelles_ocupados;

    private long t_actual;

    private PriorityQueue<Evento> colaEventos;

    //----- variables salida ----
    private int n_barcos;

    private long t_total_atracar;
    private long t_maximo_atracar;

    private int total_barcos_muelle;
    private int total_barcos_esperan_entrada;
    private int cambios_esperan_entrada;

    private int total_barcos_esperan_muelle;
    private int cambios_esperan_muelle;

    private int maximo_barcos_esperan_entrada;
    private int maximo_barcos_esperan_muelle;

    /**
     * Devuelve la hora del dia a partir del tiempo de simulacion(minutos de simulacion)
     * @param t_actual minutos que lleva corrienbdo la simulacion
     * @return hora del dia.
     */
    static public double minutosToHoraDelDia (long t_actual) {
        double vuelta;

        vuelta = t_actual / 60.0d;
        double dias = vuelta / 24.0d;
        vuelta = (dias - Math.floor(dias)) * 24.0d;

        return vuelta;
    }

    public Simulacion(int n_remolcadores, int n_muelles, long t_simulacion) {
        this.n_muelles = n_muelles;
        this.t_simulacion = t_simulacion;

        this.remolcadores_entrada = n_remolcadores;
        this.remolcadores_puerto = 0;

        this.barcos_espera_entrada = 0;
        this.barcos_espera_puerto = 0;

        this.barcos_camino_muelle = 0;
        this.barcos_camino_entrada = 0;

        this.muelles_ocupados = 0;

        this.t_actual = 0;

        this.colaEventos = new PriorityQueue<>();
        generarNuevoEventoLLegadaFerry();

        this.n_barcos = 0;

        this.t_total_atracar = 0;
        this.t_total_atracar = 0;
        this.t_maximo_atracar = 0;

        this.total_barcos_muelle = 0;
        this.total_barcos_esperan_entrada = 0;
        this.total_barcos_esperan_muelle = 0;
        this.maximo_barcos_esperan_entrada = 0;
        this.maximo_barcos_esperan_muelle = 0;

        this.cambios_esperan_entrada = 0;
        this.cambios_esperan_muelle = 0;
    }

    //######### GENERADORES DE EVENTOS ########################
    /**
     * Encola un nuevo evento de llegada de ferry al puerto
     */
    public void generarNuevoEventoLLegadaFerry() {
        if (t_actual <= t_simulacion) {
            Evento evt = new EventoLLegadaFerry(t_actual, t_actual + minutosSiguienteBarco(), this);
            colaEventos.add(evt);
        }
    }

    public void generarNuevoEventoViajeEntradaVacio() {
        Evento evt = new EventoViajeEntradaVacio(t_actual, t_actual + minutosViajeVacio(), this);
        colaEventos.add(evt);
    }

    public void generarNuevoEventoViajeEntradaCragado() {
        Evento evt = new EventoViajeEntradaCargado(t_actual, t_actual + minutosViajeCargado(), this);
        colaEventos.add(evt);
    }

    public void generarNuevoEventeViajeMuelleVacio() {
        Evento evt = new EventoViajeMuelleVacio(t_actual, t_actual + minutosViajeVacio(), this);
        colaEventos.add(evt);
    }

    public void generarNuevoEventoViajeMuelleCargado() {
        Evento evt = new EventoViajeMuelleCargado(t_actual, t_actual + minutosViajeCargado(), this);
        colaEventos.add(evt);
    }

    public void generarNuevoEventoDescarga() {
        long t_Descraga = minutosDescarga();

        t_maximo_atracar = Math.max(t_Descraga, t_maximo_atracar);
        t_total_atracar += t_Descraga;

        Evento evt = new EventoDescarga(t_actual, t_actual + t_Descraga, this);
        colaEventos.add(evt);
    }

    // ####### LOGICA #############################

    public void setT_actual(long t_actual) {
        this.t_actual = t_actual;
    }

    public boolean hayHuecoMuelle() {
        return (muelles_ocupados < n_muelles);
    }

    public boolean hayRemolcadorEntrada() {
        return (remolcadores_entrada > 0);
    }

    public boolean hayRemolcadorMuelle() {
        return (remolcadores_puerto > 0);
    }

    public boolean estanDeCaminoEntrada() {
        return (barcos_camino_entrada > 0);
    }

    public boolean estanDeCaminoMuelle() {
        return (barcos_camino_muelle > 0);
    }

    public boolean hayBarcosEsperaEntrada() {
        return (barcos_espera_entrada > 0);
    }

    public boolean hayBarcosEsperaPuerto() {
        return (barcos_espera_puerto > 0);
    }

    public void otroBarcoEsperaEntrada() {
        barcos_espera_entrada ++;
        total_barcos_esperan_entrada += barcos_espera_entrada;
        cambios_esperan_entrada ++;
        maximo_barcos_esperan_entrada = Math.max(barcos_espera_entrada, maximo_barcos_esperan_entrada);
    }

    public void otroBarcoEsperaMuelle() {
        barcos_espera_puerto ++;
        total_barcos_esperan_muelle += barcos_espera_puerto;
        cambios_esperan_muelle ++;
        maximo_barcos_esperan_muelle = Math.max(barcos_espera_puerto, maximo_barcos_esperan_muelle);
    }

    public void barcoDejaEsperaEntrada() {
        barcos_espera_entrada --;
        total_barcos_esperan_entrada += barcos_espera_entrada;
        cambios_esperan_entrada ++;
    }

    public void barcoDejaEsperaMuelle() {
        barcos_espera_puerto --;
        total_barcos_esperan_muelle += barcos_espera_puerto;
        cambios_esperan_muelle++;
    }

    @Override
    protected String doInBackground() throws Exception {
        while (t_actual <= t_simulacion) {
            Evento evt = colaEventos.poll();

            if (evt == null) {
                throw new Exception("Error fatal, cola de eventos vacia");
            } else {
                evt.finaliza();
            }
        }

        while (!colaEventos.isEmpty()) {
            colaEventos.poll().finaliza();
        }

        return generaSalida();
    }

    private String generaSalida() {
        String vuelta = "";

        vuelta += "Tiempo medio en atracar: " + t_total_atracar / n_barcos + "\n"
                + "Tiempo maximo en atracar: " + t_maximo_atracar + "\n"
                + "numero medio barcos atracados: " + total_barcos_muelle / (n_barcos*2) + "\n"
                + "numero medio barcos esperan entrada " + total_barcos_esperan_entrada + "\n"
                + "numero medio barcos esperan muelle " + total_barcos_esperan_muelle + "\n"
                + "numero maximo barcos esperan entrada " + maximo_barcos_esperan_entrada + "\n"
                + "numero maximo barcos esperan muelle " + maximo_barcos_esperan_muelle + "\n";

        return vuelta;
    }

    //########  GETTERS Y SETTERS ################

    public int getN_muelles() {
        return n_muelles;
    }

    public long getT_simulacion() {
        return t_simulacion;
    }

    public int getRemolcadores_entrada() {
        return remolcadores_entrada;
    }

    public int getRemolcadores_puerto() {
        return remolcadores_puerto;
    }

    public int getBarcos_espera_entrada() {
        return barcos_espera_entrada;
    }

    public int getBarcos_espera_puerto() {
        return barcos_espera_puerto;
    }

    public int getBarcos_camino_muelle() {
        return barcos_camino_muelle;
    }

    public int getBarcos_camino_entrada() {
        return barcos_camino_entrada;
    }

    public int getMuelles_ocupados() {
        return muelles_ocupados;
    }

    public long getT_actual() {
        return t_actual;
    }

    public PriorityQueue<Evento> getColaEventos() {
        return colaEventos;
    }

    public int getN_barcos() {
        return n_barcos;
    }

    public long getT_total_atracar() {
        return t_total_atracar;
    }

    public long getT_maximo_atracar() {
        return t_maximo_atracar;
    }

    public int getTotal_barcos_muelle() {
        return total_barcos_muelle;
    }

    public int getTotal_barcos_esperan_entrada() {
        return total_barcos_esperan_entrada;
    }

    public int getTotal_barcos_esperan_muelle() {
        return total_barcos_esperan_muelle;
    }

    public int getMaximo_barcos_esperan_entrada() {
        return maximo_barcos_esperan_entrada;
    }

    public int getMaximo_barcos_esperan_muelle() {
        return maximo_barcos_esperan_muelle;
    }

    public void setN_barcos(int n_barcos) {
        this.n_barcos = n_barcos;
    }

    public void setRemolcadores_entrada(int remolcadores_entrada) {
        this.remolcadores_entrada = remolcadores_entrada;
    }

    public void setRemolcadores_puerto(int remolcadores_puerto) {
        this.remolcadores_puerto = remolcadores_puerto;
    }

    public void setMuelles_ocupados(int muelles_ocupados) {
        this.muelles_ocupados = muelles_ocupados;
    }

    public void setBarcos_camino_muelle(int barcos_camino_muelle) {
        this.barcos_camino_muelle = barcos_camino_muelle;
    }

    public void setBarcos_camino_entrada(int barcos_camino_entrada) {
        this.barcos_camino_entrada = barcos_camino_entrada;
    }

    //##########  GENERADORES DE TIEMPOS ###################################
    /**
     * Genera un valor de la distribucion exponencial, con la lamda segun la hora del dia, usando el metodo de inversion
     * @return minutos que tarda el siguiente barco en llegar
     */
    private long minutosSiguienteBarco() {
        double vuelta;

        float lamda = calcularLamda(t_actual);
        double rand = Math.random();
        vuelta = -(1.0d / lamda) * Math.log(rand);

        vuelta = Math.round(vuelta * 60.0d);

        return (long) vuelta;
    }

    /**
     * Calcula el tiempo que tarda un remolcador en hacer el viaje llevando un ferry
     * @return minutos que trada en hacer el viaje
     */
    private long minutosViajeCargado() {
        double minutos = generarValorNormal(10.0d, 3.0d);

        return Math.round(minutos);
    }

    /**
     * Calcula el tiempo que tarda un remolcador en hacer el viaje vacio
     * @return minutos que trada en hacer el viaje
     */
    private long minutosViajeVacio() {
        double minutos = generarValorNormal(2.0d, 1.0d);

        return Math.round(minutos);
    }

    /**
     * Calcula el tiempo que tarda en descargarse un barco
     * @return tiempo en minutos que tarda en descargarse un barco
     */
    private long minutosDescarga() {
        double minutos = generarTStudent(3);

        return Math.abs(Math.round(minutos));
    }

    /**
     * Genera un nuevo valor para t-Student
     * @param gradosLibertad entero con los grados de libertad
     * @return nuevo valor de la t-Student
     */
    private double generarTStudent(int gradosLibertad) {
        double z = generarValorNormalEstandar();
        double y = generarValorChiCuadrado(gradosLibertad);

        return z / (Math.sqrt(y/gradosLibertad));
    }

    /**
     * Devuelve un nuevo valor de la distribucion Chi cuadrado n
     * @param gradosLibertad entero con los grados de libertad
     * @return valor de la chicuadrado n
     */
    private double generarValorChiCuadrado(int gradosLibertad) {
        double vuelta = Math.pow(generarValorNormalEstandar(), 2);

        for ( int i=0; i < gradosLibertad - 1; i++) {
            vuelta += Math.pow(generarValorNormalEstandar(),2);
        }
        return vuelta;
    }

    /**
     * Genera un nuevo valor de la Normal(media, desviacion) a partir de un valor de la Normal(0,1)
     * @param media valor para la media
     * @param desviacion valor de la disviacion tipica
     * @return nuevo valor de la normal
     */
    private double generarValorNormal(double media, double desviacion) {
        double vuelta = generarValorNormalEstandar();

        vuelta = media + Math.sqrt(desviacion) * vuelta;
        return vuelta;
    }

    /**
     * Generamos un valor de la Normal(0,1) medianta la aproximacion de la suma de 12 Uniformes(0,1)
     * @return valor de la normal estandar
     */
    private double generarValorNormalEstandar() {
        /*
        double vuelta = Math.random();

        for (int i = 0; i < 11; i++) {
            vuelta += Math.random();
        }
        return vuelta - 6.0d;
        */

        double u = Math.random();

        return ((Math.pow(u,0.135d)  - Math.pow((1 - u), 0.135d)) / 0.1975d);
    }

    /**
     * Devuelve un valor de lamda segun la hora del dia
     * @param t_actual tiempo de simulacion actual
     * @return valor de lamda para esa hora del dia
     */
    private float calcularLamda (long t_actual) {
        double vuelta = -1;
        double hora = minutosToHoraDelDia(t_actual);

        if (hora >= 0.0d && hora <= 5.0d) {
            vuelta = 0.40d * hora + 1.0d;
        } else if ( hora <= 8.0d) {
            vuelta = -0.34d * hora + 4.67d;
        } else if ( hora <= 15.0d) {
            vuelta = 0.43d * hora - 1.43d;
        } else if (hora <= 17.0d) {
            vuelta = -1.5d * hora + 2.5d;
        } else if (hora <= 24) {
            vuelta = -0.14d * hora + 4.43d;
        }

        return (float)(vuelta);
    }
}
