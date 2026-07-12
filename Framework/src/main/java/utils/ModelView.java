package framework.utils;

import java.util.HashMap;

public class ModelView {
    String view;
    HashMap<String, Object> data = new HashMap<>();

    public ModelView() {}
    public ModelView(String view) { 
        this.view = view; 
    }

    public void addItem(String key, Object value) {
        this.data.put(key, value);
    }
    
    public String getView(){
        return view;
    }

    public HashMap<String, Object> getData() {
        return data;
    }
}