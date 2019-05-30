package mycontroller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
/**
 * Output class is responsible to generate a file which contains result.
 * @author yuqiangz@student.unimelb.edu.au
 *
 */
public class Output {
	private String fileName;
	private FileWriter fileWritter;
	
	public Output(String fileName) {
		this.fileName = fileName;
		File file = new File(fileName);
		try {
			file = new File(fileName);

			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			/** replace the origin content
			fileWritter = new FileWriter(file.getName(), false);
			*/			
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Error in opening " + fileName);
			System.exit(1);
		}
	}
	
	/**
	 * write data into given file.
	 * @param data It'd better end with "\n"
	 */
	public void write(String data) {
		try {
			// continue write data into file
			fileWritter = new FileWriter(fileName, true);
			fileWritter.write(data);
			fileWritter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("Error in writing data.\n");
			System.exit(1);
		}
	}
	
	public void endOutput() {
		try {
			fileWritter.close();
			System.out.println("Successfully get a new " + fileName + ".");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("Error in closing file.\n");
			System.exit(1);
		}
	}
}