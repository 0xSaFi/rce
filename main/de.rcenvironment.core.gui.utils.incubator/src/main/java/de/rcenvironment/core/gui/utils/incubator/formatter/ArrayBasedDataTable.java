/*
 * Copyright (C) 2006-2017 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */

package de.rcenvironment.core.gui.utils.incubator.formatter;

import java.util.ArrayList;
import java.util.List;

/**
 * Renders a given table. You need to set an Alignment first, otherwise, it will aligned on the left hand-side by default.
 *
 * @author Adrian Stock
 */
public class ArrayBasedDataTable implements DataTable {

    private static final String BEFORE = "before";

    private static final String AFTER = "after";

    protected List<Integer> maxAmountOfSpaceBeforeAlignmentChar = new ArrayList<>();

    protected List<Integer> maxAmountOfSpaceAfterAlignmentChar = new ArrayList<>();

    protected List<Integer> maxAmountOfSpace = new ArrayList<>();

    private List<String[]> data = new ArrayList<>();

    private Alignments[] alignments;

    private char alignmentCharacter = '.';

    @Override
    public int getSizeOfTable() {
        return data.size();
    }

    @Override
    public int getSizeOfRow() {
        return data.get(0).length;
    }

    @Override
    public String[] getRow(int index) {
        return data.get(index);
    }

    @Override
    public List<Integer> getAmountOfSpace(String place) {
        if (BEFORE.equals(place)) {
            return maxAmountOfSpaceBeforeAlignmentChar;
        } else if (AFTER.equals(place)) {
            return maxAmountOfSpaceAfterAlignmentChar;
        } else {
            return maxAmountOfSpace;
        }
    }

    @Override
    public Alignments[] getAlignments() {
        return alignments;
    }

    @Override
    public char getAlignmentCharacter() {
        return alignmentCharacter;
    }

    @Override
    public void setAlignment(Alignments[] newAlignments) {
        alignments = newAlignments;
    }

    public void setAlignmentCharacter(char newCharacter) {
        alignmentCharacter = newCharacter;
    }

    /**
     * @param row is added to the table.
     */
    public void addRow(String[] row) {
        data.add(row);
    }

    /**
     * @param row is added to the table but will not be aligned.
     */
    public void addRow(String row) {
        String[] newRow = { row };
        addRow(newRow);
    }
}
