package org.dbbrowser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ProcessOF
{
	private String FILE_HEADER = "\"HDR\",\"DL7431\",\"CMS/CLASS INTEGRATION OVERNIGHT FEED -  16/03/2006\",\"2006/03/16\"\n" + 
		"\"IDENTIFIER\",\"RECORD TYPE\",\"ACTION TYPE\",\"UNIQUE CLAIM REFERENCE\",\"TRANSACTION REFERENCE\",\"CLAIM SEQUENCE NUMBER\",\"BUREAU ID\"";
	
	public static void main(String[] args)
	{
		( new ProcessOF() ).runForWellington();
	}
	
	/*
insert into policy 
(
ID, 
CLIENT_NAME,
IS_SKELETAL,
CLASS_OSND_DATE,
CLASS_OSND_NUMBER,
VERSION
)
values
(
POLICY_SEQ.nextval,
'Wellington',
'N',
'03-APR-2006',
77777,
0
)

	 *
	 */
	
	public void runForAscot()
	{
		insertHeader("C:/projects/xcs_cms/target/dist/ASCOT-claims-for-stress-testing.csv");
		processTypeA_Data("1414", "SY", 15, "ASCOT", "C:/projects/xcs_cms/target/dist/template-type-A-for-OF-for-stress-testing.csv", "C:/projects/xcs_cms/target/dist/ASCOT-claims-for-stress-testing.csv");
		System.out.println("\n-----------------------------------------------------------------------------------\n");
		processTypeB_Data("1414", "SY", 15, "ASCOT", "C:/projects/xcs_cms/target/dist/template-type-B-for-OF-for-stress-testing.csv", "C:/projects/xcs_cms/target/dist/ASCOT-claims-for-stress-testing.csv");
		System.out.println("\n-----------------------------------------------------------------------------------\n");
		processTypeC_Data("1414", "SY", 15, "ASCOT", "C:/projects/xcs_cms/target/dist/template-type-C-for-OF-for-stress-testing.csv", "C:/projects/xcs_cms/target/dist/ASCOT-claims-for-stress-testing.csv");
		System.out.println("\n-----------------------------------------------------------------------------------\n");
		//processTypeD_Data("1414", "SY", 15, "ASCOT", "C:/projects/xcs_cms/target/dist/template-type-D-for-OF-for-stress-testing.csv", "C:/projects/xcs_cms/target/dist/ASCOT-claims-for-stress-testing.csv");
	}
	
	public void runForAxis()
	{
		insertHeader("C:/projects/xcs_cms/target/dist/AXIS-claims-for-stress-testing.csv");
		processTypeA_Data("A9208", "LR", 15, "AXIS", "C:/projects/xcs_cms/target/dist/template-type-A-for-OF-for-stress-testing.csv", "C:/projects/xcs_cms/target/dist/AXIS-claims-for-stress-testing.csv");
		System.out.println("\n-----------------------------------------------------------------------------------\n");
		processTypeB_Data("A9208", "LR", 15, "AXIS", "C:/projects/xcs_cms/target/dist/template-type-B-for-OF-for-stress-testing.csv", "C:/projects/xcs_cms/target/dist/AXIS-claims-for-stress-testing.csv");
		System.out.println("\n-----------------------------------------------------------------------------------\n");
		processTypeC_Data("A9208", "LR", 15, "AXIS", "C:/projects/xcs_cms/target/dist/template-type-C-for-OF-for-stress-testing.csv", "C:/projects/xcs_cms/target/dist/AXIS-claims-for-stress-testing.csv");
		System.out.println("\n-----------------------------------------------------------------------------------\n");
		
		String[] bureauNumbers = new String[]{"A9208", "A9315", "A9703"};
		processTypeD_Data(bureauNumbers, "LR", 15, "AXIS", "C:/projects/xcs_cms/target/dist/template-type-D-for-OF-for-stress-testing.csv", "C:/projects/xcs_cms/target/dist/AXIS-claims-for-stress-testing.csv");
	}	
	
	public void runForWellington()
	{
		insertHeader("C:/projects/xcs_cms/target/dist/WELLINGTON-claims-for-stress-testing.csv");
		processTypeA_Data("0051", "SY", 15, "WELL", "C:/projects/xcs_cms/target/dist/template-type-A-for-OF-for-stress-testing.csv", "C:/projects/xcs_cms/target/dist/WELLINGTON-claims-for-stress-testing.csv");
		System.out.println("\n-----------------------------------------------------------------------------------\n");
		processTypeB_Data("0051", "SY", 15, "WELL", "C:/projects/xcs_cms/target/dist/template-type-B-for-OF-for-stress-testing.csv", "C:/projects/xcs_cms/target/dist/WELLINGTON-claims-for-stress-testing.csv");
		System.out.println("\n-----------------------------------------------------------------------------------\n");
		processTypeC_Data("0051", "SY", 15, "WELL", "C:/projects/xcs_cms/target/dist/template-type-C-for-OF-for-stress-testing.csv", "C:/projects/xcs_cms/target/dist/WELLINGTON-claims-for-stress-testing.csv");
		System.out.println("\n-----------------------------------------------------------------------------------\n");
		
		String[] bureauNumbers = new String[]{"0051", "1095", "2020"};
		processTypeD_Data(bureauNumbers, "SY", 15, "WELL", "C:/projects/xcs_cms/target/dist/template-type-D-for-OF-for-stress-testing.csv", "C:/projects/xcs_cms/target/dist/WELLINGTON-claims-for-stress-testing.csv");
	}
	
	public void runForTMG()
	{
		insertHeader("C:/projects/xcs_cms/target/dist/TMG-claims-for-stress-testing.csv");
		processTypeA_Data("T8703", "LR", 15, "TMG", "C:/projects/xcs_cms/target/dist/template-type-A-for-OF-for-stress-testing.csv", "C:/projects/xcs_cms/target/dist/TMG-claims-for-stress-testing.csv");
		System.out.println("\n-----------------------------------------------------------------------------------\n");
		processTypeB_Data("T8703", "LR", 15, "TMG", "C:/projects/xcs_cms/target/dist/template-type-B-for-OF-for-stress-testing.csv", "C:/projects/xcs_cms/target/dist/TMG-claims-for-stress-testing.csv");
		System.out.println("\n-----------------------------------------------------------------------------------\n");
		processTypeC_Data("T8703", "LR", 15, "TMG", "C:/projects/xcs_cms/target/dist/template-type-C-for-OF-for-stress-testing.csv", "C:/projects/xcs_cms/target/dist/TMG-claims-for-stress-testing.csv");
		System.out.println("\n-----------------------------------------------------------------------------------\n");
	}	
	
	public void runForCatlin()
	{
		insertHeader("C:/projects/xcs_cms/target/dist/CATLIN-claims-for-stress-testing.csv");
		processTypeA_Data("1003", "SY", 15, "CATLIN", "C:/projects/xcs_cms/target/dist/template-type-A-for-OF-for-stress-testing.csv", "C:/projects/xcs_cms/target/dist/CATLIN-claims-for-stress-testing.csv");
		System.out.println("\n-----------------------------------------------------------------------------------\n");
		processTypeB_Data("2003", "SY", 15, "CATLIN", "C:/projects/xcs_cms/target/dist/template-type-B-for-OF-for-stress-testing.csv", "C:/projects/xcs_cms/target/dist/CATLIN-claims-for-stress-testing.csv");
		System.out.println("\n-----------------------------------------------------------------------------------\n");
		processTypeC_Data("1003", "SY", 15, "CATLIN", "C:/projects/xcs_cms/target/dist/template-type-C-for-OF-for-stress-testing.csv", "C:/projects/xcs_cms/target/dist/CATLIN-claims-for-stress-testing.csv");
		System.out.println("\n-----------------------------------------------------------------------------------\n");

		String[] bureauNumbers = new String[]{"1003", "2003", "1003"};
		processTypeD_Data(bureauNumbers, "SY", 15, "CATLIN", "C:/projects/xcs_cms/target/dist/template-type-D-for-OF-for-stress-testing.csv", "C:/projects/xcs_cms/target/dist/CATLIN-claims-for-stress-testing.csv");		
	}	
	
	private void insertHeader(String outputFile)
	{
		try
		{
			FileWriter fileWriter = new FileWriter(outputFile);
			BufferedWriter writer = new BufferedWriter(fileWriter);
			writer.write( FILE_HEADER + "\n" );
			writer.flush();
			writer.close();
		}
		catch(IOException exc)
		{
			System.out.println("*** IOException ***" + exc.getMessage());
		}
	}
	
	public void processTypeD_Data(String[] bureauNumbers, String bureauId, int countOfClaimsGenerated, String clientName, String inputFile, String outputFile)
	{
		try
		{
			String CLIENT_NAME = clientName;
			String BUREAU_ID = bureauId;
			String BUREAU_NUMBER1 = bureauNumbers[0];
			String BUREAU_NUMBER2 = bureauNumbers[1];
			String BUREAU_NUMBER3 = bureauNumbers[2];
			String OSND_NUMBER1 = "99999";
			String OSND_NUMBER2 = "88888";
			String OSND_NUMBER3 = "77777";
			String OSND_DATE = "20060402";						
			
			//Load the file
			FileReader fileReader = new FileReader(inputFile);
			BufferedReader reader = new BufferedReader(fileReader);
			
			FileWriter fileWriter = new FileWriter(outputFile, true);
			BufferedWriter writer = new BufferedWriter(fileWriter);
			
			//Read data from file
			String claimLine = reader.readLine();
			
			//Read transaction data from file
			String[] txAndFinancialsLines = new String[3];
			for(int i=0; i<3; i++)
			{
				txAndFinancialsLines[i] = reader.readLine();
			}
			
			//Read market line data from file
			String[] marketLineLines = new String[3];
			for(int i=0; i<3; i++)
			{
				marketLineLines[i] = reader.readLine();
			}			
			
			//Loop and process all claims 
			for(int claimCounter=1; claimCounter<countOfClaimsGenerated; claimCounter++)
			{
				//Loop 3 times for each claim so each claim has 3 transactions
				for(int z=1; z<4; z++)
				{
					//Replace all UCR tokens in line 1
					String newClaimLine = claimLine.replaceAll("<UCR>", CLIENT_NAME + "_" + claimCounter + "D");
					newClaimLine = newClaimLine.replaceAll("<Tx-Ref>", "TxRef_" + z + "_" + CLIENT_NAME + "_" + claimCounter + "D");
					newClaimLine = newClaimLine.replaceAll("<bureau-id>", BUREAU_ID);
					newClaimLine = newClaimLine.replaceAll("<sequence-number>", "00" + z);
					
					
					//Set the osnd number
					String osndNumber = "";
					if( claimCounter <= 5 )
					{
						osndNumber = OSND_NUMBER1;
					}
					else if( claimCounter <= 10 && claimCounter > 5 )
					{
						osndNumber = OSND_NUMBER2;
					}
					else if( claimCounter > 10 )
					{
						osndNumber = OSND_NUMBER3;
					}
					
					newClaimLine = newClaimLine.replaceAll("<osnd-number>", osndNumber);
					newClaimLine = newClaimLine.replaceAll("<osnd-date>", OSND_DATE);					
					
					writer.write( newClaimLine );
					writer.write( "\n" );
					System.out.println( newClaimLine );
					
					//Record type 2 - Tx and financials
					for( int txCounter=1; txCounter<txAndFinancialsLines.length + 1; txCounter++ )
					{
						String txAndFinancialsLine = txAndFinancialsLines[txCounter - 1];
						String newTxLine1 = txAndFinancialsLine.replaceAll("<UCR>", CLIENT_NAME + "_" + claimCounter + "D");
						String newTxLine2 = newTxLine1.replaceAll("<Tx-Ref>", "TxRef_" + z + "_" + CLIENT_NAME + "_" + claimCounter + "D");
						String newTxLine3 = newTxLine2.replaceAll("<bureau-id>", BUREAU_ID);
						newTxLine3 = newTxLine3.replaceAll("<sequence-number>", "00" + z);
						
						writer.write( newTxLine3 );
						writer.write( "\n" );
						System.out.println( newTxLine3 );
					}
					
					//Record type 3 - Tx and financials
					for( int marketLineCounter=1; marketLineCounter<marketLineLines.length + 1; marketLineCounter++ )
					{
						String marketLineLine = marketLineLines[marketLineCounter - 1];
						String newMarketLineLine1 = marketLineLine.replaceAll("<UCR>", CLIENT_NAME + "_" + claimCounter + "D");
						newMarketLineLine1 = newMarketLineLine1.replaceAll("<Tx-Ref>", "TxRef_" + z + "_" + CLIENT_NAME + "_" + claimCounter + "D");
						newMarketLineLine1 = newMarketLineLine1.replaceAll("<bureau-id>", BUREAU_ID);
						newMarketLineLine1 = newMarketLineLine1.replaceAll("<sequence-number>", "00" + z);
						
						
						String bureauNumber = "";
				        switch (marketLineCounter) 
				        {
				            case 1:  bureauNumber = BUREAU_NUMBER1; break;
				            case 2:  bureauNumber = BUREAU_NUMBER2; break;
				            case 3:  bureauNumber = BUREAU_NUMBER3; break;
				            default: System.out.println("*** Invalid option ***");break;
				        }
				        
						newMarketLineLine1 = newMarketLineLine1.replaceAll("<bureau-number>", bureauNumber);
						writer.write( newMarketLineLine1 );				
						writer.write( "\n" );
						System.out.println( newMarketLineLine1 );
					}		
				}
			}
			reader.close();
			writer.flush();
			writer.close();
		}
		catch(IOException exc)
		{
			System.out.println("*** IOException ***" + exc.getMessage());
		}
	}		
	
	public void processTypeC_Data(String bureauNumber, String bureauId, int countOfClaimsGenerated, String clientName, String inputFile, String outputFile)
	{
		try
		{
			String CLIENT_NAME = clientName;
			String BUREAU_ID = bureauId;
			String BUREAU_NUMBER = bureauNumber;
			String OSND_NUMBER1 = "99999";
			String OSND_NUMBER2 = "88888";
			String OSND_NUMBER3 = "77777";
			String OSND_DATE = "20060403";			
			
			
			//Load the file
			FileReader fileReader = new FileReader(inputFile);
			BufferedReader reader = new BufferedReader(fileReader);
			
			FileWriter fileWriter = new FileWriter(outputFile, true);
			BufferedWriter writer = new BufferedWriter(fileWriter);
			
			//Read data from file
			String claimLine = reader.readLine();
			
			//Read transaction data from file
			String[] txAndFinancialsLines = new String[3];
			for(int i=0; i<3; i++)
			{
				txAndFinancialsLines[i] = reader.readLine();
			}
			
			//Read market line data from file
			String[] marketLineLines = new String[1];
			for(int i=0; i<1; i++)
			{
				marketLineLines[i] = reader.readLine();
			}			
			
			//Loop and process all claims 
			for(int claimCounter=1; claimCounter<countOfClaimsGenerated; claimCounter++)
			{
				//Loop 3 times for each claim so each claim has 3 transactions
				for(int z=1; z<4; z++)
				{
					//Replace all UCR tokens in line 1
					String newClaimLine = claimLine.replaceAll("<UCR>", CLIENT_NAME + "_" + claimCounter + "C");
					newClaimLine = newClaimLine.replaceAll("<Tx-Ref>", "TxRef_" + z + "_" + CLIENT_NAME + "_" + claimCounter + "C");
					newClaimLine = newClaimLine.replaceAll("<bureau-id>", BUREAU_ID);
					newClaimLine = newClaimLine.replaceAll("<sequence-number>", "00" + z);
					
					//Set the osnd number
					String osndNumber = "";
					if( claimCounter <= 5 )
					{
						osndNumber = OSND_NUMBER1;
					}
					else if( claimCounter <= 10 && claimCounter > 5 )
					{
						osndNumber = OSND_NUMBER2;
					}
					else if( claimCounter > 10 )
					{
						osndNumber = OSND_NUMBER3;
					}
					
					newClaimLine = newClaimLine.replaceAll("<osnd-number>", osndNumber);
					newClaimLine = newClaimLine.replaceAll("<osnd-date>", OSND_DATE);
					
					writer.write( newClaimLine );
					writer.write( "\n" );
					System.out.println( newClaimLine );
					
					//Record type 2 - Tx and financials
					for( int txCounter=1; txCounter<txAndFinancialsLines.length + 1; txCounter++ )
					{
						String txAndFinancialsLine = txAndFinancialsLines[txCounter - 1];
						String newTxLine1 = txAndFinancialsLine.replaceAll("<UCR>", CLIENT_NAME + "_" + claimCounter + "C");
						String newTxLine2 = newTxLine1.replaceAll("<Tx-Ref>", "TxRef_" + z + "_" + CLIENT_NAME + "_" + claimCounter + "C");
						String newTxLine3 = newTxLine2.replaceAll("<bureau-id>", BUREAU_ID);
						newTxLine3 = newTxLine3.replaceAll("<sequence-number>", "00" + z);
						writer.write( newTxLine3 );
						writer.write( "\n" );
						System.out.println( newTxLine3 );
					}
					
					//Record type 3 - Tx and financials
					for( int marketLineCounter=1; marketLineCounter<marketLineLines.length + 1; marketLineCounter++ )
					{
						String marketLineLine = marketLineLines[marketLineCounter - 1];
						String newMarketLineLine1 = marketLineLine.replaceAll("<UCR>", CLIENT_NAME + "_" + claimCounter + "C");
						newMarketLineLine1 = newMarketLineLine1.replaceAll("<Tx-Ref>", "TxRef_" + z + "_" + CLIENT_NAME + "_" + claimCounter + "C");
						newMarketLineLine1 = newMarketLineLine1.replaceAll("<bureau-id>", BUREAU_ID);
						newMarketLineLine1 = newMarketLineLine1.replaceAll("<bureau-number>", BUREAU_NUMBER);
						newMarketLineLine1 = newMarketLineLine1.replaceAll("<sequence-number>", "00" + z);
						
						writer.write( newMarketLineLine1 );				
						writer.write( "\n" );
						System.out.println( newMarketLineLine1 );
					}		
				}
			}
			reader.close();
			writer.flush();
			writer.close();
		}
		catch(IOException exc)
		{
			System.out.println("*** IOException ***" + exc.getMessage());
		}
	}	
	
	public void processTypeB_Data(String bureauNumber, String bureauId, int countOfClaimsGenerated, String clientName, String inputFile, String outputFile)
	{
		try
		{
			String CLIENT_NAME = clientName;
			String BUREAU_ID = bureauId;
			String BUREAU_NUMBER = bureauNumber;
			String OSND_NUMBER1 = "99999";
			String OSND_NUMBER2 = "88888";
			String OSND_NUMBER3 = "77777";
			String OSND_DATE = "20060404";			
			
			//Load the file
			FileReader fileReader = new FileReader(inputFile);
			BufferedReader reader = new BufferedReader(fileReader);
			
			FileWriter fileWriter = new FileWriter(outputFile, true);
			BufferedWriter writer = new BufferedWriter(fileWriter);
			
			//Read data from file
			String claimLine = reader.readLine();
			
			//Read transaction data from file
			String[] txAndFinancialsLines = new String[3];
			for(int i=0; i<3; i++)
			{
				txAndFinancialsLines[i] = reader.readLine();
			}
			
			//Read market line data from file
			String[] marketLineLines = new String[1];
			for(int i=0; i<1; i++)
			{
				marketLineLines[i] = reader.readLine();
			}			
			
			//Loop and process all claims 
			for(int claimCounter=1; claimCounter<countOfClaimsGenerated; claimCounter++)
			{
				//Replace all UCR tokens in line 1
				String newClaimLine = claimLine.replaceAll("<UCR>", CLIENT_NAME + "_" + claimCounter + "B");
				newClaimLine = newClaimLine.replaceAll("<Tx-Ref>", "TxRef" + "_" + CLIENT_NAME + "_" + claimCounter + "B");
				newClaimLine = newClaimLine.replaceAll("<bureau-id>", BUREAU_ID);
				
				//Set the osnd number
				String osndNumber = "";
				if( claimCounter <= 5 )
				{
					osndNumber = OSND_NUMBER1;
				}
				else if( claimCounter <= 10 && claimCounter > 5 )
				{
					osndNumber = OSND_NUMBER2;
				}
				else if( claimCounter > 10 )
				{
					osndNumber = OSND_NUMBER3;
				}
				
				newClaimLine = newClaimLine.replaceAll("<osnd-number>", osndNumber);
				newClaimLine = newClaimLine.replaceAll("<osnd-date>", OSND_DATE);
				
				writer.write( newClaimLine );
				writer.write( "\n" );
				System.out.println( newClaimLine );
				
				//Record type 2 - Tx and financials
				for( int txCounter=1; txCounter<txAndFinancialsLines.length + 1; txCounter++ )
				{
					String txAndFinancialsLine = txAndFinancialsLines[txCounter - 1];
					String newTxLine1 = txAndFinancialsLine.replaceAll("<UCR>", CLIENT_NAME + "_" + claimCounter + "B");
					String newTxLine2 = newTxLine1.replaceAll("<Tx-Ref>", "TxRef" + "_" + CLIENT_NAME + "_" + claimCounter + "B");
					String newTxLine3 = newTxLine2.replaceAll("<bureau-id>", BUREAU_ID);
					writer.write( newTxLine3 );
					writer.write( "\n" );
					System.out.println( newTxLine3 );
				}
				
				//Record type 3 - Tx and financials
				for( int marketLineCounter=1; marketLineCounter<marketLineLines.length + 1; marketLineCounter++ )
				{
					String marketLineLine = marketLineLines[marketLineCounter - 1];
					String newMarketLineLine1 = marketLineLine.replaceAll("<UCR>", CLIENT_NAME + "_" + claimCounter + "B");
					newMarketLineLine1 = newMarketLineLine1.replaceAll("<Tx-Ref>", "TxRef" + "_" + CLIENT_NAME + "_" + claimCounter + "B");
					newMarketLineLine1 = newMarketLineLine1.replaceAll("<bureau-id>", BUREAU_ID);
					newMarketLineLine1 = newMarketLineLine1.replaceAll("<bureau-number>", BUREAU_NUMBER);
					writer.write( newMarketLineLine1 );				
					writer.write( "\n" );
					System.out.println( newMarketLineLine1 );
				}				
			}
			reader.close();
			writer.flush();
			writer.close();
		}
		catch(IOException exc)
		{
			System.out.println("*** IOException ***" + exc.getMessage());
		}
	}	
	
	public void processTypeA_Data(String bureauNumber, String bureauId, int countOfClaimsGenerated, String clientName, String inputFile, String outputFile)
	{
		try
		{
			String CLIENT_NAME = clientName;
			String BUREAU_ID = bureauId;
			String BUREAU_NUMBER = bureauNumber;
			String OSND_NUMBER1 = "99999";
			String OSND_NUMBER2 = "88888";
			String OSND_NUMBER3 = "77777";
			String OSND_DATE = "20060405";
			
			//Load the file
			FileReader fileReader = new FileReader(inputFile);
			BufferedReader reader = new BufferedReader(fileReader);
			
			FileWriter fileWriter = new FileWriter(outputFile, true);
			BufferedWriter writer = new BufferedWriter(fileWriter);
			
			String claimLine = reader.readLine();
			String txAndFinancialsLine = reader.readLine();
			String marketLineLine = reader.readLine();
			
			//Loop and process all claims 
			for(int claimCounter=1; claimCounter<countOfClaimsGenerated; claimCounter++)
			{
				//Replace all tokens in line 1
				String newClaimLine = claimLine.replaceAll("<UCR>", CLIENT_NAME + "_" + claimCounter + "A");
				newClaimLine = newClaimLine.replaceAll("<Tx-Ref>", "TxRef" + "_" + CLIENT_NAME + "_" + claimCounter + "A");
				newClaimLine = newClaimLine.replaceAll("<bureau-id>", bureauId);
				
				//Set the osnd number
				String osndNumber = "";
				if( claimCounter <= 5 )
				{
					osndNumber = OSND_NUMBER1;
				}
				else if( claimCounter <= 10 && claimCounter > 5 )
				{
					osndNumber = OSND_NUMBER2;
				}
				else if( claimCounter > 10 )
				{
					osndNumber = OSND_NUMBER3;
				}
				
				newClaimLine = newClaimLine.replaceAll("<osnd-number>", osndNumber);
				newClaimLine = newClaimLine.replaceAll("<osnd-date>", OSND_DATE);
				
				writer.write( newClaimLine );
				writer.write( "\n" );
				System.out.println( newClaimLine );
				
				//Line 2
				String newTxLine1 = txAndFinancialsLine.replaceAll("<UCR>", CLIENT_NAME + "_" + claimCounter + "A");
				String newTxLine2 = newTxLine1.replaceAll("<Tx-Ref>", "TxRef_" + CLIENT_NAME + "_" + claimCounter + "A");
				String newTxLine3 = newTxLine2.replaceAll("<bureau-id>", bureauId);
				writer.write( newTxLine3 );
				writer.write( "\n" );
				System.out.println( newTxLine3 );
				
				//Line 3
				String newMarketLineLine1 = marketLineLine.replaceAll("<UCR>", clientName + "_" + claimCounter + "A");
				String newMarketLineLine2 = newMarketLineLine1.replaceAll("<Tx-Ref>", "TxRef" + "_" + CLIENT_NAME + "_" + claimCounter + "A");
				String newMarketLineLine3 = newMarketLineLine2.replaceAll("<bureau-id>", bureauId);
				String newMarketLineLine4 = newMarketLineLine3.replaceAll("<bureau-number>", bureauNumber);
				writer.write( newMarketLineLine4 );				
				writer.write( "\n" );
				System.out.println( newMarketLineLine4 );
			}
			reader.close();
			writer.flush();
			writer.close();
		}
		catch(IOException exc)
		{
			System.out.println("*** IOException ***" + exc.getMessage());
		}
	}
}
