package com.calewiz.security.auth.jwt.extractor;

public interface TokenExtractor {

    String extract(String payload);

}
