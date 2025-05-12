package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List; // Necesario para ArbolNumero
import java.util.NoSuchElementException; // Para manejar errores de árbol vacío

public class VentanaArbol extends JFrame {

    // Enum para definir el modo de operación
    public enum Modo {
        NUMEROS, EXPRESIONES
    }

    private Modo modoActual; // Almacena el modo seleccionado
    private Object arbol; // Almacena ArbolNumero o ArbolExpresion
    private ArbolPanel arbolPanel;
    private JTextField valorEntradaTextField; // Renombrado para ser más genérico
    private JTextArea resultadoOperacionesTextArea; // Para mostrar resultados de operaciones y recorridos
    private JComboBox<String> tipoRecorridoComboBox;
    // Para Modo NUMEROS, nodoPadreSeleccionado no se usa para inserción.
    // Se mantiene por si se clickea un nodo y se quiere info de ESE nodo (futuro)
    private Object nodoSeleccionadoPorClic = null; 
    private JLabel etiquetaNodoSeleccionadoPorClic; // Nueva etiqueta para info del nodo clickeado

    // Componentes que se mostrarán/ocultarán según el modo
    private JLabel etiquetaPadreSeleccionado; // Anteriormente padreSeleccionadoLabel
    private JRadioButton izquierdoRadioButton;
    private JRadioButton derechoRadioButton;
    private JLabel etiquetaPosicion;
    private JButton insertarButton; // Renombrado desde btnAgregarDato a "Insertar Hijo" o "Insertar Raíz/Hijo"
    private JButton btnEliminarDato;
    private JTextField valorEliminarTextField;

    private JLabel leyendaLabel;
    private JTextArea instruccionesTextArea;

    // Panel para operaciones adicionales en modo NUMEROS
    private JPanel panelOperacionesNumeros;
    private JTextArea outputOperacionesNumerosTextArea;
    private JLabel labelResultadosOpNumeros; // Nueva variable

    // Panel para operaciones adicionales en modo EXPRESIONES
    private JPanel panelOperacionesExpresiones;
    private JTextArea outputOperacionesExpresionesTextArea;
    private JLabel labelResultadosOpExpresiones; // Nueva variable

    // Constructor ahora acepta el modo
    public VentanaArbol(Modo modo) {
        this.modoActual = modo;

        // Instanciar el árbol correcto según el modo
        if (modoActual == Modo.NUMEROS) {
            arbol = new ArbolNumero();
            setTitle("Árbol Binario de Números"); // Título General
        } else {
            arbol = new ArbolExpresion();
            setTitle("Constructor de Árbol de Expresiones"); // Título General
        }

        // --- Configuración inicial de la ventana --- 
        setSize(1100, 700); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); 

        // --- Crear componentes de la GUI --- 
        arbolPanel = new ArbolPanel(); // El panel necesita saber el modo
        arbolPanel.setBackground(Color.WHITE);
        add(arbolPanel, BorderLayout.CENTER);

        // --- Panel Norte (Controles de Inserción/Eliminación) ---
        JPanel panelControlesNorte = new JPanel(new FlowLayout(FlowLayout.LEFT));
        valorEntradaTextField = new JTextField(8);
        insertarButton = new JButton("Insertar Raíz/Hijo"); // Nombre cambiado
        etiquetaPosicion = new JLabel("Posición Hijo:"); // Etiqueta más clara
        izquierdoRadioButton = new JRadioButton("Izquierdo", true); 
        derechoRadioButton = new JRadioButton("Derecho");
        ButtonGroup grupoPosicion = new ButtonGroup();
        grupoPosicion.add(izquierdoRadioButton);
        grupoPosicion.add(derechoRadioButton);
        etiquetaPadreSeleccionado = new JLabel("Padre para inserción: Ninguno"); // Etiqueta más clara
        
        valorEliminarTextField = new JTextField(5);
        btnEliminarDato = new JButton("Eliminar Dato");

        panelControlesNorte.add(new JLabel("Valor:")); 
        panelControlesNorte.add(valorEntradaTextField);
        panelControlesNorte.add(insertarButton);
        panelControlesNorte.add(etiquetaPosicion); // Se hará visible/invisible según contexto
        panelControlesNorte.add(izquierdoRadioButton); // Se hará visible/invisible
        panelControlesNorte.add(derechoRadioButton); // Se hará visible/invisible
        panelControlesNorte.add(etiquetaPadreSeleccionado); 
        panelControlesNorte.add(new JSeparator(SwingConstants.VERTICAL), "gapleft 15");
        panelControlesNorte.add(new JLabel("Valor a Eliminar:"));
        panelControlesNorte.add(valorEliminarTextField);
        panelControlesNorte.add(btnEliminarDato);
        add(panelControlesNorte, BorderLayout.NORTH);

        // --- Panel Sur (Recorridos y Output Principal) ---
        JPanel panelControlesSur = new JPanel(new FlowLayout(FlowLayout.LEFT));
        tipoRecorridoComboBox = new JComboBox<>(new String[]{"Inorden", "Preorden", "Postorden", "Amplitud"});
        JButton mostrarRecorridoButton = new JButton("Mostrar Recorrido/Expresión");
        resultadoOperacionesTextArea = new JTextArea(4, 50); 
        resultadoOperacionesTextArea.setEditable(false);
        resultadoOperacionesTextArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12)); 
        JScrollPane scrollPaneResultado = new JScrollPane(resultadoOperacionesTextArea);

        panelControlesSur.add(new JLabel("Tipo:")); 
        panelControlesSur.add(tipoRecorridoComboBox);
        panelControlesSur.add(mostrarRecorridoButton);
        panelControlesSur.add(new JLabel("Resultado:"));
        panelControlesSur.add(scrollPaneResultado);
        add(panelControlesSur, BorderLayout.SOUTH);

        // --- Panel Lateral (Instrucciones, Leyenda y Operaciones) ---
        JPanel panelLateralCompleto = new JPanel();
        panelLateralCompleto.setLayout(new BoxLayout(panelLateralCompleto, BoxLayout.Y_AXIS));
        panelLateralCompleto.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        leyendaLabel = new JLabel();
        leyendaLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        instruccionesTextArea = new JTextArea();
        instruccionesTextArea.setEditable(false);
        instruccionesTextArea.setLineWrap(true); 
        instruccionesTextArea.setWrapStyleWord(true); 
        instruccionesTextArea.setBackground(this.getBackground()); 
        instruccionesTextArea.setFont(new Font("SansSerif", Font.PLAIN, 12));

        JScrollPane scrollPaneInstrucciones = new JScrollPane(instruccionesTextArea);
        scrollPaneInstrucciones.setBorder(BorderFactory.createTitledBorder("Guía Rápida"));
        scrollPaneInstrucciones.setAlignmentX(Component.LEFT_ALIGNMENT);

        etiquetaNodoSeleccionadoPorClic = new JLabel("Nodo clickeado: Ninguno");
        etiquetaNodoSeleccionadoPorClic.setAlignmentX(Component.LEFT_ALIGNMENT);

        panelLateralCompleto.add(leyendaLabel);
        panelLateralCompleto.add(Box.createRigidArea(new Dimension(0,5)));
        panelLateralCompleto.add(etiquetaNodoSeleccionadoPorClic);
        panelLateralCompleto.add(Box.createRigidArea(new Dimension(0,10)));
        panelLateralCompleto.add(scrollPaneInstrucciones);
        panelLateralCompleto.add(Box.createRigidArea(new Dimension(0,10)));

        // Panel específico para Operaciones de ArbolNumero
        panelOperacionesNumeros = new JPanel(new GridLayout(0, 2, 5, 5)); // Grid para botones
        panelOperacionesNumeros.setBorder(BorderFactory.createTitledBorder("Operaciones del Árbol (Números)"));
        outputOperacionesNumerosTextArea = new JTextArea(5,20);
        outputOperacionesNumerosTextArea.setEditable(false);
        outputOperacionesNumerosTextArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane scrollOutputOpsNum = new JScrollPane(outputOperacionesNumerosTextArea);
        
        // Añadir botones de operaciones al panelOperacionesNumeros
        crearBotonesOperacionesNumero(); 

        // Panel específico para Operaciones de ArbolExpresion
        panelOperacionesExpresiones = new JPanel(new GridLayout(0, 2, 5, 5));
        panelOperacionesExpresiones.setBorder(BorderFactory.createTitledBorder("Operaciones del Árbol (Expresiones)"));
        outputOperacionesExpresionesTextArea = new JTextArea(5,20);
        outputOperacionesExpresionesTextArea.setEditable(false);
        outputOperacionesExpresionesTextArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane scrollOutputOpsExp = new JScrollPane(outputOperacionesExpresionesTextArea);

        // Añadir botones de operaciones al panelOperacionesExpresiones
        crearBotonesOperacionesExpresion();

        panelLateralCompleto.add(panelOperacionesNumeros);
        labelResultadosOpNumeros = new JLabel("Resultados Op. Números:"); // Asignar a variable
        panelLateralCompleto.add(labelResultadosOpNumeros);
        panelLateralCompleto.add(scrollOutputOpsNum);
        panelLateralCompleto.add(Box.createRigidArea(new Dimension(0,5)));
        panelLateralCompleto.add(panelOperacionesExpresiones);
        labelResultadosOpExpresiones = new JLabel("Resultados Op. Expresiones:"); // Asignar a variable
        panelLateralCompleto.add(labelResultadosOpExpresiones);
        panelLateralCompleto.add(scrollOutputOpsExp);
        
        add(panelLateralCompleto, BorderLayout.EAST);

        // --- Configurar Action Listeners --- 
        insertarButton.addActionListener(e -> ejecutarAgregarDato());
        btnEliminarDato.addActionListener(e -> ejecutarEliminarDato());
        mostrarRecorridoButton.addActionListener(e -> ejecutarMostrarRecorrido());

        // Configurar visibilidad inicial de componentes según el modo
        actualizarControlesSegunModo();
        actualizarPanelLateral(); // Leyenda e instrucciones
    }
    
    // Método para configurar/actualizar panel lateral (leyenda, instrucciones y visibilidad de paneles de operaciones)
    private void actualizarPanelLateral() {
         actualizarControlesSegunModo(); // Asegura que los paneles de operaciones correctos estén visibles

         if (modoActual == Modo.NUMEROS) {
             leyendaLabel.setText(
                 "<html><b>Leyenda (Números):</b><br>" +
                 "<font color='gray'><b>●</b></font> Número<br>" +
                 "<font color='green'><b>●</b></font> Padre Seleccionado (Inserción)<br><hr>" + // Verde es padre para inserción
                 "</html>"
             );
             instruccionesTextArea.setText(
                 "Modo: Árbol de Números\n\n" +
                 "1. Insertar Raíz: Escriba un número y pulse 'Insertar Raíz/Hijo' (sin padre sel.).\n\n" +
                 "2. Seleccionar Padre: Clic en un nodo. Se vuelve verde.\n\n" +
                 "3. Insertar Hijo: Con padre sel., escriba valor, elija posición, y pulse 'Insertar Raíz/Hijo'.\n\n" +
                 "4. Eliminar Dato: Escriba un número y pulse 'Eliminar Dato'.\n\n" +
                 "5. Operaciones/Recorridos: Use los paneles laterales correspondientes."
             );
         } else { // Modo EXPRESIONES
             leyendaLabel.setText(
                 "<html><b>Leyenda (Expresiones):</b><br>" +
                 "<font color='rgb(200, 200, 0)'><b>●</b></font> Operador<br>" + 
                 "<font color='gray'><b>●</b></font> Número (Operando)<br>" +
                 "<font color='green'><b>●</b></font> Padre Seleccionado (Inserción)<br><hr>" +
                 "</html>"
             );
              instruccionesTextArea.setText(
                 "Modo: Árbol de Expresiones\n\n"+
                 "1. Insertar Raíz: Escriba operador/número y pulse 'Insertar Raíz/Hijo' (sin padre sel.).\n\n" +
                 "2. Seleccionar Padre: Clic en un nodo OPERADOR (amarillo). Se vuelve verde.\n\n" +
                 "3. Insertar Hijo: Con padre sel., escriba valor, elija posición, y pulse 'Insertar Raíz/Hijo'.\n\n"+
                 "4. Eliminar Dato/Operaciones/Recorridos: Use los controles/paneles correspondientes."
             );
         }
         instruccionesTextArea.setCaretPosition(0); // Scroll al inicio
    }

    // Método para gestionar la visibilidad de controles según el modo
    private void actualizarControlesSegunModo() {
        boolean esModoNumeros = (modoActual == Modo.NUMEROS);
        boolean hayPadreSeleccionado = (nodoSeleccionadoPorClic != null);
        Object raizActual = null;
        if (modoActual == Modo.NUMEROS) raizActual = ((ArbolNumero)arbol).getRaiz();
        else raizActual = ((ArbolExpresion)arbol).getRaiz();
        boolean arbolVacio = (raizActual == null);

        // Controles de inserción manual (posición, padre)
        // Visibles si no es inserción de raíz (es decir, si hay padre o si el árbol no está vacío y no hay padre, para indicar que se necesita uno)
        etiquetaPosicion.setVisible(hayPadreSeleccionado && !arbolVacio);
        izquierdoRadioButton.setVisible(hayPadreSeleccionado && !arbolVacio);
        derechoRadioButton.setVisible(hayPadreSeleccionado && !arbolVacio);
        etiquetaPadreSeleccionado.setVisible(true); // Siempre visible para mostrar estado

        insertarButton.setText(arbolVacio ? "Insertar Raíz" : "Insertar Hijo");

        panelOperacionesNumeros.setVisible(esModoNumeros);
        // Controlar visibilidad de JTextArea y JLabel asociada
        outputOperacionesNumerosTextArea.setVisible(esModoNumeros);
        if (labelResultadosOpNumeros != null) labelResultadosOpNumeros.setVisible(esModoNumeros);
        // El scrollpane se hace visible/invisible si su contenido lo está, o podemos controlarlo explícitamente
        if (outputOperacionesNumerosTextArea.getParent() != null && outputOperacionesNumerosTextArea.getParent() instanceof JViewport && outputOperacionesNumerosTextArea.getParent().getParent() instanceof JScrollPane) {
            ((JScrollPane)outputOperacionesNumerosTextArea.getParent().getParent()).setVisible(esModoNumeros);
        }


        panelOperacionesExpresiones.setVisible(!esModoNumeros);
        outputOperacionesExpresionesTextArea.setVisible(!esModoNumeros);
        if (labelResultadosOpExpresiones != null) labelResultadosOpExpresiones.setVisible(!esModoNumeros);
        if (outputOperacionesExpresionesTextArea.getParent() != null && outputOperacionesExpresionesTextArea.getParent() instanceof JViewport && outputOperacionesExpresionesTextArea.getParent().getParent() instanceof JScrollPane) {
            ((JScrollPane)outputOperacionesExpresionesTextArea.getParent().getParent()).setVisible(!esModoNumeros);
        }
    }

    // --- Métodos de Lógica de la GUI (Adaptados al modo) --- 

    private void ejecutarAgregarDato() { // Renombrar mentalmente a ejecutarInsertarRaizOHijo
        String valorTexto = valorEntradaTextField.getText().trim();
        if (valorTexto.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese un valor.", "Entrada Vacía", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean esIzquierdo = izquierdoRadioButton.isSelected();
        Object padreSeleccionado = this.nodoSeleccionadoPorClic; // Usar el nodo clickeado como padre

        try {
            if (modoActual == Modo.NUMEROS) {
                ArbolNumero arbolNum = (ArbolNumero) arbol;
                int valorNum = Integer.parseInt(valorTexto);

                if (arbolNum.getRaiz() == null) { // Insertar como raíz
                    arbolNum.AgregarDato(valorNum); // O arbolNum.insertarComoHijo(null, valorNum, true) si se prefiere usar siempre el nuevo
                                                 // AgregarDato actual ya inserta como raíz si está vacío.
                    outputOperacionesNumerosTextArea.setText("Raíz " + valorNum + " insertada.");
                    setNodoClickeadoInfo(null); // Limpiar selección
                } else if (padreSeleccionado != null) { // Insertar como hijo
                    if (!(padreSeleccionado instanceof NodoNumero)) {
                        JOptionPane.showMessageDialog(this, "Error: Padre seleccionado no es válido.", "Error de Tipo", JOptionPane.ERROR_MESSAGE); return;
                    }
                    boolean exito = arbolNum.insertarComoHijo((NodoNumero) padreSeleccionado, valorNum, esIzquierdo);
                    if (exito) {
                        outputOperacionesNumerosTextArea.setText("Hijo " + valorNum + " insertado.");
                    } else {
                         outputOperacionesNumerosTextArea.setText("Fallo al insertar hijo " + valorNum + ".");
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Seleccione un nodo padre o inserte como raíz si el árbol está vacío.", "Error de Inserción", JOptionPane.WARNING_MESSAGE);
                    return; // No hacer nada si no hay raíz y no hay padre
                }

            } else { // Modo EXPRESIONES
                ArbolExpresion arbolExp = (ArbolExpresion) arbol;

                if (arbolExp.getRaiz() == null) { // Insertar como raíz
                    arbolExp.insertar(valorTexto); // Método original de ArbolExpresion para raíz
                    outputOperacionesExpresionesTextArea.setText("Raíz '" + valorTexto + "' insertada.");
                    setNodoClickeadoInfo(null); // Limpiar selección
                } else if (padreSeleccionado != null) { // Insertar como hijo
                    if (!(padreSeleccionado instanceof NodoExpresion)) {
                         JOptionPane.showMessageDialog(this, "Error: Padre seleccionado no es válido.", "Error de Tipo", JOptionPane.ERROR_MESSAGE); return;
                    }
                    NodoExpresion padreExp = (NodoExpresion) padreSeleccionado;
                    if (!padreExp.esOperador()) {
                        JOptionPane.showMessageDialog(this, "Error: El padre seleccionado debe ser un OPERADOR.", "Error de Inserción", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    boolean exito = arbolExp.insertarComoHijo(padreExp, valorTexto, esIzquierdo);
                     if (exito) {
                        outputOperacionesExpresionesTextArea.setText("Hijo '" + valorTexto + "' insertado.");
                    } else {
                         outputOperacionesExpresionesTextArea.setText("Fallo al insertar hijo '" + valorTexto + "'.");
                    }
                } else {
                     JOptionPane.showMessageDialog(this, "Seleccione un nodo padre (operador) o inserte como raíz si el árbol está vacío.", "Error de Inserción", JOptionPane.WARNING_MESSAGE);
                    return; 
                }
            }
            valorEntradaTextField.setText("");
            arbolPanel.repaint();
            actualizarControlesSegunModo(); // Actualiza texto del botón y visibilidad de controles de posición
        } catch (NumberFormatException ex) {
            if (modoActual == Modo.NUMEROS) {
                JOptionPane.showMessageDialog(this, "Entrada inválida para árbol de números. Ingrese un entero.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
            } else {
                 outputOperacionesExpresionesTextArea.setText("Error al procesar valor: " + valorTexto); // NFE no debería ocurrir para String
            }
        } catch (Exception ex) {
            String targetOutputArea = (modoActual == Modo.NUMEROS) ? "outputOperacionesNumerosTextArea" : "outputOperacionesExpresionesTextArea";
            String errorMsg = (modoActual == Modo.NUMEROS ? "Error en árbol de números: " : "Error en árbol de expresiones: ") + ex.getMessage();
            if (targetOutputArea.equals("outputOperacionesNumerosTextArea")) outputOperacionesNumerosTextArea.setText(errorMsg);
            else outputOperacionesExpresionesTextArea.setText(errorMsg);
            JOptionPane.showMessageDialog(this, errorMsg, "Error de Operación", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    private void ejecutarEliminarDato() {
        String valorTexto = valorEliminarTextField.getText().trim();
        if (valorTexto.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese valor a eliminar.", "Entrada Vacía", JOptionPane.WARNING_MESSAGE);
            return;
        }
        boolean eliminado = false;
        String mensajeResultado = "";

        try {
            if (modoActual == Modo.NUMEROS) {
                int valorNum = Integer.parseInt(valorTexto);
                ArbolNumero arbolNum = (ArbolNumero) arbol;
                eliminado = arbolNum.EliminarDato(valorNum);
                mensajeResultado = "Dato " + valorNum + (eliminado ? " eliminado." : " no encontrado para eliminar.");
                if (eliminado && nodoSeleccionadoPorClic instanceof NodoNumero && ((NodoNumero)nodoSeleccionadoPorClic).getValor() == valorNum) {
                    setNodoClickeadoInfo(null);
                }
                outputOperacionesNumerosTextArea.setText(mensajeResultado);
            } else { // Modo EXPRESIONES
                ArbolExpresion arbolExp = (ArbolExpresion) arbol;
                eliminado = arbolExp.EliminarDato(valorTexto);
                mensajeResultado = "Dato '" + valorTexto + (eliminado ? "' eliminado." : "' no encontrado o no es hoja para eliminar.");
                 if (eliminado && nodoSeleccionadoPorClic instanceof NodoExpresion && ((NodoExpresion)nodoSeleccionadoPorClic).getValor().equals(valorTexto)) {
                    setNodoClickeadoInfo(null);
                }
                outputOperacionesExpresionesTextArea.setText(mensajeResultado);
            }
            if (eliminado) {
                arbolPanel.repaint();
            }
            valorEliminarTextField.setText("");
        } catch (NumberFormatException ex) {
            if (modoActual == Modo.NUMEROS) {
                 JOptionPane.showMessageDialog(this, "Valor a eliminar debe ser numérico para árbol de números.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
            } else {
                 // No debería ocurrir para expresiones ya que el valor es String.
                 outputOperacionesExpresionesTextArea.setText("Error de formato al eliminar: " + ex.getMessage());
            }
        } catch (Exception ex) {
            String targetOutput = (modoActual == Modo.NUMEROS) ? 
                                  "Error al eliminar en árbol de números: " : "Error al eliminar en árbol de expresiones: ";
            JOptionPane.showMessageDialog(this, targetOutput + ex.getMessage(), "Error de Operación", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void ejecutarMostrarRecorrido() {
        String tipoSeleccionado = (String) tipoRecorridoComboBox.getSelectedItem();
        String resultadoStr = ""; 
        try {
            if (modoActual == Modo.NUMEROS) {
                ArbolNumero arbolNum = (ArbolNumero) arbol;
                switch (tipoSeleccionado) {
                    case "Inorden": resultadoStr = arbolNum.RecorrerInorden(); break; 
                    case "Preorden": resultadoStr = arbolNum.RecorrerPreorden(); break; 
                    case "Postorden": resultadoStr = arbolNum.RecorrerPostorden(); break; 
                    case "Amplitud": resultadoStr = arbolNum.imprimirAmplitud(); break; 
                }
            } else { // Modo EXPRESIONES
                 ArbolExpresion arbolExp = (ArbolExpresion) arbol;
                switch (tipoSeleccionado) {
                    case "Inorden": resultadoStr = arbolExp.RecorrerInorden(); break; 
                    case "Preorden": resultadoStr = arbolExp.RecorrerPreorden(); break; 
                    case "Postorden": resultadoStr = arbolExp.RecorrerPostorden(); break; 
                    case "Amplitud": resultadoStr = arbolExp.imprimirAmplitud(); break;
                }
            }
            resultadoOperacionesTextArea.setText(resultadoStr);
        } catch (Exception ex) {
             JOptionPane.showMessageDialog(this, "Error al obtener recorrido: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
             ex.printStackTrace();
        }
    }

    // Para actualizar la etiqueta del nodo clickeado (info)
    public void setNodoClickeadoInfo(Object nodo) {
        this.nodoSeleccionadoPorClic = nodo; // Este es ahora el PADRE POTENCIAL
        String valorNodoStr = "Ninguno";
        boolean esPadreValidoParaInsercion = false;

        if (nodo != null) {
            try {
                 if (nodo instanceof NodoNumero) {
                     valorNodoStr = String.valueOf(((NodoNumero) nodo).getValor());
                     esPadreValidoParaInsercion = true; // Cualquier nodo número puede ser padre
                 } else if (nodo instanceof NodoExpresion) {
                     NodoExpresion nodoExp = (NodoExpresion) nodo;
                     valorNodoStr = nodoExp.getValor();
                     esPadreValidoParaInsercion = nodoExp.esOperador(); // Solo operadores pueden ser padres
                 }
            } catch (ClassCastException cce) { valorNodoStr = "Error Casting"; }
        }
        
        etiquetaNodoSeleccionadoPorClic.setText("Info nodo click: " + valorNodoStr); // Info general del nodo
        if(esPadreValidoParaInsercion || nodo == null){ // Si es válido o no hay nada seleccionado
            etiquetaPadreSeleccionado.setText("Padre para inserción: " + valorNodoStr);
        } else {
            // Si se clickeó un nodo no válido como padre (ej. operando en modo expresión)
            etiquetaPadreSeleccionado.setText("Padre para inserción: (Inválido: " + valorNodoStr + ")");
            // No deseleccionamos nodoSeleccionadoPorClic aquí, para que el resaltado verde aún muestre el último click.
            // La lógica de inserción se encargará de prevenir la inserción.
        }
        
        arbolPanel.repaint(); 
        actualizarControlesSegunModo(); // Para actualizar el texto del botón y visibilidad de controles
    }

    private void crearBotonesOperacionesNumero() {
        if (modoActual != Modo.NUMEROS) return;
        panelOperacionesNumeros.removeAll(); // Limpiar por si se llama múltiples veces

        JButton btnEstaVacio = new JButton("estaVacio()");
        btnEstaVacio.addActionListener(e -> {
            ArbolNumero an = (ArbolNumero) arbol;
            outputOperacionesNumerosTextArea.setText("Árbol vacío: " + an.estaVacio());
        });
        panelOperacionesNumeros.add(btnEstaVacio);

        JButton btnObtenerPeso = new JButton("obtenerPeso()");
        btnObtenerPeso.addActionListener(e -> {
            ArbolNumero an = (ArbolNumero) arbol;
            outputOperacionesNumerosTextArea.setText("Peso (nodos): " + an.obtenerPeso());
        });
        panelOperacionesNumeros.add(btnObtenerPeso);
        
        JButton btnObtenerAltura = new JButton("obtenerAltura()");
        btnObtenerAltura.addActionListener(e -> {
            ArbolNumero an = (ArbolNumero) arbol;
            outputOperacionesNumerosTextArea.setText("Altura: " + an.obtenerAltura());
        });
        panelOperacionesNumeros.add(btnObtenerAltura);

        JButton btnContarHojas = new JButton("contarHojas()");
        btnContarHojas.addActionListener(e -> {
            ArbolNumero an = (ArbolNumero) arbol;
            outputOperacionesNumerosTextArea.setText("Hojas: " + an.contarHojas());
        });
        panelOperacionesNumeros.add(btnContarHojas);

        JButton btnObtenerMenor = new JButton("obtenerMenor()");
        btnObtenerMenor.addActionListener(e -> {
            ArbolNumero an = (ArbolNumero) arbol;
            try {
                outputOperacionesNumerosTextArea.setText("Menor valor: " + an.obtenerMenor());
            } catch (NoSuchElementException ex) { outputOperacionesNumerosTextArea.setText(ex.getMessage()); }
        });
        panelOperacionesNumeros.add(btnObtenerMenor);

        // En ArbolNumero se llama obtenerNodoMayor()
        JButton btnObtenerMayorNum = new JButton("obtenerNodoMayor()"); 
        btnObtenerMayorNum.addActionListener(e -> {
            ArbolNumero an = (ArbolNumero) arbol;
             try {
                // El método se llama obtenerNodoMayor, pero devuelve int.
                // Si devuelve NodoNumero, sería an.obtenerNodoMayor().getValor()
                outputOperacionesNumerosTextArea.setText("Mayor valor: " + an.obtenerNodoMayor());
            } catch (NoSuchElementException ex) { outputOperacionesNumerosTextArea.setText(ex.getMessage()); }
        });
        panelOperacionesNumeros.add(btnObtenerMayorNum);

        // Para existeDato y obtenerNivel, necesitamos un campo de entrada común
        JTextField valorOperacionFieldNum = new JTextField(5); // Renombrado para evitar colisión
        JButton btnExisteDato = new JButton("existeDato(valor)");
        btnExisteDato.addActionListener(e -> {
            try {
                int val = Integer.parseInt(valorOperacionFieldNum.getText());
                ArbolNumero an = (ArbolNumero) arbol;
                outputOperacionesNumerosTextArea.setText("Existe " + val + ": " + an.existeDato(val));
            } catch (NumberFormatException ex) { outputOperacionesNumerosTextArea.setText("Entrada inválida para valor."); }
        });

        JButton btnObtenerNivel = new JButton("obtenerNivel(valor)");
        btnObtenerNivel.addActionListener(e -> {
            try {
                int val = Integer.parseInt(valorOperacionFieldNum.getText());
                ArbolNumero an = (ArbolNumero) arbol;
                int nivel = an.obtenerNivel(val);
                outputOperacionesNumerosTextArea.setText(nivel > 0 ? "Nivel de " + val + ": " + nivel : "Dato " + val + " no encontrado.");
            } catch (NumberFormatException ex) { outputOperacionesNumerosTextArea.setText("Entrada inválida para valor."); }
        });
        
        JPanel panelValorizadoNum = new JPanel(new FlowLayout(FlowLayout.LEFT)); // Renombrado
        panelValorizadoNum.add(new JLabel("Valor op:"));
        panelValorizadoNum.add(valorOperacionFieldNum);
        panelValorizadoNum.add(btnExisteDato);
        panelValorizadoNum.add(btnObtenerNivel);
        
        panelOperacionesNumeros.add(panelValorizadoNum);
        if (panelOperacionesNumeros.getComponentCount() % 2 != 0) { 
             panelOperacionesNumeros.add(new JLabel("")); 
        }

        JButton btnBorrarArbol = new JButton("borrarElArbol()");
        btnBorrarArbol.addActionListener(_ -> {
            ArbolNumero an = (ArbolNumero) arbol;
            an.borrarElArbol();
            setNodoClickeadoInfo(null);
            arbolPanel.repaint();
            outputOperacionesNumerosTextArea.setText("Árbol borrado.");
            resultadoOperacionesTextArea.setText("");
        });
        panelOperacionesNumeros.add(btnBorrarArbol);
        panelOperacionesNumeros.revalidate();
        panelOperacionesNumeros.repaint();
    }

    private void crearBotonesOperacionesExpresion() {
        if (modoActual != Modo.EXPRESIONES) return;
        panelOperacionesExpresiones.removeAll();

        ArbolExpresion ae = (ArbolExpresion) arbol; // Para acceso en lambdas

        JButton btnEstaVacio = new JButton("estaVacio()");
        btnEstaVacio.addActionListener(e -> outputOperacionesExpresionesTextArea.setText("Árbol vacío: " + ae.estaVacio()));
        panelOperacionesExpresiones.add(btnEstaVacio);

        JButton btnObtenerPeso = new JButton("obtenerPeso()");
        btnObtenerPeso.addActionListener(e -> outputOperacionesExpresionesTextArea.setText("Peso (nodos): " + ae.obtenerPeso()));
        panelOperacionesExpresiones.add(btnObtenerPeso);
        
        JButton btnObtenerAltura = new JButton("obtenerAltura()");
        btnObtenerAltura.addActionListener(e -> outputOperacionesExpresionesTextArea.setText("Altura: " + ae.obtenerAltura()));
        panelOperacionesExpresiones.add(btnObtenerAltura);

        JButton btnContarHojas = new JButton("contarHojas()");
        btnContarHojas.addActionListener(e -> outputOperacionesExpresionesTextArea.setText("Hojas: " + ae.contarHojas()));
        panelOperacionesExpresiones.add(btnContarHojas);

        JButton btnObtenerMenor = new JButton("obtenerMenor()");
        btnObtenerMenor.addActionListener(e -> {
            try {
                outputOperacionesExpresionesTextArea.setText("Menor operando: " + ae.obtenerMenor());
            } catch (NoSuchElementException | ClassCastException ex) { outputOperacionesExpresionesTextArea.setText(ex.getMessage()); }
        });
        panelOperacionesExpresiones.add(btnObtenerMenor);
        
        JButton btnObtenerMayor = new JButton("obtenerMayor()");
        btnObtenerMayor.addActionListener(e -> {
             try {
                outputOperacionesExpresionesTextArea.setText("Mayor operando: " + ae.obtenerMayor());
            } catch (NoSuchElementException | ClassCastException ex) { outputOperacionesExpresionesTextArea.setText(ex.getMessage()); }
        });
        panelOperacionesExpresiones.add(btnObtenerMayor);

        // obtenerNodoMenor y obtenerNodoMayor devuelven NodoExpresion, mostramos su valor.
        JButton btnGetNodoMenor = new JButton("obtenerNodoMenor()");
        btnGetNodoMenor.addActionListener(e -> {
            try {
                NodoExpresion nodo = ae.obtenerNodoMenor();
                outputOperacionesExpresionesTextArea.setText(nodo != null ? "Nodo Menor: " + nodo.getValor() : "No hay operandos numéricos");
            } catch (NoSuchElementException | ClassCastException ex) { outputOperacionesExpresionesTextArea.setText(ex.getMessage()); }
        });
        panelOperacionesExpresiones.add(btnGetNodoMenor);

        JButton btnGetNodoMayor = new JButton("obtenerNodoMayor()");
        btnGetNodoMayor.addActionListener(_ -> {
            try {
                NodoExpresion nodo = ae.obtenerNodoMayor();
                outputOperacionesExpresionesTextArea.setText(nodo != null ? "Nodo Mayor: " + nodo.getValor() : "No hay operandos numéricos");
            } catch (NoSuchElementException | ClassCastException ex) { outputOperacionesExpresionesTextArea.setText(ex.getMessage()); }
        });
        panelOperacionesExpresiones.add(btnGetNodoMayor);


        JTextField valorOperacionFieldExp = new JTextField(5);
        JButton btnExisteDato = new JButton("existeDato(valor)");
        btnExisteDato.addActionListener(_ -> {
            String val = valorOperacionFieldExp.getText().trim();
            if(val.isEmpty()){ outputOperacionesExpresionesTextArea.setText("Ingrese valor para buscar/nivel."); return;}
            outputOperacionesExpresionesTextArea.setText("Existe '" + val + "': " + ae.existeDato(val));
        });

        JButton btnObtenerNivel = new JButton("obtenerNivel(valor)");
        btnObtenerNivel.addActionListener(_ -> {
            String val = valorOperacionFieldExp.getText().trim();
            if(val.isEmpty()){ outputOperacionesExpresionesTextArea.setText("Ingrese valor para buscar/nivel."); return;}
            int nivel = ae.obtenerNivel(val);
            outputOperacionesExpresionesTextArea.setText(nivel > 0 ? "Nivel de '" + val + "': " + nivel : "Dato '" + val + "' no encontrado.");
        });
        
        JPanel panelValorizadoExp = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelValorizadoExp.add(new JLabel("Valor op:"));
        panelValorizadoExp.add(valorOperacionFieldExp);
        panelValorizadoExp.add(btnExisteDato);
        panelValorizadoExp.add(btnObtenerNivel);
        
        panelOperacionesExpresiones.add(panelValorizadoExp);
         if (panelOperacionesExpresiones.getComponentCount() % 2 != 0) { 
             panelOperacionesExpresiones.add(new JLabel("")); 
        }


        JButton btnBorrarArbol = new JButton("borrarElArbol()");
        btnBorrarArbol.addActionListener(_ -> {
            ae.borrarElArbol();
            setNodoClickeadoInfo(null);
            arbolPanel.repaint();
            outputOperacionesExpresionesTextArea.setText("Árbol de expresiones borrado.");
            resultadoOperacionesTextArea.setText("");
        });
        panelOperacionesExpresiones.add(btnBorrarArbol);

        panelOperacionesExpresiones.revalidate();
        panelOperacionesExpresiones.repaint();
    }

    // --- Clase interna para el Panel de Dibujo (Adaptada) --- 
    private class ArbolPanel extends JPanel {
        private static final int DIAMETRO_NODO = 30;
        private static final int ESPACIO_VERTICAL = 50;

        // No necesita referencia directa al árbol aquí, usa la de la clase externa

        public ArbolPanel() {
            addMouseListener(new java.awt.event.MouseAdapter() {
                 public void mouseClicked(java.awt.event.MouseEvent evt) {
                    Object raizActual = null;
                    try {
                        if (VentanaArbol.this.modoActual == Modo.NUMEROS) {
                            raizActual = ((ArbolNumero) VentanaArbol.this.arbol).getRaiz();
                        } else {
                            raizActual = ((ArbolExpresion) VentanaArbol.this.arbol).getRaiz();
                        }
                    } catch (ClassCastException cce) { return; }
                    
                    if (raizActual != null) {
                        Object nodoClickeado = encontrarNodoEnPosicion(raizActual, evt.getX(), evt.getY());
                        VentanaArbol.this.setNodoClickeadoInfo(nodoClickeado); // Nueva llamada más genérica
                    }
                 }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            Object raizActual = null;
             try {
                if (VentanaArbol.this.modoActual == Modo.NUMEROS) {
                    raizActual = ((ArbolNumero) VentanaArbol.this.arbol).getRaiz();
                } else {
                    raizActual = ((ArbolExpresion) VentanaArbol.this.arbol).getRaiz();
                }
             } catch (ClassCastException cce) { return; }

            if (raizActual != null) {
                dibujarNodo(g2d, getWidth() / 2, 30 + DIAMETRO_NODO, raizActual, getWidth() / 4);
            }
        }

        // Dibuja recursivamente el árbol (adaptado para Object)
        private void dibujarNodo(Graphics2D g, int x, int y, Object nodoObj, int espacioHorizontal) {
            if (nodoObj == null) return;
            
            String textoValor = "ERR";
            boolean esOperadorExp = false; // Solo relevante para modo Expresión
            Object hijoIzquierdo = null;
            Object hijoDerecho = null;

            try {
                // Extraer info según el tipo de nodo
                if (VentanaArbol.this.modoActual == Modo.NUMEROS) {
                    NodoNumero nodoNum = (NodoNumero) nodoObj;
                    textoValor = String.valueOf(nodoNum.getValor());
                    hijoIzquierdo = nodoNum.getIzquierdo();
                    hijoDerecho = nodoNum.getDerecho();
                } else {
                    NodoExpresion nodoExp = (NodoExpresion) nodoObj;
                    textoValor = nodoExp.getValor();
                    esOperadorExp = nodoExp.esOperador();
                    hijoIzquierdo = nodoExp.getIzquierdo();
                    hijoDerecho = nodoExp.getDerecho();
                }
            } catch (ClassCastException cce) { /* Dibujar error si falla */ }

            // Determinar color
            Color colorNodo = Color.DARK_GRAY; // Color por defecto/error
            if (VentanaArbol.this.modoActual == Modo.NUMEROS) {
                 colorNodo = Color.LIGHT_GRAY;
                 // Para modo NUMEROS, el resaltado verde es solo para el nodo clickeado (info)
                 if (nodoObj == VentanaArbol.this.nodoSeleccionadoPorClic) {
                     colorNodo = Color.GREEN; 
                 }
             } else {
                 colorNodo = esOperadorExp ? Color.YELLOW : Color.LIGHT_GRAY;
                 // Para modo EXPRESIONES, el resaltado verde es el padre para inserción
                 if (nodoObj == VentanaArbol.this.nodoSeleccionadoPorClic) { // nodoSeleccionadoPorClic es el padre en modo exp
                     colorNodo = Color.GREEN; 
                 }
             }
            
            // Dibujar el nodo
            g.setColor(colorNodo);
            g.fillOval(x - DIAMETRO_NODO / 2, y - DIAMETRO_NODO / 2, DIAMETRO_NODO, DIAMETRO_NODO);
            g.setColor(Color.BLACK);
            g.setStroke(new BasicStroke(1.5f)); 
            g.drawOval(x - DIAMETRO_NODO / 2, y - DIAMETRO_NODO / 2, DIAMETRO_NODO, DIAMETRO_NODO);
            
            FontMetrics fm = g.getFontMetrics();
            g.drawString(textoValor, x - fm.stringWidth(textoValor) / 2, y + fm.getAscent() / 2);

            // Dibujar hijos recursivamente
            if (hijoIzquierdo != null) {
                int xIzq = x - espacioHorizontal;
                int yIzq = y + ESPACIO_VERTICAL;
                g.drawLine(x, y + DIAMETRO_NODO / 2, xIzq, yIzq - DIAMETRO_NODO / 2);
                dibujarNodo(g, xIzq, yIzq, hijoIzquierdo, espacioHorizontal / 2);
            }
            if (hijoDerecho != null) {
                int xDer = x + espacioHorizontal;
                int yDer = y + ESPACIO_VERTICAL;
                g.drawLine(x, y + DIAMETRO_NODO / 2, xDer, yDer - DIAMETRO_NODO / 2);
                dibujarNodo(g, xDer, yDer, hijoDerecho, espacioHorizontal / 2);
            }
        }
        
        // Encuentra qué nodo (Object) está en la posición (Adaptado)
        private Object encontrarNodoEnPosicion(Object raizObj, int clickX, int clickY) {
             if (raizObj == null) return null;
             return encontrarNodoRecursivoPos(raizObj, getWidth() / 2, 30 + DIAMETRO_NODO, getWidth() / 4, clickX, clickY);
        }

        private Object encontrarNodoRecursivoPos(Object nodoObj, int x, int y, int espacioHorizontal, int clickX, int clickY) {
            if (nodoObj == null) { return null; }

            double distancia = Math.sqrt(Math.pow(clickX - x, 2) + Math.pow(clickY - y, 2));
            if (distancia <= DIAMETRO_NODO / 2.0) {
                return nodoObj; 
            }

            Object hijoIzquierdo = null;
            Object hijoDerecho = null;
             try {
                if (nodoObj instanceof NodoNumero) {
                    hijoIzquierdo = ((NodoNumero) nodoObj).getIzquierdo();
                    hijoDerecho = ((NodoNumero) nodoObj).getDerecho();
                } else if (nodoObj instanceof NodoExpresion){
                    hijoIzquierdo = ((NodoExpresion) nodoObj).getIzquierdo();
                    hijoDerecho = ((NodoExpresion) nodoObj).getDerecho();
                }
            } catch (ClassCastException cce) { return null; /* No seguir si hay error */ }
            
            int xIzq = x - espacioHorizontal;
            int yIzq = y + ESPACIO_VERTICAL;
            Object encontrado = encontrarNodoRecursivoPos(hijoIzquierdo, xIzq, yIzq, espacioHorizontal / 2, clickX, clickY);
            if (encontrado != null) { return encontrado; }

            int xDer = x + espacioHorizontal;
            int yDer = y + ESPACIO_VERTICAL;
            encontrado = encontrarNodoRecursivoPos(hijoDerecho, xDer, yDer, espacioHorizontal / 2, clickX, clickY);
            return encontrado; 
        }
    }

    // --- Punto de entrada (Modificado para preguntar modo) --- 
    public static void main(String[] args) {
        // Preguntar al usuario el modo deseado
        String[] options = {"Árbol de Números", "Árbol de Expresiones"};
        int choice = JOptionPane.showOptionDialog(
            null, // Parent component
            "Seleccione el tipo de árbol binario a utilizar:", // Message
            "Selección de Modo", // Title
            JOptionPane.DEFAULT_OPTION, // optionType
            JOptionPane.QUESTION_MESSAGE, // messageType
            null, // Icon
            options, // Options
            options[0] // Default option
        );

        // Determinar el modo basado en la elección
        Modo modoSeleccionado;
        if (choice == 0) {
            modoSeleccionado = Modo.NUMEROS;
        } else if (choice == 1) {
            modoSeleccionado = Modo.EXPRESIONES;
        } else {
            System.out.println("Selección cancelada o cerrada. Saliendo.");
            return; // Salir si el usuario cierra el diálogo
        }

        // Ejecutar la GUI en el EDT con el modo seleccionado
        SwingUtilities.invokeLater(() -> {
            new VentanaArbol(modoSeleccionado).setVisible(true);
        });
    }
} 