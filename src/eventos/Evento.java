package eventos;


import interfaz.Simulacion;

/**
 * Created by angel on 07/12/2015.
 */
public abstract class Evento implements Comparable<Evento> {

    protected long t_inicio;
    protected long t_finalizacion;
    protected Simulacion sim;

    public Evento (long t_inicio, long t_finalizacion, Simulacion sim) {
        this.t_inicio  = t_inicio;
        this.t_finalizacion = t_finalizacion;
        this.sim = sim;
    }

    abstract public void finaliza();

    @Override
    public int compareTo(Evento o) {
        int vuelta = 0;

        if (this.t_finalizacion > o.t_finalizacion) {
            vuelta = 1;
        } else if (this.t_finalizacion < o.t_finalizacion) {
            vuelta = -1;
        }

        return vuelta;
    }
}
