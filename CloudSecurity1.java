//package cloudsecurity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.io.File;
import java.io.IOException;
//import cloudsecurity.CloudFile;
//import cloudsecurity.CloudHarddriveStorage;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
//import jdk.nashorn.internal.runtime.regexp.joni.Config;


import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.UtilizationModel;
import org.cloudbus.cloudsim.UtilizationModelFull;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;

public class CloudSecurity1 {

    /** The cloudlet list. */
    private static List<Cloudlet> cloudletList;

    /** The vmlist. */
    private static List<Vm> vmlist;

    private static SecretKeySpec secretKey;
    private static byte[] key;
    public static CloudHarddriveStorage hdst;

    public static CloudHarddriveStorage getStorage()
    {
        return hdst;
    }

    public static void setKey(String myKey) 
    {
        MessageDigest sha = null;
        try {
            key = myKey.getBytes("UTF-8");
            sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16); 
            secretKey = new SecretKeySpec(key, "AES");
            System.out.println(sha.hashCode());
        } 
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } 
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public static String encrypt(String strToEncrypt, String secret) 
    {
        try
        {
            setKey(secret);
            //Log.printLine("Size:  " + key.length + "       "+ secretKey.getAlgorithm().getBytes());
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
        } 
        catch (Exception e) 
        {
            System.out.println("Error while encrypting: " + e.toString());
        }
        return null;
    }

    public static String decrypt(String strToDecrypt, String secret) 
    {
        try
        {
            setKey(secret);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            //Log.printLine("INDecrpt: String for decryption:"+strToDecrypt);
            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
        } 
        catch (Exception e) 
        {
            System.out.println("Error while decrypting: " + e.toString());
        }
        return null;
    }

    public static String createCipherText(String fileName,String key) throws FileNotFoundException, Exception
    {
        FileInputStream inFile = new FileInputStream(fileName);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inFile));
        String line;
        String ciphertext="";
        try {
            while ((line = reader.readLine()) != null) {
                 // Do something with line
                    //Log.printLine("Line="+line);
                    String temp = encrypt(line,key);
                    ciphertext = ciphertext + temp;
                    ciphertext = ciphertext + '$';
                }
            
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //Message.getCipher(ciphertext);
        return ciphertext;
    }

    public static HashMap<String,Integer> buildMap(String fileName,String key) throws IOException{
        HashMap<String,Integer> hm=new HashMap<String,Integer>();
        FileInputStream inFile = new FileInputStream(fileName);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inFile));
        String line = null;
        while( (line = reader.readLine())!= null ){
            // \\s+ means any number of whitespaces between tokens
            String [] tokens = line.split("\\s+");
            for(int i=0;i<tokens.length;i++)
            {
                //Log.printLine("Token =" +tokens[i]);
                String temp = encrypt(tokens[i],key); 
                hm.put(temp, 1);
            }
        }
        return hm;
    }
    public static void bringText(String ciphertext,String key)
    {
        String temp = "";
        for(int i=0;i<ciphertext.length();i++)
        {
            if(ciphertext.charAt(i)!='$')
            {
                temp = temp + ciphertext.charAt(i);
            }
            else
            {
                temp = decrypt(temp,key);
                System.out.println(temp);
                temp = "";
            }
        }
    }

    public static void searchWord(CloudHarddriveStorage hdst,String text)
    {
        List<CloudFile> fileList = hdst.getFileList();
        //List<CloudFile> fileList = (List<CloudFile>) hdst.getCloudFile(text);
        Log.printLine("No. of Files in HD: "+ fileList.size());
        for(int i=0;i<fileList.size();i++)
        {
            String ciphertext=fileList.get(i).getCipherData("cText");
            HashMap<String,Integer> hm = fileList.get(i).getUniqueWords(ciphertext);
            
            String key = fileList.get(i).getCipherData("cKey");
            Log.printLine("cKey="+key);
            String temp = encrypt(text,key);
            Log.printLine("Temp-"+temp);
            Log.printLine("Text="+text);
            if(hm.containsKey(temp))
            {
                ciphertext=fileList.get(i).getCipherData("cText");
                //String ciphertext=fileList.get(i).getCipherData("cText");
                Log.printLine("CText="+ciphertext);
                bringText(ciphertext,key);
            }
            bringText(ciphertext,key);
        }
    }

    /**
     * Creates main() to run this example.
     *
     * @param args the args
     */
    @SuppressWarnings("unused")
    

    public static void main(String[] args) {
    long startTime = System.nanoTime();
        Log.printLine("Starting Cloud Security...");

        try {
            // First step: Initialize the CloudSim package. It should be called
            // before creating any entities.
            int num_user = 1;
            Calendar calendar = Calendar.getInstance();
            boolean trace_flag = false;
            CloudSim.init(num_user, calendar, trace_flag);
            //******************************************************
            //Creating Storages
            //createDatacenter("dc1", hdst);
            hdst = new CloudHarddriveStorage(100);
            double hdstCap = hdst.getCapacity();
            double hdstAvailSpace = hdst.getAvailableSpace();
            Log.printLine("Harddisk Storage created..\n"+"Capacity="+hdstCap+" Available="+hdstAvailSpace);
            
            // First step: Initialize the CloudSim package. It should be called
            // before creating any entities.
            String cipherKey = "akshaykc";
            Log.printLine(cipherKey);
            String ciphertext = createCipherText("akc.pdf",cipherKey);
            Log.printLine(ciphertext);
            HashMap<String,Integer> hm = buildMap("newfile.txt",cipherKey);
            //CloudFile file2 = new CloudFile ("data.txt",2);
            CloudFile file2 = new CloudFile ("newfile.txt",2);
            file2.addCipherData(ciphertext, cipherKey, hm);
            Log.printLine(file2.getName()+ file2.getCipherData(cipherKey)+ file2.getCipherData(ciphertext));

            //FileOutputStream fop = new FileOutputStream(file2);
            CloudSim.startSimulation();

            //Date date = CloudSim.getSimulationCalendar().getTime();
            //Log.printLine(date);
            // org.cloudbus.cloudsim.File file1 = new org.cloudbus.cloudsim.File ("data.cfg",50);
            // Log.printLine(file1.getName());
            //file1.getFileAttribute();
            //int size1 = file1.getSize();
            //Log.printLine("size="+size1);
            double timetaken = hdst.addCloudFile(file2);
            hdstCap = hdst.getCapacity();
            hdstAvailSpace = hdst.getAvailableSpace();
            Log.printLine("Capacity="+hdstCap+" Available="+hdstAvailSpace);
            Log.printLine("Generating the Keys");
            //GenerateKeys.genkeys();
            searchWord(hdst,"hi");
            //VerifyMessage.checkintegrity();
            /*CloudFile fd1 =

                    hdst.getCloudFile("data.txt");
                    ciphertext=fd1.getCipherData("cText");
                    cipherKey=fd1.getCipherData("cKey");
                    bringText(ciphertext,cipherKey,"data.txt");*/
            long endTime   = System.nanoTime();
            long totalTime = endTime - startTime;
            System.out.println("Total time in nano seconds: " + totalTime);
            double seconds = (double)totalTime / 1000000000.0;
            System.out.println("which is " + seconds); 
        } catch (Exception e) {
            e.printStackTrace();
            Log.printLine("Unwanted errors happen");
        }
    }

    /**
     * Creates the datacenter.
     *
     * @param name the name
     * @param hdList 
     *
     * @return the datacenter
     */
    private static Datacenter createDatacenter(String name, LinkedList<Storage> hdList) {

        // Here are the steps needed to create a PowerDatacenter:
        // 1. We need to create a list to store
        // our machine
        List<Host> hostList = new ArrayList<Host>();

        // 2. A Machine contains one or more PEs or CPUs/Cores.
        // In this example, it will have only one core.
        List<Pe> peList = new ArrayList<Pe>();
        int mips = 1000;

        // 3. Create PEs and add these into a list.
        peList.add(new Pe(0, new PeProvisionerSimple(mips))); // need to store Pe id and MIPS Rating

        // 4. Create Host with its id and list of PEs and add them to the list
        // of machines
        int hostId = 0;
        int ram = 2048; // host memory (MB)
        long storage = 1000000; // host storage
        int bw = 10000;

        hostList.add(
            new Host(
                hostId,
                new RamProvisionerSimple(ram),
                new BwProvisionerSimple(bw),
                storage,
                peList,
                new VmSchedulerTimeShared(peList)
            )
        ); // This is our machine

        // 5. Create a DatacenterCharacteristics object that stores the
        // properties of a data center: architecture, OS, list of
        // Machines, allocation policy: time- or space-shared, time zone
        // and its price (G$/Pe time unit).
        String arch = "x86"; // system architecture
        String os = "Linux"; // operating system
        String vmm = "Xen";
        double time_zone = 10.0; // time zone this resource located
        double cost = 3.0; // the cost of using processing in this resource
        double costPerMem = 0.05; // the cost of using memory in this resource
        double costPerStorage = 0.001; // the cost of using storage in this
                                        // resource
        double costPerBw = 0.0; // the cost of using bw in this resource
        LinkedList<Storage> storageList = hdList; // we are not adding SAN
                                                    // devices by now
        DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
                arch, os, vmm, hostList, time_zone, cost, costPerMem,
                costPerStorage, costPerBw);

        // 6. Finally, we need to create a PowerDatacenter object.
        Datacenter datacenter = null;
        try {
            datacenter = new Datacenter(name, characteristics, new VmAllocationPolicySimple(hostList), storageList, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return datacenter;
    }

    // We strongly encourage users to develop their own broker policies, to
    // submit vms and cloudlets according
    // to the specific rules of the simulated scenario
    /**
     * Creates the broker.
     *
     * @return the datacenter broker
     */
    private static DatacenterBroker createBroker() {
        DatacenterBroker broker = null;
        try {
            broker = new DatacenterBroker("Broker");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return broker;
    }

}