package org.dbbrowser.ui;

import infrastructure.internationalization.InternationalizationManager;
import infrastructure.propertymanager.PropertyManager;
import infrastructure.logging.Log;
import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import org.dbbrowser.ui.widget.Button;
import org.dbbrowser.ui.panel.ButtonsPanel;
import org.dbbrowser.util.MailException;
import org.dbbrowser.util.MailManager;

/**
 * A window which can be used by the user to enter commnets/feedback.  The user click on send to send the comment/feedback
 */
public class CommentsFeedbackWindow implements ActionListener
{
    private static final String COMMENTS_FEEDBACK_ICON_FILENAME = PropertyManager.getInstance().getProperty("dbbrowser-ui-dbbrowser-window-help-comments-feedback-menu-icon");

    private static final String COMMENTS_FEEDBACK_WINDOW_TITLE = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-dbbrowser-window-help-menu-comments-feedback-label", null);
    private static final String MESSAGE_NOT_SENT_ERROR_MESSAGE = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-comments-feedback-window-message-not-sent-label", null);
    private static final String COMMENTS_NOT_ENTERED_ERROR_MESSAGE = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-comments-feedback-window-comments-is-mandatory-in-comment-feedback-label", null);
    private static final String DESTINATION_ADDRESS_LABEL = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-comments-feedback-window-destination-email-label", null);
    private static final String EMAIL_ADDRESS_LABEL = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-comments-feedback-window-email-address-label", null);
    private static final String SUBJECT_LABEL = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-comments-feedback-window-subject-label", null);
    private static final String COMMENTS_FEEDBACK_LABEL = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-comments-feedback-window-comments-feedback-label", null);
    private static final String SEND_BUTTON_LABEL = InternationalizationManager.getInstance().getMessage( "dbbrowser-ui", "dbbrowser-ui-comments-feedback-window-send-button-label", null);

    private JFrame frame = new JFrame();
    private JTextField textFieldForDestinationEmailAddress = null;
    private JTextField textFieldForEmailAddress = null;
    private JTextField textFieldForSubject = null;
    private JTextArea textAreaForCommentsFeedback = null;

    /**
     * Constructer
     */
    public CommentsFeedbackWindow()
    {
        initialize();
    }

    /**
     * Show the UI
     */
    public void show()
    {
        this.frame.show();
    }

    public void actionPerformed(ActionEvent e)
    {
        //Get the values entered by the user
        String sendersEmailAddress = this.textFieldForEmailAddress.getText();
        String subject = this.textFieldForSubject.getText();
        String comments = this.textAreaForCommentsFeedback.getText();

        //Only comments is mandatory
        if( (comments != null) && (!"".equals( comments )) )
        {
            try
            {
            	Log.getInstance().debugMessage("Trying to send email...", this.getClass().getName());
            	MailManager.sendEmail(sendersEmailAddress, subject, comments);
                Log.getInstance().debugMessage("Message sent", this.getClass().getName());
                this.frame.pack();
                this.frame.dispose();
                this.frame = null;
            }
            catch(MailException exc)
            {
                Log.getInstance().fatalMessage("Message not sent - " + exc.getMessage(), this.getClass().getName());
                JOptionPane.showMessageDialog(null ,MESSAGE_NOT_SENT_ERROR_MESSAGE + " - " + exc.getMessage(), COMMENTS_FEEDBACK_WINDOW_TITLE, JOptionPane.ERROR_MESSAGE);
            }
        }
        else
        {
            JOptionPane.showMessageDialog(null ,COMMENTS_NOT_ENTERED_ERROR_MESSAGE, COMMENTS_FEEDBACK_WINDOW_TITLE, JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initialize()
    {
        this.frame.setTitle(COMMENTS_FEEDBACK_WINDOW_TITLE);
        this.frame.setSize(600, 400);
        this.frame.setLocationRelativeTo( null );
        this.frame.setDefaultCloseOperation( JFrame.HIDE_ON_CLOSE );
        this.frame.setIconImage( (new ImageIcon(COMMENTS_FEEDBACK_ICON_FILENAME)).getImage() );

        //Setup the widgets
        JLabel labelForDestinationEmailAddress = new JLabel(DESTINATION_ADDRESS_LABEL);
        textFieldForDestinationEmailAddress = new JTextField("DBBrowser");
        textFieldForDestinationEmailAddress.setEditable( false );
        JLabel labelForEmailAddress = new JLabel(EMAIL_ADDRESS_LABEL);
        textFieldForEmailAddress = new JTextField();
        JLabel labelForSubject = new JLabel(SUBJECT_LABEL);
        textFieldForSubject = new JTextField();
        textAreaForCommentsFeedback = new JTextArea();

        //Add widgets to the panel
        JPanel p = new JPanel( new GridLayout(3, 2) );
        p.add( labelForDestinationEmailAddress );
        p.add( textFieldForDestinationEmailAddress );
        p.add( labelForEmailAddress );
        p.add( textFieldForEmailAddress );
        p.add( labelForSubject );
        p.add( textFieldForSubject );

        this.frame.getContentPane().setLayout( new BoxLayout(this.frame.getContentPane(), BoxLayout.PAGE_AXIS) );
        this.frame.getContentPane().add( p );

        JPanel panelForComments = new JPanel();
        panelForComments.setPreferredSize( new Dimension(panelForComments.getMinimumSize().width, 600) );
        panelForComments.setLayout( new BoxLayout(panelForComments, BoxLayout.PAGE_AXIS) );
        this.textAreaForCommentsFeedback.setLineWrap( true );
        JScrollPane paneForCommentsFeedback = new JScrollPane( this.textAreaForCommentsFeedback );
        paneForCommentsFeedback.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_ALWAYS );
        panelForComments.add( paneForCommentsFeedback );
        panelForComments.setBorder( BorderFactory.createTitledBorder(COMMENTS_FEEDBACK_LABEL));
        this.frame.getContentPane().add( panelForComments );

        Button buttonToSendMessage = new Button(SEND_BUTTON_LABEL, this, SEND_BUTTON_LABEL, new ImageIcon(COMMENTS_FEEDBACK_ICON_FILENAME), Boolean.TRUE);
        List listOfButtons = new ArrayList();
        listOfButtons.add( buttonToSendMessage );
        ButtonsPanel buttonsPanel = new ButtonsPanel( listOfButtons );
        this.frame.getContentPane().add( buttonsPanel );
    }
}
