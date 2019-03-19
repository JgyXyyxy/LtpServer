package com.sdu.inas.ltp.bean;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Data
public class PreFeatures {
    private String preLoc;
    private String preObj;
    private String preTs;
    private SyntaxResult syntaxResult;

    public PreFeatures(SyntaxResult syntaxResult) {
        this.syntaxResult = syntaxResult;
    }


    public void preParse() {
        HashSet<String> tsFea = new HashSet<>();
        HashSet<String> locFea = new HashSet<>();
        HashSet<String> objFea = new HashSet<>();

        if (syntaxResult.getCoreEvent().size() != 0) {
            fillFeatureSet(syntaxResult.getCoreEvent(), tsFea, locFea, objFea);
        }
        if (syntaxResult.getSubEvent().size() != 0) {
            fillFeatureSet(syntaxResult.getSubEvent(), tsFea, locFea, objFea);
        }

        preObj = getCandidate(syntaxResult.getNh(), tsFea, locFea, syntaxResult.getWords());
        preTs = getCandidate(syntaxResult.getNt(), objFea, locFea, syntaxResult.getWords());
        preLoc = getCandidate(syntaxResult.getNs(), tsFea, objFea, syntaxResult.getWords());
        if ("".equals(preLoc)) {
            preLoc = getCandidate(syntaxResult.getNl(), tsFea, objFea, syntaxResult.getWords());
        }

    }

    private void fillFeatureSet(List<Event> events, HashSet<String> tsFea, HashSet<String> locFea, HashSet<String> objFea) {

        for (Event event : events) {
            String ts = event.getTs();
            if ((!"".equals(ts)) && (!tsFea.contains(ts))) {
                tsFea.add(ts);
            }
            String obj = event.getEntityName();
            if ((!"".equals(obj)) && (!objFea.contains(obj))) {
                objFea.add(obj);
            }
            String loc = event.getSite();
            if ((!"".equals(loc)) && (!locFea.contains(loc))) {
                locFea.add(loc);
            }
        }
    }

    private String getCandidate(List<Integer> list, HashSet<String> feature1, HashSet<String> feature2, List<Word> words) {
        HashSet<String> tmp = new HashSet<>();
        tmp.addAll(feature1);
        tmp.addAll(feature2);
        ArrayList<Integer> candidate = new ArrayList<>();
        ArrayList<String> canList = new ArrayList<>();
        StringBuilder can = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            can.setLength(0);
            candidate.clear();
            Integer st = list.get(i);
            candidate.add(st);
            while (((i + 1) < list.size()) && (list.get(i + 1) == st + 1)) {
                candidate.add(st + 1);
                st = st+1;
                i++;
            }
            if (candidate.size() != 0) {
                for (Integer k : candidate) {
                    can.append(words.get(k).getCont());
                }
                canList.add(can.toString());
            }
            if (!tmp.contains(can.toString())) {
                return can.toString();
            }
        }

        if (canList.size() == 0) {
            return "";
        }
        return canList.get(0);
    }
}
