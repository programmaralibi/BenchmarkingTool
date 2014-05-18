import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


/*
 * Parameters : 
 * 1) MinMemory
 * 2) MaxMemory
 * 3) Option1
 * 4) Option2
 * 
 * Option1 :
 *  
 */
public class Benchmark {
	public static void main(String args[]) {
		System.out.println("Running the test for ");
		String minMemory = "-Xms" + args[0] + "M";
		String maxMemory = "-Xmx" + args[1] + "M";
		Integer option1 = Integer.parseInt(args[2]);
		Integer option2 = Integer.parseInt(args[3]);
		File file = new File("Result");
		FileWriter resultFile = null;
		try {
			resultFile = new FileWriter(file, true);
			resultFile.write("******************************************\n");
			resultFile.write("-----------------START-----------------\n");
			resultFile.flush();

		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		List<String> commandList = new ArrayList<String>();
		commandList.add("java");
		commandList.add(minMemory);
		commandList.add(maxMemory);
		commandList.add("-jar");
		commandList.add("dacapo.jar");
		
		switch (option1) {
			case 1 : commandList.add("h2"); break;
			case 2 : commandList.add("h2"); break;
		}
		String[] commandArray = new String[commandList.size()];
		
		try {
			for(int numberOfRuns=0; numberOfRuns<option2; numberOfRuns++) {
				Long startTime = System.currentTimeMillis();
				Process process;
				ProcessBuilder pBuilder = new ProcessBuilder(commandList.toArray(commandArray));
				//pBuilder.redirectOutput(file);
				process = pBuilder.start();
				InputStream stream = process.getInputStream();
				int c;
				while ((c = stream.read()) != -1) {
					resultFile.write(c);
				}
				stream = process.getErrorStream();
				while ((c = stream.read()) != -1) {
					resultFile.write(c);
				}

				process.waitFor();
				resultFile.write("\n Time taken for executing : " + (System.currentTimeMillis() - startTime) + "command :---\n"+commandList.toString() );
			}
			resultFile.write("\n-----------------END----------------- \n\n\n\n");
			resultFile.flush();
			resultFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
