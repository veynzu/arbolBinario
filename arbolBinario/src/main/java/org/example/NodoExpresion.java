package org.example;

// Renombrado de Nodo a NodoExpresion
public class NodoExpresion {
    String valor;
    boolean esOperador;
    NodoExpresion izquierdo; // Referencia actualizada
    NodoExpresion derecho;  // Referencia actualizada

    // Constructor para inicializar el nodo
    public NodoExpresion(String valor) { // Nombre actualizado
        this.valor = valor.trim();
        this.esOperador = esOperadorValido(this.valor);
        this.izquierdo = null;
        this.derecho = null;
    }

    // Método auxiliar para verificar si es un operador básico
    private boolean esOperadorValido(String val) {
        return val.equals("+") || val.equals("-") || val.equals("*") || val.equals("/");
    }

    // Getters 
    public String getValor() {
        return valor;
    }

    public boolean esOperador() {
        return esOperador;
    }

    public NodoExpresion getIzquierdo() { // Tipo actualizado
        return izquierdo;
    }

    public NodoExpresion getDerecho() { // Tipo actualizado
        return derecho;
    }

    // Setters (podríamos querer validar aquí también)
    public void setValor(String valor) {
        this.valor = valor.trim();
        this.esOperador = esOperadorValido(this.valor);
    }

    public void setIzquierdo(NodoExpresion izquierdo) { // Tipo actualizado
        this.izquierdo = izquierdo;
    }

    public void setDerecho(NodoExpresion derecho) { // Tipo actualizado
        this.derecho = derecho;
    }

    // Método toString útil para depuración
    @Override
    public String toString() {
        // Nombre de clase actualizado
        return "NodoExpresion{valor='" + valor + "', esOperador=" + esOperador + "}";
    }
} 