package com.lsh.service;

import java.io.IOException;
import java.util.Map;

public interface SearchService {
    Map<String, Object> search(String q) throws IOException;
}
