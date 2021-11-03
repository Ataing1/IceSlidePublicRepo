package com.andrew.game;

import java.util.ArrayList;

public class MazeJson {


    public ArrayList<StringPoint> blockCoords;
    public StringPoint startPoint;
    public StringPoint endPoint;
    public StringPoint mazeSize;
    public int minSteps;
    public float netJump;


    public MazeJson(StringPoint startPoint, StringPoint endPoint, StringPoint mazeSize, ArrayList<StringPoint> blockCoords, int minSteps, float netJump) {

        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.mazeSize = mazeSize;
        this.blockCoords = blockCoords;
        this.minSteps = minSteps;
        this.netJump = netJump;

    }

    public MazeJson() {

        blockCoords = new ArrayList<>();

    }


    public void setStartPoint(StringPoint point) {
        startPoint = point;
    }

    public void setEndPoint(StringPoint point) {
        endPoint = point;
    }

    public void setMazeSizePoint(StringPoint point) {
        mazeSize = point;
    }

    public void setMinSteps(int steps) {
        minSteps = steps;
    }

    public void setBlockCoords(ArrayList<StringPoint> blockCoords) {
        this.blockCoords = blockCoords;
    }

    public void setNetJump(float netJump) {
        this.netJump = netJump;
    }

    public void addBlockCoord(StringPoint point) {
        blockCoords.add(point);
    }

    public StringPoint getStartPoint() {
        return startPoint;
    }

    public StringPoint getEndPoint() {
        return endPoint;
    }

    public StringPoint getMazeSize() {
        return mazeSize;
    }

    public int getMinSteps() {
        return minSteps;
    }

    public float getNetJump() {
        return netJump;
    }

    public ArrayList<StringPoint> getBlockCoords() {
        return blockCoords;
    }

}
