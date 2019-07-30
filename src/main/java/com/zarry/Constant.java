package com.zarry;

public class Constant {
    public static String getParentPath(Class clazz){
      return clazz.getProtectionDomain().getCodeSource().getLocation().getFile()
              .replace("/tcwfservice-1.0.0.jar","")
              .replace("/classes","")
              .replace("/target","").substring(1);
    }

}
