/* ASK WHAT MESSAGE IS ?
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//package cloudsecurity;

/**
 *
 * @author Mahe
 */
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.io.File;
import java.io.IOException;
//import cloudsecurity.CloudFile;
//import cloudsecurity.CloudHarddriveStorage;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Date;
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




public class CloudSecurity {

    private static List<Cloudlet>cloudletlist;
    private static List<Vm>vmlist;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException {
        // TODO code application logic here
        Log.printLine("Starting Cloud...");
        
        try{
        CloudHarddriveStorage hdst = new CloudHarddriveStorage(1024);
        
        
        double hdstCap = hdst.getCapacity();
        double hdstAvailSpace = hdst.getAvailableSpace();
        Log.printLine("Capacity="+hdstCap+" Available="+hdstAvailSpace);
         }catch(Exception e){
              Log.printLine("Hard disk Error");
              }
// First step: Initialize the CloudSim package. It should be called

// before creating any entities.

        int num_user = 1; // number of cloud users
        Calendar calendar = Calendar.getInstance();
        boolean trace_flag = false; // mean trace events
        String ciphertext="ABC";
        String cipherKey= "123";
// Initialize the CloudSim library
         CloudSim.init(num_user, calendar, trace_flag);
         try {
             File yourFile = new File("newfile.txt");
             yourFile.createNewFile(); // if file already exists will do nothing 
             FileOutputStream oFile = new FileOutputStream(yourFile, false); 
             
             try{
             CloudFile file2 = new CloudFile("newfile1.txt",50);
             file2.addCipherData(ciphertext, cipherKey);
             Log.printLine(file2.getName() + file2.getSize() + file2.getCipherData(cipherKey));
             
             CloudHarddriveStorage hdst = new CloudHarddriveStorage(1024);
             double timetaken = hdst.addCloudFile(file2);
             double hdstCap = hdst.getCapacity();
             double hdstAvailSpace = hdst.getAvailableSpace();
             Log.printLine("Capacity="+hdstCap+" Available="+hdstAvailSpace);
              }catch(Exception e)
              {
                  Log.printLine("Error in the File maybe");
              }
            }catch (IOException e) {
	      e.printStackTrace();
     
//              Config config = new Config("./data/encrypt/data.cfg","read");
//ciphertext = config.getProperty("CipherText");
//cipherKey = config.getProperty("CipherKey");
//config.closeFile("read");
            

//FileOutputStream fop = new FileOutputStream(file2);

     CloudSim.startSimulation();
     Date date = CloudSim.getSimulationCalendar().getTime();
     Log.printLine(date);

// org.cloudbus.cloudsim.File file1 = new org.cloudbus.cloudsim.File ("data.cfg",50);

// Log.printLine(file1.getName());

//file1.getFileAttribute();

//int size1 = file1.getSize();

//Log.printLine("size="+size1); 
//Data retrieval
//CloudFile fd1 = hdst.getCloudFile("newfile.txt");
//ciphertext=fd1.getCipherData("cText");
//cipherKey=fd1.getCipherData("cKey");
    }
    }
}
//        try{
//            int num_users=1;
//            Calendar calendar = Calendar.getInstance();
//	    boolean trace_flag = false;
//            CloudSim.init(num_users, calendar, trace_flag);
//            
//            Datacenter datacenter0 = createdatacenter("Datacenter_0");
//            DatacenterBroker broker = createbroker();
//	    int brokerId = broker.getId();
//            vmlist = new ArrayList<Vm>();
//
//			// VM description
//			int vmid = 0;
//			int mips = 1000;
//			long size = 10000; // image size (MB)
//			int ram = 512; // vm memory (MB)
//			long bw = 1000;
//			int pesNumber = 1; // number of cpus
//			String vmm = "Xen"; // VMM name
//
//			// create VM
//			Vm vm = new Vm(vmid, brokerId, mips, pesNumber, ram, bw, size, vmm, new CloudletSchedulerTimeShared());
//
//			// add the VM to the vmList
//			vmlist.add(vm);
//                        
//                        cloudletlist = new ArrayList<Cloudlet>();
//                        int id = 0;
//			long length = 400000;
//			long fileSize = 300;
//			long outputSize = 300;
//			UtilizationModel utilizationModel = new UtilizationModelFull();
//                        Cloudlet cloudlet = new Cloudlet(id, length, pesNumber, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
//			cloudlet.setUserId(brokerId);
//			cloudlet.setVmId(vmid);
//            
//                        cloudletlist.add(cloudlet);
//                        
//                        broker.submitCloudletList(cloudletlist);
//                        CloudSim.startSimulation();
//                        
//                        CloudSim.stopSimulation();
//                        List<Cloudlet> newList = broker.getCloudletReceivedList();
//			printCloudletList(newList);
//
//			Log.printLine("Cloud execution finished!");
//        }catch (Exception e) {
//			e.printStackTrace();
//			Log.printLine("Unwanted errors happen");
//        }
//    }
//    
//    private static Datacenter createdatacenter(String name){
//        List<Host> hostList = new ArrayList<Host>();
//        List<Pe> peList = new ArrayList<Pe>();
//        int mips = 1000;
//        peList.add(new Pe(0, new PeProvisionerSimple(mips)));
//        int hostId = 0;
//		int ram = 2048; // host memory (MB)
//		long storage = 1000000; // host storage
//		int bw = 10000;
//
//		hostList.add(
//			new Host(
//				hostId,
//				new RamProvisionerSimple(ram),
//				new BwProvisionerSimple(bw),
//				storage,
//				peList,
//				new VmSchedulerTimeShared(peList)
//			)
//		);
//                String arch = "x86"; // system architecture
//		String os = "Linux"; // operating system
//		String vmm = "Xen";
//		double time_zone = 10.0; // time zone this resource located
//		double cost = 3.0; // the cost of using processing in this resource
//		double costPerMem = 0.05; // the cost of using memory in this resource
//		double costPerStorage = 0.001; // the cost of using storage in this
//										// resource
//		double costPerBw = 0.0; // the cost of using bw in this resource
//		LinkedList<Storage> storageList = new LinkedList<Storage>(); 
//                
//                DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
//				arch, os, vmm, hostList, time_zone, cost, costPerMem,
//				costPerStorage, costPerBw);
//                Datacenter datacenter = null;
//		try {
//			datacenter = new Datacenter(name, characteristics, new VmAllocationPolicySimple(hostList), storageList, 0);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		return datacenter;
//    }
//    
//    private static DatacenterBroker createbroker() {
//		DatacenterBroker broker = null;
//		try {
//			broker = new DatacenterBroker("Broker");
//		} catch (Exception e) {
//			e.printStackTrace();
//			return null;
//		}
//		return broker;
//	}
//    
//    private static void printCloudletList(List<Cloudlet> list) {
//		int size = list.size();
//		Cloudlet cloudlet;
//
//		String indent = "    ";
//		Log.printLine();
//		Log.printLine("========== OUTPUT ==========");
//		Log.printLine("Cloudlet ID" + indent + "STATUS" + indent
//				+ "Data center ID" + indent + "VM ID" + indent + "Time" + indent
//				+ "Start Time" + indent + "Finish Time");
//
//		DecimalFormat dft = new DecimalFormat("###.##");
//		for (int i = 0; i < size; i++) {
//			cloudlet = list.get(i);
//			Log.print(indent + cloudlet.getCloudletId() + indent + indent);
//
//			if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS) {
//				Log.print("SUCCESS");
//
//				Log.printLine(indent + indent + cloudlet.getResourceId()
//						+ indent + indent + indent + cloudlet.getVmId()
//						+ indent + indent
//						+ dft.format(cloudlet.getActualCPUTime()) + indent
//						+ indent + dft.format(cloudlet.getExecStartTime())
//						+ indent + indent
//						+ dft.format(cloudlet.getFinishTime()));
//			}
//		}
//	}


