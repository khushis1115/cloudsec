

//package cloudsecurity;

import java.util.HashMap;
import org.cloudbus.cloudsim.File;
import org.cloudbus.cloudsim.ParameterException;


public class CloudFile extends org.cloudbus.cloudsim.File {
private String cloudCipherText;
private String cloudCipherKey;
	public CloudFile(File file) throws ParameterException 
	{
		super(file);
	}
	public CloudFile(String fileName, int fileSize) throws ParameterException 
	{
	  super(fileName,fileSize);	
		
	}
        public void addCipherData(String cText, String cKey, HashMap<String,Integer> hm)
        {
          cloudCipherText=cText;
	  cloudCipherKey=cKey;  
        }
	public void addCipherData(String cText,String cKey)
	{
		cloudCipherText=cText;
		cloudCipherKey=cKey;
	}
	public String getCipherData(String cType)
	{
		String result="";
		if (cType=="cText")
		{
		  result=cloudCipherText;
		}
		if (cType=="cKey")
		{
		  result=cloudCipherKey;
		}

		return result;
	}	

    HashMap<String, Integer> getUniqueWords(String s) throws UnsupportedOperationException {
//      String s = "Enter any text.";
      String[] splitted = s.split(" ");
      HashMap hm = new HashMap();
      int x;
        for (int i = 0; i < splitted.length; i++) {
            if (!hm.containsKey(splitted[i])) {
                hm.put(splitted[i], 1);
            } else {
                hm.put(splitted[i], (Integer) hm.get(splitted[i]) + 1);
            }
        }
        for (Object word : hm.keySet()){
            System.out.println(word + " " + (Integer) hm.get(word));
            
        }
          return hm;
       //throw new UnsupportedOperationException("HM:Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
