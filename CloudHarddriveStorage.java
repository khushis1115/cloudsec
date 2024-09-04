//package cloudsecurity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
//import cloudsecurity.CloudFile;

import org.cloudbus.cloudsim.File;
import org.cloudbus.cloudsim.HarddriveStorage;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.ParameterException;
import org.cloudbus.cloudsim.distributions.ContinuousDistribution;
import org.cloudbus.cloudsim.util.ExecutionTimeMeasurer;


/**
 * An implementation of a storage system. It simulates the behaviour of a typical harddrive storage.
 * The default values for this storage are those of a Maxtor DiamonMax 10 ATA harddisk with the
 * following parameters:
 * <ul>
 * <li>latency = 4.17 ms
 * <li>avg seek time = 9 ms
 * <li>max transfer rate = 133 MB/sec
 * </ul>
 * 
 * @author Uros Cibej
 * @author Anthony Sulistio
 * @since CloudSim Toolkit 1.0
 */
public class CloudHarddriveStorage extends HarddriveStorage {
	/** a list storing all the files stored on the harddrive. */
	private List<CloudFile> fileList;

	/**
	 * Creates a new harddrive storage with a given name and capacity.
	 * 
	 * @param name the name of the new harddrive storage
	 * @param capacity the capacity in MByte
	 * @throws ParameterException when the name and the capacity are not valid
	 */
	public CloudHarddriveStorage(String name, double capacity) throws ParameterException
	{
		super(name,capacity);
		fileList = new ArrayList<CloudFile>();
	}

	/**
	 * Creates a new harddrive storage with a given capacity. In this case the name of the storage
	 * is a default name.
	 * 
	 * @param capacity the capacity in MByte
	 * @throws ParameterException when the capacity is not valid
	 */
	public CloudHarddriveStorage(double capacity) throws ParameterException 
	{
	  super(capacity);
	  fileList = new ArrayList<CloudFile>();
	}
	public double addCloudFile(CloudFile file) {
		double result = 0.0;
		// check if the file is valid or not
//		if (!isFileValid(file, "addFile()")) {
	//		return result;
		//}

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

	
}
