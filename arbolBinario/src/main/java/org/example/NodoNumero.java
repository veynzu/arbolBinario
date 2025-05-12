package org.example;

// Nodo para árbol binario de números enteros
public class NodoNumero {
    int valor;
    NodoNumero izquierdo;
    NodoNumero derecho;

    public NodoNumero(int valor) {
        this.valor = valor;
        this.izquierdo = null;
        this.derecho = null;
    }

    // Getters
    public int getValor() {
        return valor;
    }

    public NodoNumero getIzquierdo() {
        return izquierdo;
    }

    public NodoNumero getDerecho() {
        return derecho;
    }

    // Setters
    public void setValor(int valor) {
        this.valor = valor;
    }

    public void setIzquierdo(NodoNumero izquierdo) {
        this.izquierdo = izquierdo;
    }

    public void setDerecho(NodoNumero derecho) {
        this.derecho = derecho;
    }

    @Override
    public String toString() {
        return "NodoNumero{valor=" + valor + "}";
    }
} 