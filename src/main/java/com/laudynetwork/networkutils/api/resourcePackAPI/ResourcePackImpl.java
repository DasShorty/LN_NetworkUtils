package com.laudynetwork.networkutils.api.resourcePackAPI;

public class ResourcePackImpl implements ResourcePackInterface{
    @Override
    public String toFind() {
        return null;
    }

    @Override
    public Character get(String name){
        return items.get(name.toUpperCase());
    }

    @Override
    public String get(Character iconChar){
        for (String elm : items.keySet()){
            if (items.get(elm).equals(iconChar)) return elm;
        }
        return null;
    }

    @Override
    public String parseName(String input){
        return input.replace(toFind(), "").replace("}", "");
    }
}
