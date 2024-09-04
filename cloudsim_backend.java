import java.text.DecimalFormat;
//import java.nio.file.Files;
import java.util.*;
import java.io.File;
import java.io.IOException;


import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.ParameterException;
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
import org.cloudbus.cloudsim.HarddriveStorage;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.NetworkTopology;
import org.cloudbus.cloudsim.ParameterException;
import org.cloudbus.cloudsim.distributions.ContinuousDistribution;
import org.cloudbus.cloudsim.util.ExecutionTimeMeasurer;


/**
 * A simple example showing how to create
 * a datacenter with two hosts and run two
 * cloudlets on it. The cloudlets run in
 * VMs with different MIPS requirements.
 * The cloudlets will take different time
 * to complete the execution depending on
 * the requested VM performance.
 */
public class cloudsim_backend extends HarddriveStorage {    
    private Map<Vm, cloudsim_backend> vmHardDriveMap;
   
    
    public cloudsim_backend(String name, double capacity) throws ParameterException {
        super(name, capacity);
        vmHardDriveMap = new HashMap<>();
        fileList = new ArrayList<CloudFile>();
        fileList2 = new ArrayList<CloudFile>();
        fileList3 = new ArrayList<CloudFile>();
    }
    /**
	 * Creates a new harddrive storage with a given name and capacity.
	 * 
	 * @param name the name of the new harddrive storage
	 * @param capacity the capacity in MByte
	 * @throws ParameterException when the name and the capacity are not valid
	 */

    public void linkVmToHardDrive(Vm vm, cloudsim_backend hardDrive) {
        vmHardDriveMap.put(vm, hardDrive);
    }

    public cloudsim_backend getHardDriveForVm(Vm vm) {
        return vmHardDriveMap.get(vm);
    }
    /** a list storing all the files stored on the harddrive. */
	private List<CloudFile> fileList;
        private List<CloudFile> fileList2;
        private List<CloudFile> fileList3;

	public double addCloudFile(CloudFile file) {
		double result = 0.0;
		
		// check the capacity
		if (file.getSize() + super.getCurrentSize() > super.getCapacity()) {
			Log.printLine(super.getName() + ".addFile(): Warning - not enough space" + " to store " + file.getName());
			return result;
		}

		// check if the same file name is alredy taken
		if (!contains(file.getName())) {
			double seekTime = getSeekTime(file.getSize());
			double transferTime = getTransferTime(file.getSize());

			fileList.add(file);               // add the file into the HD
			//nameList.add(file.getName());     // add the name to the name list
			Log.printLine("File added to cloud");
			//currentSize += file.getSize();    // increment the current HD size
			result = seekTime + transferTime;  // add total time
		}
                Log.printLine("Transaction time:" + result);
		file.setTransactionTime(result);
		return result;
	}

	private double getSeekTime(int fileSize) {
		double result = 0;
		if (fileSize > 0 && super.getCapacity() != 0) {
			result += (fileSize / super.getCapacity());
		}

		return result;
	}
	/**
	 * Gets the transfer time of a given file.
	 * 
	 * @param fileSize the size of the transferred file
	 * @return the transfer time in seconds
	 */
	private double getTransferTime(int fileSize) {
		double result = 0;
		if (fileSize > 0 && super.getCapacity() != 0) {
			result = (fileSize * super.getMaxTransferRate()) / super.getCapacity();
		}

		return result;
	}

	public CloudFile getCloudFile(String fileName) {
		// check first whether file name is valid or not
		CloudFile obj = null;
		if (fileName == null || fileName.length() == 0) {
			Log.printLine(super.getName() + ".getFile(): Warning - invalid " + "file name.");
			return obj;
		}

		Iterator<CloudFile> it = fileList.iterator();
		int size = 0;
		int index = 0;
		boolean found = false;
		CloudFile tempFile = null;

		// find the file in the disk
		while (it.hasNext()) {
			tempFile = it.next();
			size += tempFile.getSize();
			if (tempFile.getName().equals(fileName)) {
				found = true;
				obj = tempFile;
				break;
			}

			index++;
		}

		// if the file is found, then determine the time taken to get it
		if (found) {
			obj = fileList.get(index);
			double seekTime = getSeekTime(size);
			double transferTime = getTransferTime(obj.getSize());

			// total time for this operation
			obj.setTransactionTime(seekTime + transferTime);
		}
                Log.printLine("Transaction time:" + obj.getTransactionTime());
		return obj;
	}

    public List<CloudFile> getFileList() {
        Log.printLine("Inside of FileList:");
        return fileList;
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }     
    
	/** The cloudlet list. */
	private static List<Cloudlet> cloudletList;

	/** The vmlist. */
	private static List<Vm> vmlist;

	/**
	 * Creates main() to run this example
	 */
	public static void main(String[] args) {

		Log.printLine("Starting CloudSimExample3...");

		try {
			// First step: Initialize the CloudSim package. It should be called
			// before creating any entities.
			int num_user = 3;   // number of cloud users
			Calendar calendar = Calendar.getInstance();
			boolean trace_flag = false;  // mean trace events

			// Initialize the CloudSim library
			CloudSim.init(num_user, calendar, trace_flag);

			// Second step: Create Datacenters
			//Datacenters are the resource providers in CloudSim. We need atleast one of them to run a CloudSim simulation
			@SuppressWarnings("unused")
			Datacenter datacenter0 = createDatacenter("Datacenter_0");

			//Third step: Create Broker
			DatacenterBroker broker = createBroker();
			int brokerId = broker.getId();

			//Fourth step: Create one virtual machine
			vmlist = new ArrayList<Vm>();

			//VM description
			int vmid = 0;
			int mips = 250;
			long size = 10000; //image size (MB)
			int ram = 2048; //vm memory (MB)
			long bw = 1000;
			int pesNumber = 1; //number of cpus
			String vmm = "Xen"; //VMM name

			//create two VMs
			Vm vm1 = new Vm(vmid, brokerId, mips, pesNumber, ram, bw, size, vmm, new CloudletSchedulerTimeShared());

			//the second VM will have twice the priority of VM1 and so will receive twice CPU time
			vmid++;
			Vm vm2 = new Vm(vmid, brokerId, mips * 2, pesNumber, ram, bw, size, vmm, new CloudletSchedulerTimeShared());
                        
                        //third VM
                        vmid++;
			Vm vm3 = new Vm(vmid, brokerId, mips * 3, pesNumber, ram, bw, size, vmm, new CloudletSchedulerTimeShared());

			//add the VMs to the vmList
			vmlist.add(vm1);
			vmlist.add(vm2);
                        vmlist.add(vm3);

			//submit vm list to the broker
			broker.submitVmList(vmlist);


			//Fifth step: Create two Cloudlets
			cloudletList = new ArrayList<Cloudlet>();

			//Cloudlet properties
			int id = 0;
			long length = 40000;
			long fileSize = 300;
			long outputSize = 300;
			UtilizationModel utilizationModel = new UtilizationModelFull();

			Cloudlet cloudlet1 = new Cloudlet(id, length, pesNumber, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
			cloudlet1.setUserId(brokerId);

			id++;
			Cloudlet cloudlet2 = new Cloudlet(id, length, pesNumber, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
			cloudlet2.setUserId(brokerId);
                        
                        id++;
			Cloudlet cloudlet3 = new Cloudlet(id, length, pesNumber, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
			cloudlet3.setUserId(brokerId);

			//add the cloudlets to the list
			cloudletList.add(cloudlet1);
			cloudletList.add(cloudlet2);
                        cloudletList.add(cloudlet3);

			//submit cloudlet list to the broker
			broker.submitCloudletList(cloudletList);


			//bind the cloudlets to the vms. This way, the broker
			// will submit the bound cloudlets only to the specific VM
			broker.bindCloudletToVm(cloudlet1.getCloudletId(),vm1.getId());
			broker.bindCloudletToVm(cloudlet2.getCloudletId(),vm2.getId());
                        broker.bindCloudletToVm(cloudlet3.getCloudletId(),vm3.getId());
                        
                          // Create the hard drive storage
                        cloudsim_backend hardDrive1 = new cloudsim_backend("HardDrive1", 10000);
                        cloudsim_backend hardDrive2 = new cloudsim_backend("HardDrive2", 10000);
                        cloudsim_backend hardDrive3 = new cloudsim_backend("HardDrive3", 10000);// Adjust capacity as needed

                       hardDrive1.linkVmToHardDrive(vm1, hardDrive1);
                       hardDrive2.linkVmToHardDrive(vm2, hardDrive2);
                       hardDrive3.linkVmToHardDrive(vm3, hardDrive3);
                        // Link the hard drive to the VMs
                        
                        //Sixth step: configure network
			//load the network topology file
			NetworkTopology.buildNetworkTopology("topology1.brite");

			//maps CloudSim entities to BRITE entities
			//Datacenter0 will correspond to BRITE node 0
			int briteNode=0;
			NetworkTopology.mapNode(vm1.getId(),briteNode);

			//Datacenter1 will correspond to BRITE node 2
			briteNode=1;
			NetworkTopology.mapNode(vm2.getId(),briteNode);

			//Broker1 will correspond to BRITE node 3
			briteNode=2;
			NetworkTopology.mapNode(vm3.getId(),briteNode);

			// Sixth step: Starts the simulation
			CloudSim.startSimulation();
                        
                        // Assuming the file hello.txt is stored on your desktop

                        // Step 1: Read the file from your local machine
                        String filePath = "C:\\Users\\amits\\OneDrive\\Desktop\\hello.txt";  // Update the path accordingly
                        File helloFile = new File(filePath);

                        // Step 2: Upload the file to the hard disk linked to vm1
                        CloudFile cloudFile = new CloudFile(helloFile.getName(), (int) helloFile.length());  // Create a CloudFile object
                        boolean uploadSuccessful = hardDrive1.uploadFile(cloudFile,hardDrive1);  // Assuming hardDrive1 is linked to vm1

                        if (uploadSuccessful) {
                            // Step 3: Transfer the file from vm1 to vm2
                            boolean transferToVM2Successful = hardDrive1.transferFile(cloudFile.getName(), hardDrive1, hardDrive2);  // Assuming hardDrive2 is linked to vm2

                            if (transferToVM2Successful) {
                                // Step 4: Transfer the file from vm2 to vm3
                                boolean transferToVM3Successful = hardDrive2.transferFile(cloudFile.getName(), hardDrive2, hardDrive3);  // Assuming hardDrive3 is linked to vm3

                                if (transferToVM3Successful) {
                                    System.out.println("File transferred successfully to vm3.");
                                } else {
                                    System.out.println("Failed to transfer file to vm3.");
                                }
                            } else {
                                System.out.println("Failed to transfer file to vm2.");
                            }
                        } else {
                            System.out.println("Failed to upload file to vm1.");
                        }


			// Final step: Print results when simulation is over
			List<Cloudlet> newList = broker.getCloudletReceivedList();
                        
                        Log.print("=============> User "+broker.getId()+"    ");
			printCloudletList(newList);
                        
                       for (Cloudlet cloudlet : newList) 
                        {
                            double fileTransferTime = cloudlet.getCloudletFileSize() / vm1.getBw();
                             Log.printLine("File transfer time for Cloudlet " + cloudlet.getCloudletId() + ": " + fileTransferTime + " seconds");
                        } 
                        
                      
                       
			CloudSim.stopSimulation();

        	printCloudletList(newList);

			Log.printLine("CloudSimExample3 finished!");
		}
		catch (Exception e) {
			e.printStackTrace();
			Log.printLine("The simulation has been terminated due to an unexpected error");
		}
	}

	private static Datacenter createDatacenter(String name){

		// Here are the steps needed to create a PowerDatacenter:
		// 1. We need to create a list to store
		//    our machine
		List<Host> hostList = new ArrayList<Host>();

		// 2. A Machine contains one or more PEs or CPUs/Cores.
		// In this example, it will have only one core.
		List<Pe> peList = new ArrayList<Pe>();

		int mips = 1000;

		// 3. Create PEs and add these into a list.
		peList.add(new Pe(0, new PeProvisionerSimple(mips))); // need to store Pe id and MIPS Rating

		//4. Create Hosts with its id and list of PEs and add them to the list of machines
		int hostId=0;
		int ram = 2048; //host memory (MB)
		long storage = 1000000; //host storage
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
    		); // This is our first machine

		//create another machine in the Data center
		List<Pe> peList2 = new ArrayList<Pe>();

		peList2.add(new Pe(0, new PeProvisionerSimple(mips)));

		hostId++;

		hostList.add(
    			new Host(
    				hostId,
    				new RamProvisionerSimple(ram),
    				new BwProvisionerSimple(bw),
    				storage,
    				peList2,
    				new VmSchedulerTimeShared(peList2)
    			)
    		); // This is our second machine
                
                //create another machine in the Data center
		List<Pe> peList3 = new ArrayList<Pe>();

		peList3.add(new Pe(0, new PeProvisionerSimple(mips)));

		hostId++;

		hostList.add(
    			new Host(
    				hostId,
    				new RamProvisionerSimple(ram),
    				new BwProvisionerSimple(bw),
    				storage,
    				peList3,
    				new VmSchedulerTimeShared(peList3)
    			)
    		); // This is our third machine



		// 5. Create a DatacenterCharacteristics object that stores the
		//    properties of a data center: architecture, OS, list of
		//    Machines, allocation policy: time- or space-shared, time zone
		//    and its price (G$/Pe time unit).
		String arch = "x86";      // system architecture
		String os = "Linux";          // operating system
		String vmm = "Xen";
		double time_zone = 10.0;         // time zone this resource located
		double cost = 3.0;              // the cost of using processing in this resource
		double costPerMem = 0.05;		// the cost of using memory in this resource
		double costPerStorage = 0.001;	// the cost of using storage in this resource
		double costPerBw = 0.0;			// the cost of using bw in this resource
		LinkedList<Storage> storageList = new LinkedList<Storage>();	//we are not adding SAN devices by now

        DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
                arch, os, vmm, hostList, time_zone, cost, costPerMem, costPerStorage, costPerBw);

		// 6. Finally, we need to create a PowerDatacenter object.
		Datacenter datacenter = null;
		try {
			datacenter = new Datacenter(name, characteristics, new VmAllocationPolicySimple(hostList), storageList, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return datacenter;
	}

	// develop your own broker policies, to submit vms and cloudlets according
	//to the specific rules of the simulated scenario
	private static DatacenterBroker createBroker(){

		DatacenterBroker broker = null;
		try {
			broker = new DatacenterBroker("Broker");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return broker;
	}

	/**
	 * Prints the Cloudlet objects
	 * @param list  list of Cloudlets
	 */
	private static void printCloudletList(List<Cloudlet> list) {
		int size = list.size();
		Cloudlet cloudlet;

		String indent = "    ";
		Log.printLine();
		Log.printLine("========== OUTPUT ==========");
		Log.printLine("Cloudlet ID" + indent + "STATUS" + indent +
				"Data center ID" + indent + "VM ID" + indent + "Time" + indent + "Start Time" + indent + "Finish Time");

		DecimalFormat dft = new DecimalFormat("###.##");
		for (int i = 0; i < size; i++) {
			cloudlet = list.get(i);
			Log.print(indent + cloudlet.getCloudletId() + indent + indent);

			if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS){
				Log.print("SUCCESS");

				Log.printLine( indent + indent + cloudlet.getResourceId() + indent + indent + indent + cloudlet.getVmId() +
						indent + indent + dft.format(cloudlet.getActualCPUTime()) + indent + indent + dft.format(cloudlet.getExecStartTime())+
						indent + indent + dft.format(cloudlet.getFinishTime()));
			}
		}

	}
        
        
public boolean uploadFile(CloudFile file, cloudsim_backend destinationHardDrive) {
    // Check if there's enough space on the destination hard disk
    if (file.getSize() + destinationHardDrive.getCurrentSize() > destinationHardDrive.getCapacity()) {
        Log.printLine(destinationHardDrive.getName() + ": Not enough space to store the file.");
        return false;
    }

    // Perform the upload
    double transactionTime = addCloudFile(file, destinationHardDrive);

    // Optionally, update file metadata
    //file.setHardDrive(destinationHardDrive);
    file.setTransactionTime(transactionTime);

    Log.printLine("File " + file.getName() + " uploaded to " + destinationHardDrive.getName());
    return true;
}

private double addCloudFile(CloudFile file, cloudsim_backend destinationHardDrive) {
    double result = 0.0;

    // check the capacity
    if (file.getSize() + destinationHardDrive.getCurrentSize() > destinationHardDrive.getCapacity()) {
        Log.printLine(destinationHardDrive.getName() + ".addFile(): Warning - not enough space to store " + file.getName());
        return result;
    }

    // check if the same file name is already taken
    if (!destinationHardDrive.contains(file.getName())) {
        double seekTime = destinationHardDrive.getSeekTime(file.getSize());
        double transferTime = destinationHardDrive.getTransferTime(file.getSize());

        destinationHardDrive.addFile(file); // add the file into the HD
        result = seekTime + transferTime; // add total time
    }

    Log.printLine("Transaction time:" + result);
    file.setTransactionTime(result);
    return result;
}


public boolean transferFile(String fileName, cloudsim_backend sourceHardDrive, cloudsim_backend destinationHardDrive) {
    CloudFile file = sourceHardDrive.getCloudFile(fileName);
    if (file == null) {
        Log.printLine("File not found on source hard disk.");
        return false;
    }

    // Check if there's enough space on the destination hard disk
    if (file.getSize() + destinationHardDrive.getCurrentSize() > destinationHardDrive.getCapacity()) {
        Log.printLine("Destination hard disk does not have enough space to store the file.");
        return false;
    }

    // Perform the file transfer
    destinationHardDrive.addCloudFile(file);

    // Optionally, update file metadata to reflect the new location
    //file.setHardDrive(destinationHardDrive);
    file.setTransactionTime(sourceHardDrive.getTransferTime(file.getSize()) + destinationHardDrive.getTransferTime(file.getSize()));

    Log.printLine("File " + fileName + " transferred from " + sourceHardDrive.getName() + " to " + destinationHardDrive.getName());
    return true;
}

public void setHardDriveForFile(String fileName, cloudsim_backend hardDrive) {
        for (CloudFile file : fileList) {
            if (file.getName().equals(fileName)) {
                //file.setHardDrive(hardDrive);
                Log.printLine("Hard drive for file " + fileName + " set to " + hardDrive.getName());
                return;
            }
        }
        Log.printLine("File " + fileName + " not found.");
    }


}