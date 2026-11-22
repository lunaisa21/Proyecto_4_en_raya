package ventanaInteraccion;

import datos.ColorFicha;
import datos.Ficha;
import datos.Jugador;
import datos.Marcador;
import datos.Tablero;
import operaciones.GestorPartida;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.GlyphVector;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.Random;

public class VentanaJuego extends JFrame {

    private static final double ESC = 1.8;
    private static final int BASE_W = 525;
    private static final int BASE_H = 400;

    private static int e(double v) {
        return (int) Math.round(v * ESC);
    }

    // ------------------------------------------------------------------
    //  Colores
    // ------------------------------------------------------------------
    private static final Color VERDE_TITULO = new Color(0x8BD36B);
    private static final Color VERDE_LINEA = new Color(0x6AA84F);
    private static final Color PANEL_FONDO = new Color(0x1F241E);
    private static final Color TABLERO_FONDO = new Color(0x453A7A);
    private static final Color TABLERO_BORDE = new Color(0x8C82C8);
    private static final Color CELDA_VACIA = new Color(0xF7F5EC);
    private static final Color FICHA_ROJA = new Color(0xFF4733);
    private static final Color FICHA_AZUL = new Color(0x22C3E6);
    private static final Color DORADO = new Color(0xF2B632);

    private static final String PANTALLA_MENU = "menu";
    private static final String PANTALLA_JUEGO = "juego";
    private static final String PANTALLA_MARCADOR = "marcador";

    private enum Estado {EN_CURSO, VICTORIA, DERROTA, EMPATE}

    // ------------------------------------------------------------------
    //  Estado
    // ------------------------------------------------------------------
    private final Jugador jugador;
    private final Jugador ia;
    private final Marcador marcador;
    private GestorPartida gestor;
    private Timer temporizadorIA;
    private int ultimoResultado = 0; // 0 victoria, 1 derrota, 2 empate (resalta la tarjeta del marcador)

    private final CardLayout cartas = new CardLayout();
    private final JPanel contenedor = new JPanel(cartas);
    private final PantallaJuego pantallaJuego;
    private final PantallaMarcador pantallaMarcador;

    private static BufferedImage fondoCielo;
    private static BufferedImage fondoMenu;

    // ------------------------------------------------------------------
    //  Construccion
    // ------------------------------------------------------------------
    public VentanaJuego() {
        this(crearJugador("Steve", false, 1), crearJugador("Enderman", true, 0), new Marcador());
    }

    public VentanaJuego(Jugador jugador, Jugador ia, Marcador marcador) {
        super("4 en Raya");
        this.jugador = jugador;
        this.ia = ia;
        this.marcador = marcador;

        pantallaJuego = new PantallaJuego();
        pantallaMarcador = new PantallaMarcador();

        contenedor.add(new PantallaMenu(), PANTALLA_MENU);
        contenedor.add(pantallaJuego, PANTALLA_JUEGO);
        contenedor.add(pantallaMarcador, PANTALLA_MARCADOR);
        contenedor.setPreferredSize(new Dimension(e(BASE_W), e(BASE_H)));

        setContentPane(contenedor);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        pack();
        setLocationRelativeTo(null);
        mostrar(PANTALLA_MENU);
    }

    private static Jugador crearJugador(String nombre, boolean esIa, int id) {
        Jugador j = new Jugador();
        j.setNombre(nombre);
        j.setEsIa(esIa);
        j.setIdJugador(id);
        return j;
    }

    // ------------------------------------------------------------------
    //  Flujo del juego
    // ------------------------------------------------------------------
    void mostrar(String pantalla) {
        cartas.show(contenedor, pantalla);
    }

    void irAlMenu() {
        detenerTemporizador();
        mostrar(PANTALLA_MENU);
    }

    void mostrarMarcador() {
        detenerTemporizador();
        pantallaMarcador.actualizar();
        mostrar(PANTALLA_MARCADOR);
    }

    /** Inicia una partida nueva (sirve para Jugar, Reintentar y Continuar). */
    void nuevaPartida() {
        detenerTemporizador();
        gestor = new GestorPartida(jugador, ia, marcador);
        gestor.iniciarPartida();
        pantallaJuego.refrescar();
        mostrar(PANTALLA_JUEGO);
        programarTurnoIA();
    }

    /** El usuario hace clic en una columna del tablero. */
    void onColumna(int columna) {
        if (gestor == null || gestor.getPartida().isTerminada()) {
            return;
        }
        if (gestor.getPartida().getTurnoActual() != jugador) {
            return; // turno de la IA
        }
        if (gestor.colocarFicha(columna)) {
            despuesDeJugada();
        }
    }

    private void despuesDeJugada() {
        boolean terminada = gestor.getPartida().isTerminada();
        if (terminada) {
            Jugador g = gestor.getGanador();
            ultimoResultado = (g == jugador) ? 0 : (g == ia) ? 1 : 2;
        }
        pantallaJuego.refrescar();
        if (!terminada) {
            programarTurnoIA();
        }
    }

    private void programarTurnoIA() {
        if (gestor == null || gestor.getPartida().isTerminada()) {
            return;
        }
        if (gestor.getPartida().getTurnoActual() != ia) {
            return;
        }
        detenerTemporizador();
        temporizadorIA = new Timer(650, ev -> {
            gestor.ejecutarTurnoIA();
            despuesDeJugada();
        });
        temporizadorIA.setRepeats(false);
        temporizadorIA.start();
    }

    private void detenerTemporizador() {
        if (temporizadorIA != null) {
            temporizadorIA.stop();
            temporizadorIA = null;
        }
    }

    void salirDelJuego() {
        Object[] opciones = {"Sí", "No"};
        int r = JOptionPane.showOptionDialog(this, "¿Seguro que quieres salir del juego?", "Salir",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, opciones, opciones[1]);
        if (r == 0) {
            detenerTemporizador();
            dispose();
            System.exit(0);
        }
    }

    private Estado estadoActual() {
        if (gestor == null || !gestor.getPartida().isTerminada()) {
            return Estado.EN_CURSO;
        }
        Jugador g = gestor.getGanador();
        if (g == jugador) {
            return Estado.VICTORIA;
        }
        if (g == ia) {
            return Estado.DERROTA;
        }
        return Estado.EMPATE;
    }

    private static Color colorDe(ColorFicha c) {
        return c == ColorFicha.AZUL ? FICHA_AZUL : FICHA_ROJA;
    }

    // ==================================================================
    //  PANTALLA: MENU / INICIO
    // ==================================================================
    private class PantallaMenu extends JPanel {
        PantallaMenu() {
            setLayout(null);
            if (fondoMenu == null) {
                fondoMenu = crearFondoMenu();
            }

            BotonMC jugar = new BotonMC("Jugar", true);
            jugar.setBounds(e(232), e(223), e(102), e(33));
            jugar.addActionListener(ev -> nuevaPartida());
            add(jugar);

            BotonMC marcadorBtn = new BotonMC("Marcador", true);
            marcadorBtn.setBounds(e(232), e(266), e(102), e(33));
            marcadorBtn.addActionListener(ev -> mostrarMarcador());
            add(marcadorBtn);
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            g.drawImage(fondoMenu, 0, 0, null);
            dibujarTitulo(g, "4 EN RAYA", e(288), e(110), e(350));
            g.dispose();
        }
    }

    /** Titulo grande con efecto de piedra y sombra 3D. */
    private static void dibujarTitulo(Graphics2D g, String texto, int centroX, int baseY, int anchoDeseado) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        Font f = new Font(Font.MONOSPACED, Font.BOLD, 100);
        int ancho = g.getFontMetrics(f).stringWidth(texto);
        f = f.deriveFont(100f * anchoDeseado / ancho);
        g.setFont(f);
        int x = centroX - g.getFontMetrics().stringWidth(texto) / 2;

        int profundidad = e(5);
        g.setColor(new Color(0x2B2B2B));
        for (int i = profundidad; i >= 1; i--) {
            g.drawString(texto, x + i, baseY + i);
        }
        GlyphVector gv = f.createGlyphVector(g.getFontRenderContext(), texto);
        Shape contorno = gv.getOutline(x, baseY);
        g.setColor(new Color(0xC6C6C6));
        g.fill(contorno);
        g.setColor(new Color(0x5A5A5A));
        g.setStroke(new BasicStroke(e(1.4)));
        g.draw(contorno);
    }

    // ==================================================================
    //  PANTALLA: PARTIDA (en curso / finalizada)
    // ==================================================================
    private class PantallaJuego extends JPanel {
        private final TarjetaJugador tarjetaIa = new TarjetaJugador(ia, new Color(0x9B59D0), true);
        private final TarjetaJugador tarjetaUsuario = new TarjetaJugador(jugador, new Color(0x7ACB5A), false);
        private final TableroView tablero = new TableroView();
        private final PanelEstado panelEstado = new PanelEstado();

        PantallaJuego() {
            setLayout(null);
            if (fondoCielo == null) {
                fondoCielo = crearFondoCielo();
            }
            tarjetaIa.setBounds(e(23), e(26), e(78), e(96));
            tarjetaUsuario.setBounds(e(215), e(26), e(78), e(96));
            tablero.setBounds(e(15), e(135), e(283), e(248));
            panelEstado.setBounds(e(327), e(83), e(163), e(233));
            add(tarjetaIa);
            add(tarjetaUsuario);
            add(tablero);
            add(panelEstado);
        }

        void refrescar() {
            panelEstado.actualizarEstado();
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            g.drawImage(fondoCielo, 0, 0, null);
            // Caja "VS"
            int x = e(143), y = e(52), w = e(30), h = e(18);
            g.setColor(new Color(0x2A2A1E));
            g.fillRect(x, y, w, h);
            g.setColor(DORADO);
            g.setStroke(new BasicStroke(e(1.2)));
            g.drawRect(x, y, w, h);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g.setFont(new Font(Font.MONOSPACED, Font.BOLD, e(8)));
            FontMetrics fm = g.getFontMetrics();
            g.setColor(new Color(0xFFD94A));
            g.drawString("VS", x + (w - fm.stringWidth("VS")) / 2, y + (h + fm.getAscent()) / 2 - e(1));
            g.dispose();
        }
    }

    // ---------------- Tarjeta del jugador (avatar + nombre) ----------------
    private static class TarjetaJugador extends JComponent {
        private final Jugador j;
        private final Color borde;
        private final boolean esIa;

        private static final String[] STEVE = {
                "hhhhhhhh",
                "hhhhhhhh",
                "hssssssh",
                "ssssssss",
                "ebssssbe",
                "sssnnsss",
                "smmmmmms",
                "smssssms"};
        private static final String[] ENDERMAN = {
                "gggggggg",
                "gkgggkgg",
                "gggkgggg",
                "gggggggk",
                "gpwggwpg",
                "gppggppg",
                "gggkgggg",
                "kgggggkg"};

        TarjetaJugador(Jugador j, Color borde, boolean esIa) {
            this.j = j;
            this.borde = borde;
            this.esIa = esIa;
        }

        private static Color pixel(char c) {
            switch (c) {
                case 'h': return new Color(0x3B2A1A);
                case 's': return new Color(0xB8835A);
                case 'e': return new Color(0xF2F2F2);
                case 'b': return new Color(0x2B3A9A);
                case 'n': return new Color(0x8A5A3A);
                case 'm': return new Color(0x5A3A22);
                case 'g': return new Color(0x0E0E12);
                case 'k': return new Color(0x1B1B24);
                case 'p': return new Color(0xC070FF);
                case 'w': return new Color(0xF4DCFF);
                default: return Color.BLACK;
            }
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            int w = getWidth(), h = getHeight();
            int pad = e(3);
            int av = w - 2 * pad;

            g.setColor(new Color(0x181818));
            g.fillRect(0, 0, w, h);
            g.setColor(borde);
            g.fillRect(0, 0, w, av + 2 * pad);

            String[] filas = esIa ? ENDERMAN : STEVE;
            for (int r = 0; r < 8; r++) {
                for (int c = 0; c < 8; c++) {
                    g.setColor(pixel(filas[r].charAt(c)));
                    int x0 = pad + c * av / 8, x1 = pad + (c + 1) * av / 8;
                    int y0 = pad + r * av / 8, y1 = pad + (r + 1) * av / 8;
                    g.fillRect(x0, y0, x1 - x0, y1 - y0);
                }
            }

            // Etiqueta
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            int yEtiqueta = av + 2 * pad;
            g.setFont(new Font(Font.MONOSPACED, Font.BOLD, e(4.8)));
            String tag = esIa ? "IA" : "USUARIO";
            g.setColor(esIa ? new Color(0xB58CF0) : new Color(0x8BD36B));
            FontMetrics fm = g.getFontMetrics();
            int dot = e(5);
            int anchoTag = fm.stringWidth(tag) + dot + e(3);
            int xTag = (w - anchoTag) / 2;
            int yTag = yEtiqueta + e(8);
            g.drawString(tag, xTag, yTag);
            ColorFicha cf = j.getColor() != null ? j.getColor() : (esIa ? ColorFicha.AZUL : ColorFicha.ROJO);
            g.setColor(colorDe(cf));
            g.fill(new Ellipse2D.Double(xTag + fm.stringWidth(tag) + e(3), yTag - dot + e(0.5), dot, dot));

            g.setFont(new Font(Font.MONOSPACED, Font.BOLD, e(7)));
            fm = g.getFontMetrics();
            String nombre = j.getNombre() == null ? "" : j.getNombre();
            g.setColor(Color.WHITE);
            g.drawString(nombre, (w - fm.stringWidth(nombre)) / 2, h - e(4));
            g.dispose();
        }
    }

    // ---------------- Tablero 6x7 ----------------
    private class TableroView extends JComponent {
        private int columnaHover = -1;

        TableroView() {
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseMotionListener(new MouseAdapter() {
                @Override
                public void mouseMoved(MouseEvent ev) {
                    int c = columnaDe(ev.getX());
                    if (c != columnaHover) {
                        columnaHover = c;
                        repaint();
                    }
                }
            });
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseExited(MouseEvent ev) {
                    columnaHover = -1;
                    repaint();
                }

                @Override
                public void mouseReleased(MouseEvent ev) {
                    int c = columnaDe(ev.getX());
                    if (c >= 0) {
                        onColumna(c);
                    }
                }
            });
        }

        private int columnaDe(int x) {
            int w = getWidth();
            int celda = e(34);
            int margen = (w - 7 * celda) / 2;
            if (x < margen || x >= margen + 7 * celda) {
                return -1;
            }
            return (x - margen) / celda;
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();
            g.setColor(TABLERO_FONDO);
            g.fill(new RoundRectangle2D.Double(0, 0, w, h, e(14), e(14)));
            g.setColor(TABLERO_BORDE);
            g.setStroke(new BasicStroke(e(1.2)));
            g.draw(new RoundRectangle2D.Double(e(0.6), e(0.6), w - e(1.2), h - e(1.2), e(14), e(14)));

            int celdaW = e(34), celdaH = e(33), diam = e(28);
            int mx = (w - 7 * celdaW) / 2, my = (h - 6 * celdaH) / 2;

            Ficha[][] casillas = null;
            if (gestor != null) {
                Tablero t = gestor.getPartida().getTablero();
                casillas = t.getCasillas();
            }

            for (int f = 0; f < 6; f++) {
                for (int c = 0; c < 7; c++) {
                    double cx = mx + c * celdaW + (celdaW - diam) / 2.0;
                    double cy = my + f * celdaH + (celdaH - diam) / 2.0;
                    Ficha ficha = casillas == null ? null : casillas[f][c];
                    if (ficha == null) {
                        g.setColor(CELDA_VACIA);
                        g.fill(new Ellipse2D.Double(cx, cy, diam, diam));
                    } else {
                        g.setColor(colorDe(ficha.getColor()));
                        g.fill(new Ellipse2D.Double(cx, cy, diam, diam));
                        g.setColor(Color.WHITE);
                        g.setStroke(new BasicStroke(e(1.3)));
                        g.draw(new Ellipse2D.Double(cx + e(0.7), cy + e(0.7), diam - e(1.4), diam - e(1.4)));
                    }
                }
            }

            // Vista previa de la ficha en la columna bajo el cursor (solo en el turno del usuario)
            if (casillas != null && columnaHover >= 0 && estadoActual() == Estado.EN_CURSO
                    && gestor.getPartida().getTurnoActual() == jugador) {
                for (int f = 5; f >= 0; f--) {
                    if (casillas[f][columnaHover] == null) {
                        double cx = mx + columnaHover * celdaW + (celdaW - diam) / 2.0;
                        double cy = my + f * celdaH + (celdaH - diam) / 2.0;
                        Color c = colorDe(jugador.getColor());
                        g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 110));
                        g.fill(new Ellipse2D.Double(cx, cy, diam, diam));
                        break;
                    }
                }
            }

            // Linea ganadora
            if (casillas != null && gestor.getPartida().isTerminada() && gestor.getGanador() != null) {
                int[] l = lineaGanadora(casillas);
                if (l != null) {
                    double x1 = mx + l[1] * celdaW + celdaW / 2.0, y1 = my + l[0] * celdaH + celdaH / 2.0;
                    double x2 = mx + l[3] * celdaW + celdaW / 2.0, y2 = my + l[2] * celdaH + celdaH / 2.0;
                    g.setColor(DORADO);
                    g.setStroke(new BasicStroke(e(2.6), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g.draw(new java.awt.geom.Line2D.Double(x1, y1, x2, y2));
                }
            }
            g.dispose();
        }
    }

    /** Devuelve {filaIni, colIni, filaFin, colFin} de la primera linea de 4, o null. */
    private static int[] lineaGanadora(Ficha[][] c) {
        int[][] dirs = {{0, 1}, {1, 0}, {1, 1}, {-1, 1}};
        for (int f = 0; f < 6; f++) {
            for (int col = 0; col < 7; col++) {
                Ficha a = c[f][col];
                if (a == null) {
                    continue;
                }
                for (int[] d : dirs) {
                    int ff = f + 3 * d[0], cc = col + 3 * d[1];
                    if (ff < 0 || ff >= 6 || cc < 0 || cc >= 7) {
                        continue;
                    }
                    boolean ok = true;
                    for (int k = 1; k <= 3 && ok; k++) {
                        Ficha x = c[f + k * d[0]][col + k * d[1]];
                        ok = x != null && x.getColor() == a.getColor();
                    }
                    if (ok) {
                        return new int[]{f, col, ff, cc};
                    }
                }
            }
        }
        return null;
    }

    // ---------------- Panel lateral (En curso / Victoria / ...) ----------------
    private class PanelEstado extends JComponent {
        private final BotonMC reintentar = new BotonMC("Reintentar", false);
        private final BotonMC menu = new BotonMC("Menú", false);
        private final BotonMC salir = new BotonMC("Salir", false);

        PanelEstado() {
            setLayout(null);
            reintentar.setBounds(e(22), e(91), e(119), e(30));
            menu.setBounds(e(22), e(138), e(119), e(30));
            salir.setBounds(e(22), e(185), e(119), e(30));
            reintentar.addActionListener(ev -> nuevaPartida());
            menu.addActionListener(ev -> irAlMenu());
            salir.addActionListener(ev -> salirDelJuego());
            add(reintentar);
            add(menu);
            add(salir);
        }

        void actualizarEstado() {
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();
            dibujarPanelOscuro(g, 0, 0, w, h);

            Estado estado = estadoActual();
            String titulo, mensaje;
            Color colorTitulo;
            switch (estado) {
                case VICTORIA:
                    titulo = "Victoria";
                    mensaje = "!!Felicidades!! Has\nganado la partida";
                    colorTitulo = new Color(0xA9A0F5);
                    break;
                case DERROTA:
                    titulo = "Derrota";
                    mensaje = "!!Ay no!! La IA ganó\nesta vez";
                    colorTitulo = new Color(0xFF7B6B);
                    break;
                case EMPATE:
                    titulo = "Empate";
                    mensaje = "!!Tablero lleno!! Nadie\nganó la partida";
                    colorTitulo = new Color(0xF2D45A);
                    break;
                default:
                    titulo = "En curso...";
                    boolean turnoIa = gestor != null && gestor.getPartida().getTurnoActual() == ia;
                    mensaje = turnoIa ? "Turno de " + ia.getNombre() + "...\n!Piensa tu jugada!"
                            : "!!Sigue jugando !!\n!Atrévete a ganar!";
                    colorTitulo = VERDE_TITULO;
            }

            g.setFont(new Font(Font.MONOSPACED, Font.BOLD, e(10.5)));
            g.setColor(colorTitulo);
            g.drawString(titulo, e(13), e(31));

            g.setFont(new Font(Font.MONOSPACED, Font.BOLD, e(6.2)));
            g.setColor(new Color(0xE8E8E8));
            int y = e(47);
            for (String linea : mensaje.split("\n")) {
                g.drawString(linea, e(13), y);
                y += e(10);
            }

            g.setColor(VERDE_LINEA);
            g.fillRect(e(13), e(79), w - e(26), Math.max(2, e(1.3)));

            if (estado == Estado.VICTORIA) {
                dibujarTrofeo(g, w - e(40), e(13));
            } else if (estado == Estado.DERROTA) {
                g.setColor(new Color(0xFF5A4A));
                g.setStroke(new BasicStroke(e(2.4), BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER));
                int ox = w - e(38), oy = e(14), s = e(16);
                g.drawLine(ox, oy, ox + s, oy + s);
                g.drawLine(ox + s, oy, ox, oy + s);
            } else if (estado == Estado.EMPATE) {
                g.setColor(new Color(0xF2D45A));
                int ox = w - e(38), oy = e(17);
                g.fillRect(ox, oy, e(16), e(4));
                g.fillRect(ox, oy + e(9), e(16), e(4));
            }
            g.dispose();
        }
    }

    private static void dibujarTrofeo(Graphics2D g, int ox, int oy) {
        double u = ESC * 1.15;
        g.setColor(DORADO);
        g.fillRect(ox + (int) (3 * u), oy, (int) (12 * u), (int) (10 * u));          // copa
        g.fillRect(ox, oy + (int) (2 * u), (int) (3 * u), (int) (5 * u));            // asa izq
        g.fillRect(ox + (int) (15 * u), oy + (int) (2 * u), (int) (3 * u), (int) (5 * u)); // asa der
        g.fillRect(ox + (int) (7 * u), oy + (int) (10 * u), (int) (4 * u), (int) (4 * u)); // tallo
        g.fillRect(ox + (int) (4 * u), oy + (int) (14 * u), (int) (10 * u), (int) (3 * u)); // base
        g.setColor(new Color(0xFFE48A));
        g.fillRect(ox + (int) (5 * u), oy + (int) (1 * u), (int) (2 * u), (int) (7 * u)); // brillo
    }

    // ==================================================================
    //  PANTALLA: MARCADOR
    // ==================================================================
    private class PantallaMarcador extends JPanel {
        // Posicion del panel (unidades base)
        private final int px = 45, py = 35, pw = 435, ph = 328;

        private final CajaMarcador victorias = new CajaMarcador("Victorias");
        private final CajaMarcador derrotas = new CajaMarcador("Derrotas");
        private final CajaMarcador empates = new CajaMarcador("Empates");

        PantallaMarcador() {
            setLayout(null);
            if (fondoCielo == null) {
                fondoCielo = crearFondoCielo();
            }
            int ancho = 127, hueco = 10, x0 = px + 16;
            victorias.setBounds(e(x0), e(py + 78), e(ancho), e(48));
            derrotas.setBounds(e(x0 + ancho + hueco), e(py + 78), e(ancho), e(48));
            empates.setBounds(e(x0 + 2 * (ancho + hueco)), e(py + 78), e(ancho), e(48));
            add(victorias);
            add(derrotas);
            add(empates);

            BotonMC continuar = new BotonMC("Continuar", true);
            BotonMC menu = new BotonMC("Menú", false);
            BotonMC salir = new BotonMC("Salir del juego", false);
            continuar.setBounds(e(x0), e(py + 262), e(ancho), e(34));
            menu.setBounds(e(x0 + ancho + hueco), e(py + 262), e(ancho), e(34));
            salir.setBounds(e(x0 + 2 * (ancho + hueco)), e(py + 262), e(ancho), e(34));
            continuar.addActionListener(ev -> nuevaPartida());
            menu.addActionListener(ev -> irAlMenu());
            salir.addActionListener(ev -> salirDelJuego());
            add(continuar);
            add(menu);
            add(salir);
        }

        void actualizar() {
            victorias.setValor(marcador.getVictorias());
            derrotas.setValor(marcador.getDerrotas());
            empates.setValor(marcador.getEmpates());
            victorias.setResaltada(ultimoResultado == 0);
            derrotas.setResaltada(ultimoResultado == 1);
            empates.setResaltada(ultimoResultado == 2);
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g.drawImage(fondoCielo, 0, 0, null);
            dibujarPanelOscuro(g, e(px), e(py), e(pw), e(ph));

            g.setFont(new Font(Font.MONOSPACED, Font.BOLD, e(14)));
            g.setColor(VERDE_TITULO);
            g.drawString("MARCADOR", e(px + 16), e(py + 34));

            g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, e(5.8)));
            g.setColor(new Color(0xD5DDD5));
            g.drawString("Tus resultados contra la IA", e(px + 16), e(py + 48));

            g.setColor(VERDE_LINEA);
            g.fillRect(e(px + 16), e(py + 56), e(pw - 32), Math.max(2, e(1.3)));

            int total = marcador.getVictorias() + marcador.getDerrotas() + marcador.getEmpates();
            int pct = total == 0 ? 0 : Math.round(100f * marcador.getVictorias() / total);
            g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, e(6.3)));
            g.setColor(new Color(0xE8E8E8));
            g.drawString("Partidas jugadas: " + total + "     Victorias: " + pct + "%", e(px + 16), e(py + 152));

            g.setColor(new Color(0x3A4239));
            g.fillRect(e(px + 16), e(py + 185), e(pw - 32), Math.max(1, e(0.6)));

            g.setFont(new Font(Font.MONOSPACED, Font.BOLD, e(7.5)));
            g.setColor(Color.WHITE);
            g.drawString("¿Quieres jugar otra partida?", e(px + 16), e(py + 228));
            g.dispose();
        }
    }

    /** Tarjeta oscura con etiqueta a la izquierda y valor a la derecha (estilo de la referencia). */
    private static class CajaMarcador extends JComponent {
        private final String etiqueta;
        private int valor = 0;
        private boolean resaltada = false;

        CajaMarcador(String etiqueta) {
            this.etiqueta = etiqueta;
        }

        void setValor(int v) {
            this.valor = v;
            repaint();
        }

        void setResaltada(boolean r) {
            this.resaltada = r;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();
            double r = e(7);
            g.setColor(new Color(0x212328));
            g.fill(new RoundRectangle2D.Double(0, 0, w - 1, h - 1, r, r));
            g.setColor(new Color(0x3B3E45));
            g.setStroke(new BasicStroke(1f));
            g.draw(new RoundRectangle2D.Double(0.5, 0.5, w - 2, h - 2, r, r));

            if (resaltada) { // subrayado turquesa
                Shape clip = g.getClip();
                g.setClip(new RoundRectangle2D.Double(0, 0, w - 1, h - 1, r, r));
                g.setColor(new Color(0x19B394));
                g.fillRect(0, h - e(2.4), w, e(2.4));
                g.setClip(clip);
            }

            g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, e(7.5)));
            FontMetrics fm = g.getFontMetrics();
            int y = (h + fm.getAscent()) / 2 - e(2);
            g.setColor(new Color(0xF1F1F1));
            g.drawString(etiqueta, e(10), y);

            g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, e(8.5)));
            fm = g.getFontMetrics();
            String v = String.valueOf(valor);
            g.setColor(valor == 0 ? new Color(0x8C9097) : new Color(0xD7DAE0));
            g.drawString(v, w - e(12) - fm.stringWidth(v), y);
            g.dispose();
        }
    }

    // ==================================================================
    //  Componentes y utilidades graficas estilo Minecraft
    // ==================================================================
    private static void dibujarPanelOscuro(Graphics2D g, int x, int y, int w, int h) {
        int b = Math.max(2, e(1.5));
        g.setColor(new Color(0x0C0E0C));
        g.fillRect(x, y, w, h);
        g.setColor(new Color(0x7A7F7A));
        g.fillRect(x + b, y + b, w - 2 * b, h - 2 * b);
        g.setColor(new Color(0x3C403C));
        g.fillRect(x + b, y + h - 2 * b, w - 2 * b, b);
        g.fillRect(x + w - 2 * b, y + b, b, h - 2 * b);
        g.setColor(PANEL_FONDO);
        g.fillRect(x + 2 * b, y + 2 * b, w - 4 * b, h - 4 * b);
    }

    /** Boton con aspecto de Minecraft (verde para el menu, gris para el resto). */
    private static class BotonMC extends JButton {
        private final boolean verde;

        BotonMC(String texto, boolean verde) {
            super(texto);
            this.verde = verde;
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setRolloverEnabled(true);
            setFont(new Font(Font.MONOSPACED, Font.BOLD, e(8.5)));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();
            int b = Math.max(2, e(1.2));

            Color base = verde ? new Color(0x3F8A2E) : new Color(0x6E6E6E);
            Color claro = verde ? new Color(0x6FBF4F) : new Color(0xA0A0A0);
            Color oscuro = verde ? new Color(0x265C19) : new Color(0x444444);
            if (getModel().isPressed()) {
                Color t = claro;
                claro = oscuro;
                oscuro = t;
            } else if (getModel().isRollover()) {
                base = base.brighter();
            }

            g.setColor(new Color(0x0E0E0E));
            g.fillRect(0, 0, w, h);
            g.setColor(base);
            g.fillRect(b, b, w - 2 * b, h - 2 * b);
            g.setColor(claro);
            g.fillRect(b, b, w - 2 * b, b);
            g.fillRect(b, b, b, h - 2 * b);
            g.setColor(oscuro);
            g.fillRect(b, h - 2 * b, w - 2 * b, b);
            g.fillRect(w - 2 * b, b, b, h - 2 * b);

            g.setFont(getFont());
            FontMetrics fm = g.getFontMetrics();
            int x = (w - fm.stringWidth(getText())) / 2;
            int y = (h + fm.getAscent()) / 2 - fm.getDescent() / 2;
            g.setColor(new Color(0, 0, 0, 150));
            g.drawString(getText(), x + e(0.8), y + e(0.8));
            g.setColor(Color.WHITE);
            g.drawString(getText(), x, y);
            g.dispose();
        }
    }

    // ==================================================================
    //  Fondos (dibujados por codigo)
    // ==================================================================
    private static void bloques(Graphics2D g, int x0, int y0, int x1, int y1, long semilla, boolean piedra) {
        Random rnd = new Random(semilla);
        int b = 11;
        int[] pasto = {0x5FB03A, 0x56A431, 0x69B944};
        int[] tierra = {0x8A5A30, 0x7C5029, 0x976638, 0x6E4724};
        int[] roca = {0x8E8E8E, 0x7D7D7D, 0x9D9D9D};
        for (int y = y0; y < y1; y += b) {
            for (int x = x0; x < x1; x += b) {
                int[] paleta = piedra ? roca : (y == y0 ? pasto : tierra);
                g.setColor(new Color(paleta[rnd.nextInt(paleta.length)]));
                g.fillRect(x, y, Math.min(b, x1 - x), Math.min(b, y1 - y));
                if (!piedra && y != y0) {
                    g.setColor(new Color(0, 0, 0, 35));
                    g.fillRect(x + rnd.nextInt(7), y + rnd.nextInt(7), 3, 3);
                }
            }
        }
    }

    private static void nube(Graphics2D g, int x, int y, int w) {
        g.fillRect(x, y + 8, w, 10);
        g.fillRect(x + 10, y + 2, w - 28, 8);
        g.fillRect(x + w / 3, y - 4, w / 3, 8);
    }

    private static BufferedImage crearFondoCielo() {
        BufferedImage img = new BufferedImage(e(BASE_W), e(BASE_H), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.scale(ESC, ESC);
        g.setPaint(new GradientPaint(0, 0, new Color(0x79B6EE), 0, 330, new Color(0xCFE8FB)));
        g.fillRect(0, 0, BASE_W, BASE_H);

        g.setColor(new Color(255, 255, 255, 235));
        nube(g, 30, 40, 50);
        nube(g, 150, 22, 60);
        nube(g, 330, 45, 55);
        nube(g, 430, 28, 45);
        nube(g, 200, 100, 40);
        nube(g, 60, 125, 35);

        bloques(g, 0, 335, BASE_W, BASE_H, 1, false);
        bloques(g, 0, 60, 22, 335, 2, false);
        bloques(g, 503, 185, BASE_W, 335, 3, false);
        bloques(g, 154, 366, 187, BASE_H, 4, true);
        g.dispose();
        return img;
    }

    private static void disco(Graphics2D g, int x, int y, Color c) {
        g.setColor(new Color(0, 0, 0, 60));
        g.fillOval(x - 2, y + 2, 34, 15);
        g.setColor(c);
        g.fillOval(x, y, 32, 14);
        g.setColor(c.darker().darker());
        g.fillOval(x + 12, y + 4, 8, 5);
    }

    private static BufferedImage crearFondoMenu() {
        BufferedImage img = new BufferedImage(e(BASE_W), e(BASE_H), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.scale(ESC, ESC);

        int esquinaX = 225, esquinaY = 210, bordeY = 262;

        // Paredes
        g.setColor(new Color(0x1D2377));
        g.fillPolygon(new int[]{0, esquinaX, esquinaX, 0}, new int[]{0, 0, esquinaY, bordeY}, 4);
        g.setColor(new Color(0xDDD8CC));
        g.fillPolygon(new int[]{esquinaX, BASE_W, BASE_W, esquinaX}, new int[]{0, 0, bordeY, esquinaY}, 4);
        g.setColor(new Color(0, 0, 0, 40));
        g.fillRect(esquinaX - 1, 0, 3, esquinaY);

        // Suelo de madera
        Polygon suelo = new Polygon(new int[]{0, esquinaX, BASE_W, BASE_W, 0},
                new int[]{bordeY, esquinaY, bordeY, BASE_H, BASE_H}, 5);
        Shape clip = g.getClip();
        g.setClip(suelo);
        g.setColor(new Color(0xB27A3D));
        g.fill(suelo);
        double pendiente = (double) (bordeY - esquinaY) / (BASE_W - esquinaX);
        for (int off = 0, i = 0; off < 260; off += 14, i++) {
            int[] xs = {0, BASE_W, BASE_W, 0};
            int[] ys = {(int) (esquinaY + pendiente * (0 - esquinaX)) + off,
                    (int) (esquinaY + pendiente * (BASE_W - esquinaX)) + off,
                    (int) (esquinaY + pendiente * (BASE_W - esquinaX)) + off + 14,
                    (int) (esquinaY + pendiente * (0 - esquinaX)) + off + 14};
            g.setColor(i % 2 == 0 ? new Color(0xA86F34) : new Color(0xB9823F));
            g.fillPolygon(xs, ys, 4);
            g.setColor(new Color(0x8A5A2A));
            g.drawLine(xs[0], ys[0], xs[1], ys[1]);
        }
        g.setClip(clip);

        // Palanca en la pared azul
        g.setColor(new Color(0x7A7A7A));
        g.fillRect(21, 178, 16, 22);
        g.setColor(new Color(0x4A4A4A));
        g.fillRect(25, 183, 8, 8);

        // Escalera
        g.setColor(new Color(0x6B4423));
        g.fillRect(405, 0, 7, 312);
        g.fillRect(438, 0, 7, 312);
        g.setColor(new Color(0x8B5A2B));
        for (int y = 6; y < 312; y += 18) {
            g.fillRect(405, y, 40, 5);
        }

        // Cama
        g.setColor(new Color(0x7A4A1E));
        g.fillRect(125, 245, 112, 22);
        g.fillRect(125, 267, 10, 16);
        g.fillRect(227, 267, 10, 16);
        g.setColor(new Color(0xB02A2A));
        g.fillRect(125, 200, 105, 45);
        g.setColor(new Color(0xD04040));
        g.fillRect(125, 200, 105, 6);
        g.setColor(Color.WHITE);
        g.fillRect(205, 184, 50, 20);
        g.setColor(new Color(0xD8D8D8));
        g.fillRect(205, 198, 50, 6);

        // Barril con linterna
        g.setColor(new Color(0x7F5224));
        g.fillRect(262, 208, 80, 68);
        g.setColor(new Color(0x4A2E12));
        g.fillRect(262, 208, 80, 6);
        g.fillRect(262, 240, 80, 5);
        g.fillRect(262, 270, 80, 6);
        g.setColor(new Color(0x2D2D2D));
        g.fillRect(293, 178, 18, 28);
        g.setColor(new Color(0xE8A02A));
        g.fillRect(296, 184, 12, 16);
        g.setColor(new Color(0xFFD060));
        g.fillRect(299, 188, 6, 8);

        // Discos
        disco(g, 80, 322, new Color(0xD2791F));
        disco(g, 170, 380, new Color(0x2F6FD6));
        disco(g, 306, 368, new Color(0xD2791F));
        disco(g, 306, 304, new Color(0x2F6FD6));
        disco(g, 458, 384, new Color(0xD2791F));

        // Huevo de dragon
        g.setColor(new Color(0x0B0612));
        g.fillRoundRect(425, 308, 70, 64, 40, 50);
        g.setColor(new Color(0x5A2A8A));
        Random rnd = new Random(11);
        for (int i = 0; i < 14; i++) {
            g.fillRect(432 + rnd.nextInt(54), 318 + rnd.nextInt(44), 4, 4);
        }
        g.dispose();
        return img;
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VentanaJuego().setVisible(true));
    }
}