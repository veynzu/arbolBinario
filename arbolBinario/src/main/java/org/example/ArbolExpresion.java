package org.example;

// Renombrado de ArbolBinario a ArbolExpresion
public class ArbolExpresion {

    NodoExpresion raiz; // Tipo actualizado

    public ArbolExpresion() { // Nombre actualizado
        this.raiz = null;
    }

    // Método de inserción básico (ahora con String)
    public void insertar(String valor) {
        if (this.raiz == null) {
            this.raiz = new NodoExpresion(valor); // Usa NodoExpresion
            System.out.println("NodoExpresion '" + valor + "' insertado como raíz.");
        } else {
            System.err.println("Error: La raíz ya existe. Use la selección manual para insertar hijos.");
        }
    }

    // --- Métodos de Recorrido (Ahora devuelven String) --- 

    public String recorridoInorden() {
        StringBuilder resultado = new StringBuilder();
        inordenRecursivo(raiz, resultado);
        return resultado.toString();
    }

    private void inordenRecursivo(NodoExpresion nodo, StringBuilder resultado) { // Tipo actualizado
        if (nodo != null) {
            boolean necesitaParentesis = nodo.esOperador();
            if (necesitaParentesis) { resultado.append("( "); }
            inordenRecursivo(nodo.getIzquierdo(), resultado);
            if (resultado.length() > 0 && !resultado.substring(resultado.length() - 2).equals("( ")) { resultado.append(" "); }
            resultado.append(nodo.getValor());
            if (resultado.length() > 0) { resultado.append(" "); }
            inordenRecursivo(nodo.getDerecho(), resultado);
            if (necesitaParentesis) {
                if (resultado.charAt(resultado.length() - 1) == ' ') { resultado.deleteCharAt(resultado.length() - 1); }
                resultado.append(")");
            }
        }
    }

    public String recorridoPreorden() {
        StringBuilder resultado = new StringBuilder();
        preordenRecursivo(raiz, resultado);
        return resultado.toString().trim(); 
    }

    private void preordenRecursivo(NodoExpresion nodo, StringBuilder resultado) { // Tipo actualizado
        if (nodo != null) {
            resultado.append(nodo.getValor()).append(" ");
            preordenRecursivo(nodo.getIzquierdo(), resultado);
            preordenRecursivo(nodo.getDerecho(), resultado);
        }
    }

    public String recorridoPostorden() {
        StringBuilder resultado = new StringBuilder();
        postordenRecursivo(raiz, resultado);
        return resultado.toString().trim();
    }

    private void postordenRecursivo(NodoExpresion nodo, StringBuilder resultado) { // Tipo actualizado
        if (nodo != null) {
            postordenRecursivo(nodo.getIzquierdo(), resultado);
            postordenRecursivo(nodo.getDerecho(), resultado);
            resultado.append(nodo.getValor()).append(" ");
        }
    }

    public NodoExpresion getRaiz() { // Tipo actualizado
        return raiz;
    }

    public NodoExpresion buscarNodoPorValor(String valor) { // Tipo actualizado
        return buscarNodoRecursivo(raiz, valor);
    }

    private NodoExpresion buscarNodoRecursivo(NodoExpresion nodo, String valor) { // Tipos actualizados
        if (nodo == null) { return null; }
        if (nodo.getValor().equals(valor)) { return nodo; }
        NodoExpresion encontradoIzquierdo = buscarNodoRecursivo(nodo.getIzquierdo(), valor);
        if (encontradoIzquierdo != null) { return encontradoIzquierdo; }
        return buscarNodoRecursivo(nodo.getDerecho(), valor);
    }

    public boolean insertarComoHijo(NodoExpresion padre, String valorNuevo, boolean esIzquierdo) { // Tipo actualizado
        if (padre == null) {
            System.err.println("Error: El nodo padre no puede ser nulo para la inserción.");
            return false;
        }
        if (!padre.esOperador()){
             System.err.println("Error: No se pueden añadir hijos a un nodo operando ('"+ padre.getValor()+"').");
             return false; 
        }
        NodoExpresion nuevoNodo = new NodoExpresion(valorNuevo); // Usa NodoExpresion
        if (esIzquierdo) {
            if (padre.getIzquierdo() != null) { System.out.println("Advertencia: Sobrescribiendo hijo izquierdo existente de '" + padre.getValor() + "'."); }
            padre.setIzquierdo(nuevoNodo);
            System.out.println("NodoExpresion '" + valorNuevo + "' insertado como hijo izquierdo de '" + padre.getValor() + "'");
        } else {
             if (padre.getDerecho() != null) { System.out.println("Advertencia: Sobrescribiendo hijo derecho existente de '" + padre.getValor() + "'."); }
            padre.setDerecho(nuevoNodo);
            System.out.println("NodoExpresion '" + valorNuevo + "' insertado como hijo derecho de '" + padre.getValor() + "'");
        }
        return true;
    }

    // --- Nuevos Métodos Solicitados ---

    public boolean estaVacio() {
        return this.raiz == null;
    }

    public void AgregarDato(String valor) {
        NodoExpresion nuevoNodo = new NodoExpresion(valor);
        if (this.raiz == null) {
            this.raiz = nuevoNodo;
            System.out.println("NodoExpresion '" + valor + "' insertado como raíz.");
            return;
        }

        java.util.Queue<NodoExpresion> cola = new java.util.LinkedList<>();
        cola.add(raiz);

        while (!cola.isEmpty()) {
            NodoExpresion actual = cola.poll();

            if (actual.esOperador()) {
                if (actual.getIzquierdo() == null) {
                    actual.setIzquierdo(nuevoNodo);
                    System.out.println("NodoExpresion '" + valor + "' insertado como hijo izquierdo de '" + actual.getValor() + "'.");
                    return;
                } else if (actual.getDerecho() == null) {
                    actual.setDerecho(nuevoNodo);
                    System.out.println("NodoExpresion '" + valor + "' insertado como hijo derecho de '" + actual.getValor() + "'.");
                    return;
                }
            }
            // Solo se añaden a la cola para seguir buscando si tienen potencial de ser padres (operadores)
            if (actual.getIzquierdo() != null) {
                cola.add(actual.getIzquierdo());
            }
            if (actual.getDerecho() != null) {
                cola.add(actual.getDerecho());
            }
        }
        // Si sale del bucle, significa que no encontró un operador con espacio.
        // Esto podría pasar si el árbol está lleno de operandos o operadores completos.
        // Opcionalmente, podríamos manejar este caso de forma diferente, por ejemplo, expandiendo el último operador.
        // Por ahora, si no encuentra un lugar, no se inserta (o se podría lanzar una excepción/mensaje).
        System.err.println("No se encontró una posición válida para insertar '" + valor + "' (operador con espacio libre).");
    }

    // Métodos de recorrido con nombres exactos solicitados
    public String RecorrerInorden() {
        return recorridoInorden(); // Llama al método existente
    }

    public String RecorrerPreorden() {
        return recorridoPreorden(); // Llama al método existente
    }

    public String RecorrerPostorden() {
        return recorridoPostorden(); // Llama al método existente
    }

    public boolean existeDato(String valor) {
        return buscarNodoPorValor(valor) != null;
    }

    public int obtenerPeso() {
        return obtenerPesoRecursivo(raiz);
    }

    private int obtenerPesoRecursivo(NodoExpresion nodo) {
        if (nodo == null) {
            return 0;
        }
        return 1 + obtenerPesoRecursivo(nodo.getIzquierdo()) + obtenerPesoRecursivo(nodo.getDerecho());
    }

    public int obtenerAltura() {
        return obtenerAlturaRecursivo(raiz);
    }

    private int obtenerAlturaRecursivo(NodoExpresion nodo) {
        if (nodo == null) {
            return 0; // Altura de un árbol vacío o nodo nulo es 0 según algunas convenciones, o -1. Para altura de árbol >=1 nodo, es 0 para hoja.
                     // Si se quiere que una hoja tenga altura 1, entonces return 1 aquí y ajustar.
                     // Si se quiere que raíz única tenga altura 0, caso base -1 y +1 en llamada inicial.
                     // Usaremos la convención de que un nodo hoja tiene altura 1, árbol vacío altura 0.
        }
        if(nodo.getIzquierdo() == null && nodo.getDerecho() == null && nodo == raiz) return 1; // Raiz unica altura 1
        if(nodo.getIzquierdo() == null && nodo.getDerecho() == null) return 1; // Hoja altura 1

        int alturaIzquierda = obtenerAlturaRecursivo(nodo.getIzquierdo());
        int alturaDerecha = obtenerAlturaRecursivo(nodo.getDerecho());
        return Math.max(alturaIzquierda, alturaDerecha) + 1;
    }


    public int obtenerNivel(String valor) {
        return obtenerNivelRecursivo(raiz, valor, 1);
    }

    private int obtenerNivelRecursivo(NodoExpresion nodo, String valor, int nivelActual) {
        if (nodo == null) {
            return 0; // O -1 para indicar no encontrado
        }
        if (nodo.getValor().equals(valor)) {
            return nivelActual;
        }
        int nivelIzquierdo = obtenerNivelRecursivo(nodo.getIzquierdo(), valor, nivelActual + 1);
        if (nivelIzquierdo != 0) {
            return nivelIzquierdo;
        }
        return obtenerNivelRecursivo(nodo.getDerecho(), valor, nivelActual + 1);
    }

    public int contarHojas() {
        return contarHojasRecursivo(raiz);
    }

    private int contarHojasRecursivo(NodoExpresion nodo) {
        if (nodo == null) {
            return 0;
        }
        // Una hoja en un árbol de expresión es un operando (no es operador)
        // O podría ser un operador sin hijos (si permitimos operadores como hojas en algún caso)
        // La definición actual de insertarComoHijo ya previene añadir hijos a operandos.
        // Un nodo es hoja si no tiene hijos. En expresiones, los operandos son siempre hojas.
        // Los operadores pueden ser hojas si no se les han asignado hijos.
        if (nodo.getIzquierdo() == null && nodo.getDerecho() == null) {
            return 1;
        }
        return contarHojasRecursivo(nodo.getIzquierdo()) + contarHojasRecursivo(nodo.getDerecho());
    }


    private void obtenerOperandosNumericos(NodoExpresion nodo, java.util.List<Double> operandos) {
        if (nodo == null) {
            return;
        }
        if (!nodo.esOperador()) { // Es un operando
            try {
                operandos.add(Double.parseDouble(nodo.getValor()));
            } catch (NumberFormatException e) {
                // Ignorar operandos no numéricos para min/max
            }
        }
        obtenerOperandosNumericos(nodo.getIzquierdo(), operandos);
        obtenerOperandosNumericos(nodo.getDerecho(), operandos);
    }

    public String obtenerMenor() {
        if (raiz == null) throw new java.util.NoSuchElementException("Árbol vacío");
        java.util.List<Double> operandos = new java.util.ArrayList<>();
        obtenerOperandosNumericos(raiz, operandos);
        if (operandos.isEmpty()) throw new java.util.NoSuchElementException("No hay operandos numéricos en el árbol");
        return String.valueOf(operandos.stream().min(Double::compare).get());
    }

    public String obtenerMayor() {
        if (raiz == null) throw new java.util.NoSuchElementException("Árbol vacío");
        java.util.List<Double> operandos = new java.util.ArrayList<>();
        obtenerOperandosNumericos(raiz, operandos);
        if (operandos.isEmpty()) throw new java.util.NoSuchElementException("No hay operandos numéricos en el árbol");
        return String.valueOf(operandos.stream().max(Double::compare).get());
    }


    public NodoExpresion obtenerNodoMenor() {
        if (raiz == null) throw new java.util.NoSuchElementException("Árbol vacío");
        final java.util.List<NodoExpresion> nodosOperandos = new java.util.ArrayList<>();
        obtenerNodosOperandosNumericos(raiz, nodosOperandos);

        if (nodosOperandos.isEmpty()) throw new java.util.NoSuchElementException("No hay operandos numéricos en el árbol");

        return nodosOperandos.stream().min((n1, n2) -> {
            try {
                double v1 = Double.parseDouble(n1.getValor());
                double v2 = Double.parseDouble(n2.getValor());
                return Double.compare(v1, v2);
            } catch (NumberFormatException e) { return 0; /* Should not happen if filtered by obtenerNodosOperandosNumericos */ }
        }).get();
    }


    public NodoExpresion obtenerNodoMayor() {
        if (raiz == null) throw new java.util.NoSuchElementException("Árbol vacío");
        final java.util.List<NodoExpresion> nodosOperandos = new java.util.ArrayList<>();
        obtenerNodosOperandosNumericos(raiz, nodosOperandos);

        if (nodosOperandos.isEmpty()) throw new java.util.NoSuchElementException("No hay operandos numéricos en el árbol");
        
        return nodosOperandos.stream().max((n1, n2) -> {
            try {
                double v1 = Double.parseDouble(n1.getValor());
                double v2 = Double.parseDouble(n2.getValor());
                return Double.compare(v1, v2);
            } catch (NumberFormatException e) { return 0; }
        }).get();
    }

    private void obtenerNodosOperandosNumericos(NodoExpresion nodo, java.util.List<NodoExpresion> listaNodos) {
        if (nodo == null) return;
        if (!nodo.esOperador()) {
            try {
                Double.parseDouble(nodo.getValor()); // Chequear si es numérico
                listaNodos.add(nodo);
            } catch (NumberFormatException e) { /* No es numérico, no añadir */ }
        }
        obtenerNodosOperandosNumericos(nodo.getIzquierdo(), listaNodos);
        obtenerNodosOperandosNumericos(nodo.getDerecho(), listaNodos);
    }


    public String imprimirAmplitud() {
        if (raiz == null) {
            return "Árbol vacío";
        }
        StringBuilder sb = new StringBuilder();
        java.util.Queue<NodoExpresion> cola = new java.util.LinkedList<>();
        cola.add(raiz);
        while (!cola.isEmpty()) {
            NodoExpresion actual = cola.poll();
            sb.append(actual.getValor()).append(" ");
            if (actual.getIzquierdo() != null) {
                cola.add(actual.getIzquierdo());
            }
            if (actual.getDerecho() != null) {
                cola.add(actual.getDerecho());
            }
        }
        return sb.toString().trim();
    }

    public boolean EliminarDato(String valor) {
        NodoExpresion nodoAEliminar = buscarNodoPorValor(valor);
        if (nodoAEliminar == null) {
            System.out.println("Nodo '" + valor + "' no encontrado para eliminar.");
            return false;
        }

        // Solo se pueden eliminar hojas (nodos sin hijos) en esta implementación simple
        if (nodoAEliminar.getIzquierdo() != null || nodoAEliminar.getDerecho() != null) {
            System.out.println("No se puede eliminar '" + valor + "': solo se pueden eliminar nodos hoja (sin hijos).");
            return false;
        }

        if (nodoAEliminar == raiz) {
            raiz = null;
            System.out.println("Nodo raíz '" + valor + "' eliminado. El árbol está ahora vacío.");
            return true;
        }

        // Buscar el padre del nodo a eliminar
        NodoExpresion padre = encontrarPadre(raiz, nodoAEliminar);
        if (padre != null) {
            if (padre.getIzquierdo() == nodoAEliminar) {
                padre.setIzquierdo(null);
            } else {
                padre.setDerecho(null);
            }
            System.out.println("Nodo '" + valor + "' eliminado.");
            return true;
        }
        // Esto no debería ocurrir si el nodo no es la raíz y fue encontrado.
        System.err.println("Error inesperado al intentar eliminar '" + valor + "': no se encontró el padre.");
        return false; 
    }

    private NodoExpresion encontrarPadre(NodoExpresion actual, NodoExpresion hijo) {
        if (actual == null || hijo == null || actual == hijo) { // hijo no puede ser la raíz si buscamos padre
            return null;
        }
        if (actual.getIzquierdo() == hijo || actual.getDerecho() == hijo) {
            return actual;
        }
        NodoExpresion padreEnIzquierdo = encontrarPadre(actual.getIzquierdo(), hijo);
        if (padreEnIzquierdo != null) {
            return padreEnIzquierdo;
        }
        return encontrarPadre(actual.getDerecho(), hijo);
    }

    public void borrarElArbol() {
        this.raiz = null;
        System.out.println("Árbol de expresiones borrado.");
    }
} 