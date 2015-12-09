package eventos;

import interfaz.Simulacion;

/**
 * Created by angel on 07/12/2015.
 */
public class EventoViajeEntradaVacio extends Evento {

    public EventoViajeEntradaVacio(long t_inicio, long t_finalizacion, Simulacion sim) {
        super(t_inicio, t_finalizacion, sim);
    }

    @Override
    public void finaliza() {
        sim.setT_actual(t_finalizacion);

        if (sim.hayBarcosEsperaEntrada()) {
            if (sim.hayHuecoMuelle()) {
                sim.setRemolcadores_entrada(sim.getRemolcadores_entrada() - 1);
                sim.setMuelles_ocupados(sim.getMuelles_ocupados() + 1);
                sim.setBarcos_camino_muelle(sim.getBarcos_camino_muelle() + 1);
                sim.barcoDejaEsperaEntrada();
                sim.generarNuevoEventoViajeMuelleCargado();
            } else {
                sim.setRemolcadores_entrada(sim.getRemolcadores_entrada() + 1);
            }
        } else {
            if (sim.hayBarcosEsperaPuerto()) {
                sim.generarNuevoEventeViajeMuelleVacio();
            } else {
                sim.setRemolcadores_entrada(sim.getRemolcadores_entrada() + 1);
            }
        }
    }
}
