package com.stadela.model;

public class HasilApriori {
    private int    idHasil;
    private int    idProses;
    private String antecedent;
    private String consequent;
    private float  support;
    private float  supportPct;
    private float  confidence;
    private float  confidencePct;
    private float  lift;

    public int    getIdHasil()        { return idHasil; }
    public void   setIdHasil(int v)   { idHasil = v; }
    public int    getIdProses()       { return idProses; }
    public void   setIdProses(int v)  { idProses = v; }
    public String getAntecedent()     { return antecedent; }
    public void   setAntecedent(String v) { antecedent = v; }
    public String getConsequent()     { return consequent; }
    public void   setConsequent(String v) { consequent = v; }
    public float  getSupport()        { return support; }
    public void   setSupport(float v) { support = v; }
    public float  getSupportPct()     { return supportPct; }
    public void   setSupportPct(float v) { supportPct = v; }
    public float  getConfidence()     { return confidence; }
    public void   setConfidence(float v) { confidence = v; }
    public float  getConfidencePct()  { return confidencePct; }
    public void   setConfidencePct(float v) { confidencePct = v; }
    public float  getLift()           { return lift; }
    public void   setLift(float v)    { lift = v; }
}
