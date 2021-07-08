package com.dna.plank.Modeles;

public class Historique {
    private long id;
    private String NomEx;
    private String DureeEx;
    private String dureepose;
    private String ratio;

    private String image;


    public Historique(long id, String nomEx, String dureeEx, String dureepose, String ratio, String image) {
        this.id = id;
        NomEx = nomEx;
        DureeEx = dureeEx;
        this.dureepose = dureepose;
        this.ratio = ratio;
        this.image = image;
    }

    public Historique() {

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNomEx() {
        return NomEx;
    }

    public void setNomEx(String nomEx) {
        NomEx = nomEx;
    }

    public String getDureeEx() {
        return DureeEx;
    }

    public void setDureeEx(String dureeEx) {
        DureeEx = dureeEx;
    }

    public String getDureepose() {
        return dureepose;
    }

    public void setDureepose(String dureepose) {
        this.dureepose = dureepose;
    }

    public String getRatio() {
        return ratio;
    }

    public void setRatio(String ratio) {
        this.ratio = ratio;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
