package com.sdu.inas.ltp.bean;

import com.google.common.base.MoreObjects;

import javax.naming.Name;

public class Event {

    private String entityName;
    private String ts;
    private String site;
    private String details;
    private String affect;

    public Event(String entityName,String ts, String site, String details, String affect) {
        this.ts = ts;
        this.site = site;
        this.details = details;
        this.affect = affect;
        this.entityName = entityName;
    }

    public Event() {
    }



    public String getTs() {
        return ts;
    }

    public void setTs(String ts) {
        this.ts = ts;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getAffect() {
        return affect;
    }

    public void setAffect(String affect) {
        this.affect = affect;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    @Override
    public String toString() {
        return "("+entityName+","+ts+","+site+","+details+")";
    }

    public java.lang.String packEventLabel(){
        StringBuilder sb = new StringBuilder();
        sb.append("时间 "+ts+"  地点 "+site+"||");
        sb.append("详情 "+details+"||");
        return sb.toString();
    }
}
