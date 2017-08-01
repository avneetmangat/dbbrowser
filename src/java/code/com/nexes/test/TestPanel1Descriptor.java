package com.nexes.test;

import com.nexes.wizard.*;

import java.awt.*;
import javax.swing.*;

import org.dbbrowser.ui.helper.exporthelper.wizard.paneldescriptors.OverviewPanelDescriptor;


public class TestPanel1Descriptor extends WizardPanelDescriptor {
    
    public static final String IDENTIFIER = "INTRODUCTION_PANEL";
    
    public TestPanel1Descriptor() {
        super(IDENTIFIER, new TestPanel1());
    }
    
    public Object getNextPanelDescriptor() {
        return TestPanel2Descriptor.IDENTIFIER;
    }
    
    public Object getBackPanelDescriptor() {
        return OverviewPanelDescriptor.IDENTIFIER;
    }  
    
}
