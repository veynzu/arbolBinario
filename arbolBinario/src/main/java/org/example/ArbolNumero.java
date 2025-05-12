package org.example;

import java.util.*;

// Árbol binario para almacenar solo números enteros
public class ArbolNumero {

    NodoNumero raiz;

    public ArbolNumero() {
        this.raiz = null;
    }

    // 1. estaVacio
    public boolean estaVacio() {
        return raiz == null;
    }

    // 2. Agregar dato (Inserción por nivel)
    public void AgregarDato(int valor) {
        if (raiz == null) {
            raiz = new NodoNumero(valor);
            System.out.println("Dato " + valor + " agregado como raíz.");
            return;
        }
        Queue<NodoNumero> cola = new LinkedList<>();
        cola.add(raiz);
        while (!cola.isEmpty()) {
            NodoNumero actual = cola.poll();
            if (actual.izquierdo == null) {
                actual.izquierdo = new NodoNumero(valor);
                System.out.println("Dato " + valor + " agregado como hijo izquierdo de " + actual.valor);
                return;
            } else {
                cola.add(actual.izquierdo);
            }
            if (actual.derecho == null) {
                actual.derecho = new NodoNumero(valor);
                System.out.println("Dato " + valor + " agregado como hijo derecho de " + actual.valor);
                return;
            } else {
                cola.add(actual.derecho);
            }
        }
    }

    // 3. Recorrer árbol (Inorden, Postorden, Preorden)
    // Renombrados a R mayúscula y devuelven String formateado con StringBuilder
    public String RecorrerInorden() {
        StringBuilder resultado = new StringBuilder();
        inordenRecursivo(raiz, resultado);
        return resultado.toString().trim();
    }
    public String RecorrerPreorden() {
        StringBuilder resultado = new StringBuilder();
        preordenRecursivo(raiz, resultado);
        return resultado.toString().trim();
    }
    public String RecorrerPostorden() {
        StringBuilder resultado = new StringBuilder();
        postordenRecursivo(raiz, resultado);
        return resultado.toString().trim();
    }

    // Modificados para usar StringBuilder y añadir espacio después de cada número
    private void inordenRecursivo(NodoNumero nodo, StringBuilder resultado) {
        if (nodo != null) {
            inordenRecursivo(nodo.izquierdo, resultado);
            resultado.append(nodo.valor).append(" "); // Añade espacio
            inordenRecursivo(nodo.derecho, resultado);
        }
    }
    private void preordenRecursivo(NodoNumero nodo, StringBuilder resultado) {
        if (nodo != null) {
            resultado.append(nodo.valor).append(" "); // Añade espacio
            preordenRecursivo(nodo.izquierdo, resultado);
            preordenRecursivo(nodo.derecho, resultado);
        }
    }
    private void postordenRecursivo(NodoNumero nodo, StringBuilder resultado) {
        if (nodo != null) {
            postordenRecursivo(nodo.izquierdo, resultado);
            postordenRecursivo(nodo.derecho, resultado);
            resultado.append(nodo.valor).append(" "); // Añade espacio
        }
    }

    // 4. existe dato
    public boolean existeDato(int valor) {
        return existeDatoRec(raiz, valor);
    }
    private boolean existeDatoRec(NodoNumero nodo, int valor) {
        if (nodo == null) return false;
        if (nodo.valor == valor) return true;
        return existeDatoRec(nodo.izquierdo, valor) || existeDatoRec(nodo.derecho, valor);
    }

    // 5. obtenerPeso (número de nodos)
    public int obtenerPeso() {
        return contarNodos(raiz);
    }
    private int contarNodos(NodoNumero nodo) {
        if (nodo == null) return 0;
        return 1 + contarNodos(nodo.izquierdo) + contarNodos(nodo.derecho);
    }

    // 6. obtenerAltura
    public int obtenerAltura() {
        return alturaRec(raiz);
    }
    private int alturaRec(NodoNumero nodo) {
        if (nodo == null) return 0;
        return 1 + Math.max(alturaRec(nodo.izquierdo), alturaRec(nodo.derecho));
    }

    // 7. obtenerNivel (nivel de un nodo con valor dado, raíz es nivel 1)
    public int obtenerNivel(int valor) {
        return obtenerNivelRec(raiz, valor, 1);
    }
    private int obtenerNivelRec(NodoNumero nodo, int valor, int nivel) {
        if (nodo == null) return 0;
        if (nodo.valor == valor) return nivel;
        int izq = obtenerNivelRec(nodo.izquierdo, valor, nivel + 1);
        if (izq != 0) return izq;
        return obtenerNivelRec(nodo.derecho, valor, nivel + 1);
    }

    // 8. contarHojas
    public int contarHojas() {
        return contarHojasRec(raiz);
    }
    private int contarHojasRec(NodoNumero nodo) {
        if (nodo == null) return 0;
        if (nodo.izquierdo == null && nodo.derecho == null) return 1;
        return contarHojasRec(nodo.izquierdo) + contarHojasRec(nodo.derecho);
    }

    // 9. obtenerMenor (valor mínimo)
    public int obtenerMenor() {
        if (raiz == null) throw new NoSuchElementException("Árbol vacío");
        int min = raiz.valor;
        Queue<NodoNumero> cola = new LinkedList<>();
        cola.add(raiz);
        while (!cola.isEmpty()) {
            NodoNumero actual = cola.poll();
            if (actual.valor < min) min = actual.valor;
            if (actual.izquierdo != null) cola.add(actual.izquierdo);
            if (actual.derecho != null) cola.add(actual.derecho);
        }
        return min;
    }

    // 10. imprimirAmplitud (recorrido por niveles)
    // Modificado para devolver String usando StringBuilder y trim()
    public String imprimirAmplitud() {
        if (raiz == null) {
            return "[]"; // O "Árbol vacío"
        }
        Queue<NodoNumero> cola = new LinkedList<>();
        cola.add(raiz);
        StringBuilder resultado = new StringBuilder();
        while (!cola.isEmpty()) {
            NodoNumero actual = cola.poll();
            resultado.append(actual.valor).append(" "); // Añade espacio
            if (actual.izquierdo != null) cola.add(actual.izquierdo);
            if (actual.derecho != null) cola.add(actual.derecho);
        }
        return resultado.toString().trim(); // Usa trim() al final
    }

    // 11. Eliminar dato
    public boolean EliminarDato(int valor) {
        if (raiz == null) return false;
        NodoNumero dummy = new NodoNumero(0);
        dummy.izquierdo = raiz;
        boolean eliminado = eliminarRec(dummy, raiz, valor);
        raiz = dummy.izquierdo;
        return eliminado;
    }
    private boolean eliminarRec(NodoNumero padre, NodoNumero nodo, int valor) {
        if (nodo == null) return false;
        if (nodo.valor == valor) {
            // Caso hoja
            if (nodo.izquierdo == null && nodo.derecho == null) {
                if (padre.izquierdo == nodo) padre.izquierdo = null;
                else padre.derecho = null;
                return true;
            }
            // Caso un hijo
            if (nodo.izquierdo == null || nodo.derecho == null) {
                NodoNumero hijo = (nodo.izquierdo != null) ? nodo.izquierdo : nodo.derecho;
                if (padre.izquierdo == nodo) padre.izquierdo = hijo;
                else padre.derecho = hijo;
                return true;
            }
            // Caso dos hijos: reemplazar por el menor de la derecha
            NodoNumero sucesorPadre = nodo;
            NodoNumero sucesor = nodo.derecho;
            while (sucesor.izquierdo != null) {
                sucesorPadre = sucesor;
                sucesor = sucesor.izquierdo;
            }
            nodo.valor = sucesor.valor;
            if (sucesorPadre.izquierdo == sucesor) sucesorPadre.izquierdo = sucesor.derecho;
            else sucesorPadre.derecho = sucesor.derecho;
            return true;
        }
        return eliminarRec(nodo, nodo.izquierdo, valor) || eliminarRec(nodo, nodo.derecho, valor);
    }

    // 12. obtenerNodoMayor (devuelve el valor máximo)
    public int obtenerNodoMayor() {
        if (raiz == null) throw new NoSuchElementException("Árbol vacío");
        int max = raiz.valor;
        Queue<NodoNumero> cola = new LinkedList<>();
        cola.add(raiz);
        while (!cola.isEmpty()) {
            NodoNumero actual = cola.poll();
            if (actual.valor > max) max = actual.valor;
            if (actual.izquierdo != null) cola.add(actual.izquierdo);
            if (actual.derecho != null) cola.add(actual.derecho);
        }
        return max;
    }

    // 13. obtenerNodoMenor (igual a obtenerMenor)
    public int obtenerNodoMenor() {
        return obtenerMenor();
    }

    // 14. borrar el arbol
    public void borrarElArbol() {
        raiz = null;
    }

    // Método para insertar un nodo como hijo de un padre específico
    public boolean insertarComoHijo(NodoNumero padre, int valorNuevo, boolean esIzquierdo) {
        if (padre == null) {
            System.err.println("Error: El nodo padre no puede ser nulo para la inserción de hijo.");
            // Podríamos optar por insertar como raíz si el árbol está vacío y el padre es nulo
            if (this.raiz == null) {
                this.raiz = new NodoNumero(valorNuevo);
                System.out.println("Nodo " + valorNuevo + " insertado como raíz (padre era nulo y árbol vacío).");
                return true;
            }
            return false;
        }

        NodoNumero nuevoNodo = new NodoNumero(valorNuevo);
        if (esIzquierdo) {
            if (padre.izquierdo != null) {
                System.out.println("Advertencia: Sobrescribiendo hijo izquierdo existente de " + padre.valor + ".");
            }
            padre.izquierdo = nuevoNodo;
            System.out.println("Nodo " + valorNuevo + " insertado como hijo izquierdo de " + padre.valor + ".");
        } else {
            if (padre.derecho != null) {
                System.out.println("Advertencia: Sobrescribiendo hijo derecho existente de " + padre.valor + ".");
            }
            padre.derecho = nuevoNodo;
            System.out.println("Nodo " + valorNuevo + " insertado como hijo derecho de " + padre.valor + ".");
        }
        return true;
    }

    // --- Métodos auxiliares para la GUI ---
    public NodoNumero getRaiz() {
        return raiz;
    }
} 