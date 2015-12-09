package eventos;

import interfaz.Simulacion;

/**
 * Created by angel on 07/12/2015.
 */
public class EventoDescarga extends Evento {

    public EventoDescarga(long t_inicio, long t_finalizacion, Simulacion sim) {
        super(t_inicio, t_finalizacion, sim);
    }

    @Override
    public void finaliza() {
        sim.setT_actual(t_finalizacion);

        if (! sim.hayHuecoMuelle()) {
            if (sim.hayBarcosEsperaEntrada()) {
                if (sim.hayRemolcadorEntrada()) {
                    sim.setRemolcadores_entrada(sim.getRemolcadores_entrada() - 1);
                    sim.setBarcos_camino_muelle(sim.getBarcos_camino_muelle() + 1);
                    sim.barcoDejaEsperaEntrada();
                    sim.generarNuevoEventoViajeMuelleCargado();
                } else {
                    if (sim.estanDeCaminoEntrada()){
                        sim.setBarcos_camino_entrada(sim.getBarcos_camino_entrada() - 1);
                    } else {
                        if (sim.getRemolcadores_puerto() > 0) {
                            sim.setRemolcadores_puerto(sim.getRemolcadores_puerto() - 1);
                            sim.generarNuevoEventoViajeEntradaVacio();
                        }
                    }
                    sim.setMuelles_ocupados(sim.getMuelles_ocupados() - 1);
                }
            } else {
                sim.setMuelles_ocupados(sim.getMuelles_ocupados() - 1);
            }
        } else {
            sim.setMuelles_ocupados(sim.getMuelles_ocupados() - 1);
        }

        if (sim.hayRemolcadorMuelle()) {
            sim.setBarcos_camino_entrada(sim.getBarcos_camino_entrada() + 1);
            sim.setRemolcadores_puerto(sim.getRemolcadores_puerto() - 1);
            sim.generarNuevoEventoViajeEntradaCragado();
        } else {
            if (sim.estanDeCaminoMuelle()) {
                sim.setBarcos_camino_muelle(sim.getBarcos_camino_muelle() - 1);
            } else {
                if (sim.hayRemolcadorEntrada()) {
                    sim.setRemolcadores_entrada(sim.getRemolcadores_entrada() - 1);
                    sim.generarNuevoEventeViajeMuelleVacio();
                }
            }
            sim.otroBarcoEsperaMuelle();
        }
    }
}
