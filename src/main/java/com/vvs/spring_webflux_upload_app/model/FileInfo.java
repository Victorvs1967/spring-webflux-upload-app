package com.vvs.spring_webflux_upload_app.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FileInfo {
    
    private String name;
    private String url;
}
