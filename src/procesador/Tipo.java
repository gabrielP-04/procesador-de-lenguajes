package procesador;

import java.util.ArrayList;
import java.util.List;

public class Tipo {
    private String tipo;
    private int ancho;
    private List<Tipo> producto = new ArrayList<>();

    Tipo(String tipo) {
        if (esTipoValido(tipo)) {
            this.tipo = tipo;

        } else {
            throw new IllegalArgumentException("Tipo no válido");
        }

    }

    Tipo(String tipo, Tipo producto) {
        if (tipo.equals("producto")) {
            this.tipo = tipo;

        } else {
            throw new IllegalArgumentException("Tipo no válido");
        }
        this.producto.add(producto);
    }

    Tipo(String tipo, int ancho) {
        if (tipo.equals("entero") || tipo.equals("boolean") || tipo.equals("cadena")) {
            this.tipo = tipo;

        } else {
            throw new IllegalArgumentException("Tipo no válido");
        }
        this.ancho = ancho;
    }

    public int getAncho() {
        return ancho;
    }

    public String getTipo() {
        return tipo;
    }

    public List<Tipo> getProducto() {
        return producto;
    }

    public String toString(){
        return tipo;
    }

    private boolean esTipoValido(String tipo) {
        return tipo.equals("entero") || tipo.equals("boolean") ||
                tipo.equals("cadena") || tipo.equals("vacio") ||
                tipo.equals("funcion") || tipo.equals("producto") ||
                tipo.equals("tipoOk") || tipo.equals("tipoError");
    }

    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (obj == null || (!(obj instanceof Tipo) && !(obj instanceof String)))
            return false;

        Tipo tipo = (Tipo) obj;
        if (tipo.getTipo().equals(this.tipo)) {
            if (tipo.getTipo().equals("producto")) {
                if (tipo.producto.isEmpty()) {
                    return true;
                } else {
                    return equalsProducto(tipo.producto);
                }
            }
            return true;

        } else {
            return false;
        }
    }

    private boolean equalsProducto(List<Tipo> otroProducto) {
        boolean esIgual = otroProducto.size() == producto.size();
        for (int i = 0; i < otroProducto.size() && esIgual; i++) {
            esIgual = otroProducto.get(i).equals(producto.get(i));
        }

        return esIgual;
    }

    public void añadirProducto(Tipo factor) {
        if (factor.getTipo().equals("producto")) {
            for (Tipo t : factor.producto) {
                this.producto.add(t);
            }
        } else {
            producto.add(factor);
        }
    }

}
