package eventos;

import interfaz.Simulacion;

/**
 * Created by angel on 07/12/2015.
 */
public class EventoLLegadaFerry extends Evento {


    public EventoLLegadaFerry(long t_inicio, long t_finalizacion, Simulacion sim) {
        super(t_inicio, t_finalizacion, sim);
    }

    @Override
    public void finaliza() {
        // marco que ha llegado un barco nuevo
        sim.setN_barcos(sim.getN_barcos() + 1);
        sim.setT_actual(t_finalizacion);
        sim.generarNuevoEventoLLegadaFerry();

        if (sim.hayHuecoMuelle()) {
            if (sim.hayRemolcadorEntrada()) {
                sim.setRemolcadores_entrada(sim.getRemolcadores_entrada() - 1);
                sim.setMuelles_ocupados(sim.getMuelles_ocupados() + 1);
                sim.setBarcos_camino_muelle(sim.getBarcos_camino_muelle() + 1);
                sim.generarNuevoEventoViajeMuelleCargado();
            } else {
                if (!sim.estanDeCaminoEntrada()) {
                    if (sim.hayRemolcadorMuelle()) {
                        sim.generarNuevoEventoViajeEntradaVacio();
                        sim.setRemolcadores_puerto(sim.getRemolcadores_puerto() - 1);
                    }
                } else {
                    sim.setBarcos_camino_entrada(sim.getBarcos_camino_entrada() - 1);
                }
                sim.otroBarcoEsperaEntrada();
            }
        } else {
            sim.otroBarcoEsperaEntrada();
        }
    }
}
