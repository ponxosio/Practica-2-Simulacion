package interfaz;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * Created by angel on 06/12/2015.
 */
public class MainPanel extends JPanel{

    BufferedImage img;
    JTextField tf_numero_ferrys;
    JTextField tf_numero_puertos;
    JTextField tf_tiempo_simulacion;

    public MainPanel() throws IOException {
        super();
        img = ImageIO.read(new File("img/fondo.png"));

        this.setPreferredSize(new Dimension(897, 530));
        this.setLayout(null);

        tf_numero_ferrys = new JTextField("00010");
        tf_numero_ferrys.setColumns(5);
        this.add(tf_numero_ferrys);

        tf_numero_puertos = new JTextField("00120");
        tf_numero_puertos.setColumns(5);
        this.add(tf_numero_puertos);

        tf_tiempo_simulacion = new JTextField("0000");
        tf_tiempo_simulacion.setColumns(4);
        this.add(tf_tiempo_simulacion);

        JButton iniciar = new JButton("Iniciar interfaz.Simulacion");
        iniciar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                iniciarSimulacion();
            }
        });
        this.add(iniciar);

        tf_numero_ferrys.setBounds(511, 130, 50, 20);
        tf_numero_puertos.setBounds(822, 103, 50, 20);
        tf_tiempo_simulacion.setBounds(493, 490, 50, 20);
        iniciar.setBounds(639, 480, 150, 40);
    }

    protected void iniciarSimulacion() {
        int n_ferrys = Integer.parseInt(tf_numero_ferrys.getText());
        int n_puertos = Integer.parseInt(tf_numero_puertos.getText());
        float t_simulacion = Float.parseFloat(tf_tiempo_simulacion.getText());

        Simulacion sim = new Simulacion(n_ferrys, n_puertos, Math.round(t_simulacion * 24f * 60f));
        sim.execute();
        try {
            String salida_html = sim.get();
            System.out.println(salida_html);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(img, 0, 0, this);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    JFrame frame = new JFrame();
                    JPanel panel = new MainPanel();
                    frame.setContentPane(panel);

                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    frame.setLocationRelativeTo(null);
                    frame.setResizable(false);

                    frame.pack();
                    frame.setVisible(true);

                } catch (IOException e) {
                    JOptionPane.showMessageDialog(null,"Error imposible iniciar, mesage asociado: " + e.getMessage(), "Error falta una imagen", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
}
