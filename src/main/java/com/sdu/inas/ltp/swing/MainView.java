package com.sdu.inas.ltp.swing;

import com.sdu.inas.ltp.bean.Event;
import com.sdu.inas.ltp.bean.SyntaxResult;
import com.sdu.inas.ltp.service.LtpService;

import javax.swing.*;
import javax.xml.soap.Text;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.util.List;

public class MainView extends JFrame {

    private JTextArea rta;
    private JTextArea sta;
    private JButton extract;

    public MainView(String title) throws HeadlessException {
        super(title);
        this.setLayout(null);
        this.setSize(850,270);
        rta= new JTextArea();
        rta.setLineWrap(true);
        rta.setWrapStyleWord(true);
        JScrollPane rsp = new JScrollPane(rta);
        rsp.setBounds(13, 10, 350, 200);
        rsp.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        sta= new JTextArea();
        JScrollPane ssp = new JScrollPane(sta);
        ssp.setBounds(465, 10, 350, 200);
        ssp.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        sta.setLineWrap(true);
        sta.setWrapStyleWord(true);

        extract = new JButton("提取");
        extract.setBounds(373,95,80,40);
        extract.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text = rta.getText();
                LtpService ltpService = new LtpService();
                SyntaxResult ltpResult = ltpService.getLtpResult(text);
                try {
                    List<Event> events = ltpResult.dotrans();
                    sta.setText("");
                    for (Event event:events){
                      sta.append(event.toString()+"\r\n");
                    }
                } catch (ParseException e1) {
                    System.out.println("trans error");
                }
                System.out.println(ltpResult);
            }
        });


        this.add(rsp);
        this.add(ssp);
        this.add(extract);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        MainView ltp = new MainView("LTP");
    }
}
