package com.sdu.inas.ltp.swing;

import com.sdu.inas.ltp.bean.Event;
import com.sdu.inas.ltp.bean.PreFeatures;
import com.sdu.inas.ltp.bean.SyntaxResult;
import com.sdu.inas.ltp.service.LtpService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class MainView extends JFrame {

    private JTextArea rta;
    private JTextArea sta;
    private JButton extract;
    private List result = new ArrayList();

    public MainView(String title) throws HeadlessException {
        super(title);
        this.setLayout(null);
        this.setSize(850, 270);
        rta = new JTextArea();
        rta.setLineWrap(true);
        rta.setWrapStyleWord(true);
        JScrollPane rsp = new JScrollPane(rta);
        rsp.setBounds(13, 10, 350, 200);
        rsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        rta.setFont(new Font("宋体", Font.BOLD, 14));

        sta = new JTextArea();
        JScrollPane ssp = new JScrollPane(sta);
        ssp.setBounds(465, 10, 350, 200);
        ssp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        sta.setLineWrap(true);
        sta.setWrapStyleWord(true);
        sta.setFont(new Font("宋体", Font.BOLD, 14));

        extract = new JButton("提取");
        extract.setBounds(373, 95, 80, 40);
        extract.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text = rta.getText();
                String[] split = text.split("[。？！]");
                sta.setText("");
                for (String s : split) {
                    LtpService ltpService = new LtpService();
                    SyntaxResult syntaxResult = ltpService.getLtpResult(s);
                    try {
                        syntaxResult.verbs2Event();
                        PreFeatures preFeatures = new PreFeatures(syntaxResult);
                        preFeatures.preParse();
                        System.out.println(preFeatures);
                        List<Event> coreEvent = syntaxResult.getCoreEvent();
                        sta.append("核心事件: "+ "\r\n");
                        for (int i =0;i<coreEvent.size();i++){
                            Event event = coreEvent.get(i);
                            if ("".equals(event.getEntityName())){
                                event.setEntityName(preFeatures.getPreObj());
                            }else {
                                preFeatures.setPreObj(event.getEntityName());
                            }
                            if ("".equals(event.getSite())){
                                event.setSite(preFeatures.getPreLoc());
                            }else {
                                preFeatures.setPreLoc(event.getSite());
                            }
                            if ("".equals(event.getTs())){
                                event.setTs(preFeatures.getPreTs());
                            }else {
                                preFeatures.setPreTs(event.getTs());
                            }
                            sta.append(event.toString()+ "\r\n");
                        }
                        List<Event> subEvent = syntaxResult.getSubEvent();
                        if (subEvent.size()!=0){
                            sta.append("子事件列表:"+ "\r\n");
                            for (int i =0;i<subEvent.size();i++){
                                Event event = subEvent.get(i);
                                if ("".equals(event.getEntityName())){
                                    event.setEntityName(preFeatures.getPreObj());
                                }
                                if ("".equals(event.getSite())){
                                    event.setSite(preFeatures.getPreLoc());
                                }
                                if ("".equals(event.getTs())){
                                    event.setTs(preFeatures.getPreTs());
                                }
                                sta.append(event.toString()+ "\r\n");
                            }
                        }

                    } catch (ParseException ex) {
                        ex.printStackTrace();
                    }
                    sta.append("-------------------------------------"+"\r\n");
                }


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
