package eventos;

import interfaz.Simulacion;

/**
 * Created by angel on 07/12/2015.
 */
public class EventoViajeMuelleCargado extends Evento {

    public EventoViajeMuelleCargado(long t_inicio, long t_finalizacion, Simulacion sim) {
        super(t_inicio, t_finalizacion, sim);
    }

    @Override
    public void finaliza() {
        sim.setT_actual(t_finalizacion);

        sim.generarNuevoEventoDescarga();
        sim.setBarcos_camino_muelle(Math.max(sim.getBarcos_camino_muelle() - 1,0));

        if (sim.hayBarcosEsperaPuerto()) {
            sim.barcoDejaEsperaMuelle();
            sim.setBarcos_camino_entrada(sim.getBarcos_camino_entrada() + 1);
            sim.generarNuevoEventoViajeEntradaCragado();
        } else {
            if (sim.hayBarcosEsperaEntrada()) {
                sim.generarNuevoEventoViajeEntradaVacio();
            } else {
                sim.setRemolcadores_puerto(sim.getRemolcadores_puerto() + 1);
            }
        }

    }
}
