import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;


/*
 * Parameters : 
 * 1) Param0 : MinMemory
 * 2) Param1 : MaxMemory
 * 3) Param2 : BenchmarkName
 * 		   1 : h2
 * 		   2 : h2
 * 4) Param3 : Number Of executions
 * 5) Param4 : Size of benchmark
 * 		   1 : small
 * 		   2 : large
 * 	 default : medium  
 *  
 */
public class Benchmark {
	
	static PrintWriter resultFile = null;
	static String JAVOLUTION_DACAPO = "dacapo.jar"; 
	static String JAVA_DACAPO = "dacapo_original.jar"; 

	public static void main(String args[]) {
		System.out.println("Running the test for ");
		String minMemory = "-Xms" + args[0] + "M";
		String maxMemory = "-Xmx" + args[1] + "M";
		Integer option1 = Integer.parseInt(args[2]);
		Integer option2 = Integer.parseInt(args[3]);
		Integer option3 = null;
		if(args.length > 4) {
			option3 = Integer.parseInt(args[4]);
		}
		
		File file = new File("Result");
		try {
			resultFile = new PrintWriter(new FileWriter(file, true));
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
		
		switch (option3) {
			case 1 : commandList.add("-s"); commandList.add("small"); break;
			case 2 : commandList.add("-s"); commandList.add("large"); break;
		}

		String[] commandArray = new String[commandList.size()];
		commandArray = commandList.toArray(commandArray);
		executeBenchmark(commandArray, option2);
		
		int index = commandList.indexOf(JAVOLUTION_DACAPO);
		commandList.remove(JAVOLUTION_DACAPO);
		commandList.add(index, JAVA_DACAPO);

		commandArray = commandList.toArray(commandArray);
		executeBenchmark(commandArray, option2);

		resultFile.close();

	}
	
	static void executeBenchmark(String[] commandArray, int numberOfExecution) {
		try {
			for(int numberOfRuns=0; numberOfRuns<numberOfExecution; numberOfRuns++) {
				Long startTime = System.currentTimeMillis();
				Process process;
				ProcessBuilder pBuilder = new ProcessBuilder(commandArray);
				//pBuilder.redirectOutput(file);
				process = pBuilder.start();

				InputStream stream = process.getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

				String line;
				while ((line = reader.readLine()) != null) {
					System.out.println(line);
					resultFile.write(line+"\n");
					resultFile.flush();
				}
				stream = process.getErrorStream();
				
				reader = new BufferedReader(new InputStreamReader(stream));
				while ((line = reader.readLine()) != null) {
					System.out.println(line);
					resultFile.write(line+"\n");
					resultFile.flush();
				}

				long timeTaken = System.currentTimeMillis() - startTime;
				StringBuilder commandStr = new StringBuilder("");
				for(int index=0; index<commandArray.length; index++) {
					commandStr.append(" "+commandArray[index]);
				}
				resultFile.write("\n Time taken for executing : " + timeTaken + " command :---\n"+commandStr.toString());
				process.waitFor();
			}
			resultFile.write("\n-----------------END----------------- \n\n\n\n");
			resultFile.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
