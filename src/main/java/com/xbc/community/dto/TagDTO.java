package com.xbc.community.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
@Data
public class TagDTO implements Serializable {
    private String categoryName;
    private List<String> tags;
    private String tag;
}
