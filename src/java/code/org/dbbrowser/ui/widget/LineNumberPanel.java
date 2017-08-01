/*
 * LineNumberPanel2.java
 *
 * Created on pirmdiena, 2005, 12 decembris, 15:49
 * Created by Andrejs Grave (agrave@inbox.lv)
 *
 */

package org.dbbrowser.ui.widget;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;

/**
 *
 * @author andrgrav
 */
public class LineNumberPanel extends JPanel implements DocumentListener {
    private final static Color DEFAULT_BACKGROUND = new Color(224, 224, 224);
    private final static int MARGIN = 5;
    private final static int INITIAL_LINE_COUNT = 15;

    private JTextComponent theTextComponent;
    private FontMetrics theFontMetrics;
    private int currentRowWidth;

    
    public LineNumberPanel(JTextComponent aTextComponent) {
        theTextComponent = aTextComponent;
        theTextComponent.getDocument().addDocumentListener(this);
        setOpaque(true);
        setBackground(DEFAULT_BACKGROUND);
        setFont(theTextComponent.getFont());
        theFontMetrics = getFontMetrics(getFont());
        setForeground(theTextComponent.getForeground());
        currentRowWidth = getDesiredRowWidth();
    }
    
    private void update() {
        int desiredRowWidth = getDesiredRowWidth();
        if (desiredRowWidth != currentRowWidth) {
            currentRowWidth = desiredRowWidth;
            revalidate();
        }
        repaint();
    }
    
    private int getRowHeight(){
        return (theFontMetrics.getHeight() + theFontMetrics.getDescent());
    }
    
   
    
    private int getDesiredRowWidth() {
        Document doc = theTextComponent.getDocument();
        int length = doc.getLength();
        Element map = doc.getDefaultRootElement();
        int nbLines = map.getElementIndex(length) + INITIAL_LINE_COUNT;
        return theFontMetrics.stringWidth(Integer.toString(nbLines));
    }
    
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        try {
            Document doc = theTextComponent.getDocument();
            int length = doc.getLength();
            Element map = doc.getDefaultRootElement();
            int startLine = map.getElementIndex(0);
            int endline = map.getElementIndex(length);
            endline = (endline > INITIAL_LINE_COUNT) ? endline : INITIAL_LINE_COUNT;
            int y = theTextComponent.modelToView(map.getElement(startLine).getStartOffset()).y + theFontMetrics.getHeight() - theFontMetrics.getDescent();
            
            for (int line = startLine; line <= endline; line++) {
                    String s = Integer.toString(line + 1);
                    int width = theFontMetrics.stringWidth(s);
                    g.drawString(s, MARGIN + currentRowWidth - width, y);
                    y += theFontMetrics.getHeight();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public Dimension getPreferredSize() {
        return new Dimension(2 * MARGIN + currentRowWidth, theTextComponent.getHeight());
    }
    
    public void insertUpdate(DocumentEvent e) {
        update();
    }
    
    public void removeUpdate(DocumentEvent e) {
        update();
    }
    
    public void changedUpdate(DocumentEvent e) {
        update();
    }
}



