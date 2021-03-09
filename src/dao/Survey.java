package com.cnpc.bgp.seismiccrew.task;

import java.io.Serializable;

public class Survey implements Serializable {
    private int id = 0;
	private int state = 0;
    private String lineNo;
    private String pointNo;
    private String stakeNo;
    private String Coordinate_X;
    private String Coordinate_Y;
    private String Coordinate_H;
    private String lineNo2;
    private String pointNo2;
    private String Coordinate_X2;
    private String Coordinate_Y2;
    private String operator;
    private String surveyDate;
    private String remark;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public int getState() {
        return state;
    }
    public void setState(int state) {
        this.state = state;
    }

    public String getLineNo() {
        return lineNo;
    }
    public void setLineNo(String lineNo) {
        this.lineNo = lineNo;
    }

    public String getPointNo() {
        return pointNo;
    }
    public void setPointNo(String pointNo) {
        this.pointNo = pointNo;
    }

    public String getStakeNo() {
        return stakeNo;
    }
    public void setStakeNo(String stakeNo) {
        this.stakeNo = stakeNo;
    }

    public String getEastCoord() {
        return Coordinate_X;
    }
    public void setEastCoord(String coord) {
        Coordinate_X = coord;
    }

    public String getNorthCoord() {
        return Coordinate_Y;
    }
    public void setNorthCoord(String coord) {
        Coordinate_Y = coord;
    }

    public String getAltitude() {
        return this.Coordinate_H;
    }
    public void setAltitude(String altitude) {
        this.Coordinate_H = altitude;
    }

    public String getLineNo2() {
        if (lineNo2 == null || lineNo2.isEmpty())
            return lineNo;

        return lineNo2;
    }
    public void setLineNo2(String lineNo) {
        this.lineNo2 = lineNo;
    }

    public String getPointNo2() {
        if (pointNo2 == null || pointNo2.isEmpty())
            return pointNo;

        return pointNo2;
    }
    public void setPointNo2(String pointNo) {
        this.pointNo2 = pointNo;
    }

    public String getEastCoord2() {
        if (Coordinate_X2 == null || Coordinate_X2.isEmpty())
            return Coordinate_X;

        return Coordinate_X2;
    }
    public void setEastCoord2(String coord) {
        Coordinate_X2 = coord;
    }

    public String getNorthCoord2() {
        if (Coordinate_Y2 == null || Coordinate_Y2.isEmpty())
            return Coordinate_Y;

        return Coordinate_Y2;
    }
    public void setNorthCoord2(String coord) {
        Coordinate_Y2 = coord;
    }

    public String getSurveyor() {
        return operator;
    }
    public void setSurveyor(String operator) {
        this.operator = operator;
    }

    public String getDate() {
        return surveyDate;
    }
    public void setDate(String surveyDate) {
        this.surveyDate = surveyDate;
    }

    public String getRemark() {
        return remark;
    }
    public void setRemark(String remark) {
        this.remark = remark;
    }
}