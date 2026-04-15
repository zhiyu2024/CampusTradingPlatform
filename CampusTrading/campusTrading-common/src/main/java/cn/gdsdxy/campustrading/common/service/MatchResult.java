package cn.gdsdxy.campustrading.common.service;

import lombok.Data;

@Data
public class MatchResult {
    private int score;
    private String reason;
    private boolean recommend;

    public MatchResult(int score, String reason, boolean recommend) {
        this.score = score;
        this.reason = reason;
        this.recommend = recommend;
    }
}