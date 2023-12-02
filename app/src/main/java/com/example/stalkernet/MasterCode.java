package com.example.stalkernet;

import java.util.HashMap;

public class MasterCode {

    public MasterCode(){

    }


    public String textConstructor(String input){
        String output = "";
        String[] splitInput = input.split(", ");
        for (int i = 0; i < splitInput.length; i++){
            if (splitInput[i].equals("sc2")){
                output += sc2Constructor(new String[]{splitInput[i+1], splitInput[i+2]});
                i += 3;
            }else if (splitInput[i].equals("sc1")){
                output += sc1Constructor(new String[]{splitInput[i+1], splitInput[i+2], splitInput[i+3]});
                i += 4;
            }
            if (splitInput.length - i > 2){
                output += "\n";
            }
        }
        return output;
    }

    private String sc2Constructor(String[] input){
        String output = input[0];
        if (Double.parseDouble(input[1]) > 0){
            output += " +" + input[1] + "%;";
        } else {
            output += " " + input[1] + "%;";
        }
        return output;
    }

    private String sc1Constructor(String[] input){
        setMcMap();
        return "защита от " + mcMap.get(input[0])+ "\n" + mcMap.get(input[1]) + input[2] + "%;";
    }

    private HashMap<String, String> mcMap = new HashMap<>();
    private void setMcMap(){
        mcMap.put("rad", "РАД ");
        mcMap.put("bio", "БИО ");
        mcMap.put("psy", "ПСИ ");
        mcMap.put("suit", " костюмная ");
        mcMap.put("art", " артефактная ");
        mcMap.put("quest", " квестовая ");
    }
}
