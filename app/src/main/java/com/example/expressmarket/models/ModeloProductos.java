package com.example.expressmarket.models;

public class ModeloProductos {

    private String productId, tituloProducto, descripcionProducto, categoriaProducto, cantidadProducto, imagenProducto, precioOriginal,
            precioDescuento, notaDescuento, descuentoDisponible, timestamp, uid;

    public ModeloProductos() {

    }

    public ModeloProductos(String productId, String tituloProducto, String descripcionProducto, String categoriaProducto,
                           String cantidadProducto, String imagenProducto, String precioOriginal, String precioDescuento, String notaDescuento,
                           String descuentoDisponible, String timestamp, String uid) {
        this.productId = productId;
        this.tituloProducto = tituloProducto;
        this.descripcionProducto = descripcionProducto;
        this.categoriaProducto = categoriaProducto;
        this.cantidadProducto = cantidadProducto;
        this.imagenProducto = imagenProducto;
        this.precioOriginal = precioOriginal;
        this.precioDescuento = precioDescuento;
        this.notaDescuento = notaDescuento;
        this.descuentoDisponible = descuentoDisponible;
        this.timestamp = timestamp;
        this.uid = uid;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getTituloProducto() {
        return tituloProducto;
    }

    public void setTituloProducto(String tituloProducto) {
        this.tituloProducto = tituloProducto;
    }

    public String getDescripcionProducto() {
        return descripcionProducto;
    }

    public void setDescripcionProducto(String descripcionProducto) {
        this.descripcionProducto = descripcionProducto;
    }

    public String getCategoriaProducto() {
        return categoriaProducto;
    }

    public void setCategoriaProducto(String categoriaProducto) {
        this.categoriaProducto = categoriaProducto;
    }

    public String getCantidadProducto() {
        return cantidadProducto;
    }

    public void setCantidadProducto(String cantidadProducto) {
        this.cantidadProducto = cantidadProducto;
    }

    public String getImagenProducto() {
        return imagenProducto;
    }

    public void setImagenProducto(String imagenProducto) {
        this.imagenProducto = imagenProducto;
    }

    public String getPrecioOriginal() {
        return precioOriginal;
    }

    public void setPrecioOriginal(String precioOriginal) {
        this.precioOriginal = precioOriginal;
    }

    public String getPrecioDescuento() {
        return precioDescuento;
    }

    public void setPrecioDescuento(String precioDescuento) {
        this.precioDescuento = precioDescuento;
    }

    public String getNotaDescuento() {
        return notaDescuento;
    }

    public void setNotaDescuento(String notaDescuento) {
        this.notaDescuento = notaDescuento;
    }

    public String getDescuentoDisponible() {
        return descuentoDisponible;
    }

    public void setDescuentoDisponible(String descuentoDisponible) {
        this.descuentoDisponible = descuentoDisponible;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
