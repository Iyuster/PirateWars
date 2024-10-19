package com.mycompany.piratewars;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Random;

public class PirateWars extends JPanel {

    private BarcoPirata barcoPirata; 
    private ArrayList<Disparo> disparos;
    private ArrayList<BarcoEnemigo> barcosEnemigos;
    private int puntos;
    private boolean juegoTerminado;
    private boolean juegoTerminado2;
    private double velocidad = 1;

    public PirateWars() {
    setBackground(Color.BLACK); // Cambia el color del fondo
    barcoPirata = new BarcoPirata(); // Instancia de la clase BarcoPirata corregida
    disparos = new ArrayList<>();
    barcosEnemigos = new ArrayList<>();
    puntos = 0;
    juegoTerminado = false;
    juegoTerminado2 = false;

    // Generar barcos enemigos
    Timer timer = new Timer(1000, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            int x = (int) (Math.random() * (getWidth() - 30));
            barcosEnemigos.add(new BarcoEnemigo(x, 0)); // Añade un nuevo BarcoEnemigo
        }
    });
    timer.start();

    // Control de movimiento del barco pirata y reinicio del juego
    addKeyListener(new KeyAdapter() {
        @Override
        public void keyPressed(KeyEvent e) {
            // Si el juego ha terminado y se presiona "R", reiniciar el juego
            if ((juegoTerminado || juegoTerminado2) && e.getKeyCode() == KeyEvent.VK_R) {
                reiniciarJuego();  // Llama al método para reiniciar el juego
            }

            // Si el juego no ha terminado, permite controlar el barco pirata
            if (!juegoTerminado && !juegoTerminado2) {
                barcoPirata.keyPressed(e);  // Control de movimiento del barco
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            barcoPirata.keyReleased(e);
        }
    });

    setFocusable(true); // Asegúrate de que el panel sea enfocable para recibir eventos de teclado
}
    
public void reiniciarJuego() {
    // Reiniciar variables y estado del juego
    barcoPirata = new BarcoPirata();  // Reiniciar barco
    disparos.clear();  // Limpiar disparos
    barcosEnemigos.clear();  // Limpiar enemigos
    puntos = 0;  // Reiniciar puntaje
    juegoTerminado = false;
    juegoTerminado2 = false;

    repaint();  // Redibujar la pantalla desde el inicio
}



    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (juegoTerminado) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 48));
            g.drawString("¡Has ganado!", getWidth() / 4, getHeight() / 2);
            g.setColor(Color.WHITE);  // Asegúrate de que el color sea blanco para el texto adicional
            g.setFont(new Font("Arial", Font.PLAIN, 24));
            g.drawString("Presiona 'R' para reiniciar", getWidth() / 4, getHeight() / 2 + 50);
            return; // No dibujar nada más
        }
    
        if (juegoTerminado2) {
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 48));
            g.drawString("¡Has perdido!", getWidth() / 4, getHeight() / 2);
            g.setColor(Color.WHITE);  // Asegúrate de que el color sea blanco para el texto adicional
            g.setFont(new Font("Arial", Font.PLAIN, 24));
            g.drawString("Presiona 'R' para reiniciar", getWidth() / 4, getHeight() / 2 + 50);
            return; // No dibujar nada más
        }

        barcoPirata.dibujar(g); // Se cambió de naveEspacial a barcoPirata

        // Dibujar disparos
        for (Disparo disparo : disparos) {
            disparo.mover();
            disparo.dibujar(g);
        }

        // Dibujar barcos enemigos y verificar colisiones
           ArrayList<Disparo> disparosRemover = new ArrayList<>();
           ArrayList<BarcoEnemigo> barcosEnemigosRemover = new ArrayList<>();

             for (BarcoEnemigo enemigo : barcosEnemigos) {
                  enemigo.mover(); // Mueve el barco enemigo
                  enemigo.actualizarDireccion(getWidth()); // Actualiza la dirección en función de los límites
                  enemigo.dibujar(g);

                for (Disparo disparo : disparos) {
                    if (disparo.intersecta(enemigo)) { // Se cambió naveEspacial por enemigo
                      disparosRemover.add(disparo);
                      barcosEnemigosRemover.add(enemigo);
                     puntos += 10;
                    }
                }

                if (barcoPirata.intersecta2(enemigo)) { // Si colisionan enemigo y jugador pierde partida
                   juegoTerminado2 = true;
                }

                if (enemigo.getY() > getHeight()) {
                    barcosEnemigosRemover.add(enemigo);
                }
            }

        disparos.removeAll(disparosRemover);
        barcosEnemigos.removeAll(barcosEnemigosRemover);

        g.setColor(Color.BLACK);
        g.drawString("Puntos: " + puntos, 10, 20);
        
        if (puntos == 100){
            velocidad = 1.5;
            if (puntos == 200){
                velocidad = 2;
                if (puntos == 300){
                    velocidad = 2.5;
                    if (puntos == 400){
                        velocidad = 3;  
                    }    
                }
            }
        }

        if (puntos == 500) {
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 48));
            g.drawString("¡Has ganado!", getWidth() / 4, getHeight() / 2);
            juegoTerminado = true;
        }
    }

class BarcoPirata {
    private int x, y;
    private boolean izquierda, derecha;
    private long ultimoDisparo; // Variable para almacenar el tiempo del último disparo
    private final long TIEMPO_RECARGA = 300; // Tiempo de recarga en milisegundos (0.5 segundos)

    public BarcoPirata() {
        x = 175;
        y = 600;
        izquierda = false;
        derecha = false;
        ultimoDisparo = 0; // Inicialmente, no hay disparos
    }

    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_A) {
            izquierda = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_D) {
            derecha = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            long tiempoActual = System.currentTimeMillis();
            if (tiempoActual - ultimoDisparo >= TIEMPO_RECARGA) { // Comprueba si ha pasado el tiempo de recarga
                disparos.add(new Disparo(x + 18, y)); // Disparo generado desde la posición del barco
                ultimoDisparo = tiempoActual; // Actualiza el tiempo del último disparo
            }
        }
        
    }   

    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_A) {
            izquierda = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_D) {
            derecha = false;
        }
        
    }

    public void dibujar(Graphics g) {
        if (izquierda && x > 0) {
            x -= 2;
        }
        if (derecha && x < getWidth() - 35) {
            x += 2;
        }
        g.setColor(Color.GREEN);
        g.fillRect(x, y, 40, 10); // Dibuja el barco pirata
    }
    
    
    
    public boolean intersecta2(BarcoEnemigo enemigo) { // Cambiado a BarcoEnemigo
        Rectangle areaBarco = new Rectangle(x, y, 40, 10);
        Rectangle areaEnemigo = new Rectangle(enemigo.getX(), enemigo.getY(), 30, 30); // Se corrigió enemigo
        return areaBarco.intersects(areaEnemigo); // Corregida la comparación de colisión
    }
}

    class BarcoEnemigo {
    private int x, y;
    private int direccion; // Controla la dirección horizontal
    private static final Random random = new Random();

    public BarcoEnemigo(int x, int y) {
        this.x = x;
        this.y = y;
        this.direccion = random.nextBoolean() ? 1 : -1; // Direcciones aleatorias
    }

    public void mover() {
        y += 1; // Movimiento hacia abajo
        x += direccion; // Aplica la dirección al movimiento en x
    }

    public void actualizarDireccion(int anchoPantalla) {
        // Cambiar dirección al tocar los bordes
        if (x <= 0) {
            x = 0; // Asegura que no se salga del borde izquierdo
            direccion = 1; // Cambia la dirección a la derecha
        } else if (x >= anchoPantalla - 30) {
            x = anchoPantalla - 30; // Asegura que no se salga del borde derecho
            direccion = -1; // Cambia la dirección a la izquierda
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void dibujar(Graphics g) {
        g.setColor(Color.RED);
        g.fillRect(x, y, 30, 30); // Dibuja el barco enemigo
    }
}


    class Disparo {
        private int x, y;

        public Disparo(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public void mover() {
            y -= 3;  // Velocidad de movimiento hacia arriba
        }

        public void dibujar(Graphics g) {
            g.setColor(Color.YELLOW);
            g.fillRect(x, y, 5, 10); // Dibuja el disparo
        }

        public boolean intersecta(BarcoEnemigo enemigo) { // Cambiado a BarcoEnemigo
            Rectangle r1 = new Rectangle(x, y, 5, 10);
            Rectangle r2 = new Rectangle(enemigo.getX(), enemigo.getY(), 30, 30); // Se corrigió enemigo
            return r1.intersects(r2);
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("PirateWars");
        PirateWars juego = new PirateWars();
        frame.add(juego);
        frame.setSize(800, 700);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //Inicia como ventana maximizada
        //frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setVisible(true);

        Timer timer = new Timer(10, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                juego.repaint();
            }
        });
        timer.start();
    }
}

