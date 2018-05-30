package com.luqi.service.search;

/**
 * Created by luqi
 * 2018-05-30 22:05.
 */
public class HouseSuggest {

    private String input; // 用户输入的词汇
    private int weight = 10; // 默认权重

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}
