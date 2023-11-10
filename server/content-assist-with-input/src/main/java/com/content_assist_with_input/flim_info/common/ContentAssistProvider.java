package com.content_assist_with_input.flim_info.common;

import java.util.List;

public interface ContentAssistProvider {
    String saveNewElement(String element);
    String saveNewElements(List<?> elements);
    List<String> getAvailableSuggestions(String sequence);
}
