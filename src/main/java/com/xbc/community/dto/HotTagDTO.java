package com.xbc.community.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class HotTagDTO implements Comparable, Serializable {
    private String name;
    private Integer priority;
    private Integer questionCount;
    private Integer commentCount;
    private Integer viewCount;

    @Override
    public int compareTo(Object o) {
        return this.getPriority()-((HotTagDTO) o).getPriority();
    }
}
