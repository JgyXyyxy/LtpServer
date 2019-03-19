package com.sdu.inas.ltp.service;


import com.sdu.inas.ltp.bean.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LtpService {


    private final String basicUrl = "http://192.168.11.210:8020/ltp";

    private final String defaultParam = "t=all&x=n";

    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url   发送请求的 URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     */
    public static String sendPost(String url, String param) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
                System.out.println();
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！" + e);
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        System.out.println(result);
        return result;
    }

    public SyntaxResult getLtpResult(String text) {
        String para = "s=" + text + "&" + defaultParam;
        String result = sendPost(basicUrl, para);

        ArrayList<Word> words = new ArrayList<>();
        ArrayList<Word> verbs = new ArrayList<>();
        ArrayList<Integer> ns = new ArrayList<>();
        ArrayList<Integer> nl = new ArrayList<>();
        ArrayList<Integer> nh = new ArrayList<>();
        ArrayList<Integer> nt = new ArrayList<>();

        String replace = result.replace(" ", "");
        System.out.println(replace);
        String s = replace.substring(2, replace.length() - 2);
        System.out.println(s);
//        String s = result.substring(5, result.length() - 2);
        JSONArray array = JSONArray.fromObject(s);
        for (int j = 0; j < array.size(); j++) {
            JSONObject object = array.getJSONObject(j);
            Word word = new Word();
            word.setCont(object.getString("cont"));
            word.setId(object.getInt("id"));
            word.setRelate(object.getString("relate"));
            word.setPos(object.getString("pos"));
            word.setParent(object.getInt("parent"));
            if ("ns".equals(word.getPos())) {
                ns.add(word.getId());
            }
            if ("nl".equals(word.getPos())) {
                nl.add(word.getId());
            }
            if ("nh".equals(word.getPos())) {
                nh.add(word.getId());
            }
            if ("nt".equals(word.getPos())) {
                nt.add(word.getId());
            }
            word.setNe(object.getString("ne"));
            words.add(word);
            JSONArray args = object.getJSONArray("arg");
            if (args.size()!= 0){
                Word verb = parseVerb(word, object);
                verbs.add(verb);
            }
        }

        SyntaxResult syntaxResult = new SyntaxResult();
        syntaxResult.setNh(nh);
        syntaxResult.setNl(nl);
        syntaxResult.setNs(ns);
        syntaxResult.setNt(nt);
        syntaxResult.setVerbs(verbs);
        syntaxResult.setWords(words);

        return syntaxResult;
    }

    public Word parseVerb(Word word, JSONObject object) {
        JSONArray args = object.getJSONArray("arg");
        ArrayList<Arg> argsList = new ArrayList<>();
        for (int i = 0; i < args.size(); i++) {
            JSONObject o = args.getJSONObject(i);
            Arg arg = new Arg();
            arg.setId(o.getInt("id"));
            arg.setBegin(o.getInt("beg"));
            arg.setEnd(o.getInt("end"));
            arg.setType(o.getString("type"));
            argsList.add(arg);
        }
        word.setArgs(argsList);
        return word;
    }


    public static void main(String[] args) {
        LtpService ltpService = new LtpService();
        SyntaxResult syntaxResult = ltpService.getLtpResult("1840年7月5日，英国炮轰中国定海县城，第一次鸦片战争爆发。");
        try {
            syntaxResult.verbs2Event();
            PreFeatures preFeatures = new PreFeatures(syntaxResult);
            preFeatures.preParse();
            System.out.println(preFeatures);
            List<Event> coreEvent = syntaxResult.getCoreEvent();
            System.out.println("core: ");
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
                System.out.println(event);
            }
            List<Event> subEvent = syntaxResult.getSubEvent();
            System.out.println("sub:");
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
                System.out.println(event);
            }


        } catch (ParseException e) {
            e.printStackTrace();
        }
        System.out.println("end");
    }
}
