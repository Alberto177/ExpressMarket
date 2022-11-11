package com.example.expressmarket.models;

public class ModelShop {
    private String uid, email, name,shopname, phone, gasto, ciudad, estado, direccion, latitud, longitud, tiempo, tipo, online, shopopen,profileimagen;

    public ModelShop(){

    }

    public ModelShop(String uid, String email, String name, String shopname, String phone, String gasto, String ciudad, String estado, String direccion, String latitud, String longitud, String tiempo, String tipo, String online, String shopopen, String profileimagen) {
        this.uid = uid;
        this.email = email;
        this.name = name;
        this.shopname = shopname;
        this.phone = phone;
        this.gasto = gasto;
        this.ciudad = ciudad;
        this.estado = estado;
        this.direccion = direccion;
        this.latitud = latitud;
        this.longitud = longitud;
        this.tiempo = tiempo;
        this.tipo = tipo;
        this.online = online;
        this.shopopen = shopopen;
        this.profileimagen = profileimagen;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShopname() {
        return shopname;
    }

    public void setShopname(String shopname) {
        this.shopname = shopname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getGasto() {
        return gasto;
    }

    public void setGasto(String gasto) {
        this.gasto = gasto;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

    public String getTiempo() {
        return tiempo;
    }

    public void setTiempo(String tiempo) {
        this.tiempo = tiempo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getOnline() {
        return online;
    }

    public void setOnline(String online) {
        this.online = online;
    }

    public String getShopopen() {
        return shopopen;
    }

    public void setShopopen(String shopopen) {
        this.shopopen = shopopen;
    }

    public String getProfileimagen() {
        return profileimagen;
    }

    public void setProfileimagen(String profileimagen) {
        this.profileimagen = profileimagen;
    }
}
