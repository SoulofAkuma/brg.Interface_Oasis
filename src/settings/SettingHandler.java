package settings;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import xmlhandler.SettingFunctions;

public class SettingHandler {
	
	public void handle() {
		
		String input = "<?xml version=\"1.0\"?>  \r\n" + 
				"<Tests xmlns=\"http://www.adatum.com\">  \r\n" + 
				"  testestsets \r\n" +
				"  <Test TestId=\"0001\" TestType=\"CMD\">  \r\n" + 
				"    <Name>Convert number to string</Name>  \r\n" + 
				"    <CommandLine>Examp1.EXE</CommandLine>  \r\n" + 
				"    <Input>1</Input>  \r\n" + 
				"    <Output>One</Output>  \r\n" + 
				"  </Test>  \r\n" + 
				"  <Test TestId=\"0002\" TestType=\"CMD\">  \r\n" + 
				"    <Name>Find succeeding characters</Name>  \r\n" + 
				"    <CommandLine>Examp2.EXE</CommandLine>  \r\n" + 
				"    <Input>abc</Input>  \r\n" + 
				"    <Output>def</Output>  \r\n" + 
				"  </Test>  \r\n" + 
				"  <Test TestId=\"0003\" TestType=\"GUI\">  \r\n" + 
				"    <Name>Convert multiple numbers to strings</Name>  \r\n" + 
				"    <CommandLine>Examp2.EXE /Verbose</CommandLine>  \r\n" + 
				"    <Input>123</Input>  \r\n" + 
				"    <Output>One Two Three</Output>  \r\n" + 
				"  </Test>  \r\n" + 
				"  <Test TestId=\"0004\" TestType=\"GUI\">  \r\n" + 
				"    <Name>Find correlated key</Name>  \r\n" + 
				"    <CommandLine>Examp3.EXE</CommandLine>  \r\n" + 
				"    <Input>a1</Input>  \r\n" + 
				"    <Output>b1</Output>  \r\n" + 
				"  </Test>  \r\n" + 
				"  <Test TestId=\"0005\" TestType=\"GUI\">  \r\n" + 
				"    <Name>Count characters</Name>  \r\n" + 
				"    <CommandLine>FinalExamp.EXE</CommandLine>  \r\n" + 
				"    <Input>This is a test</Input>  \r\n" + 
				"    <Output>14</Output>  \r\n" + 
				"  </Test>  \r\n" + 
				"  <Test TestId=\"0006\" TestType=\"GUI\">  \r\n" + 
				"    <Name>Another Test</Name>  \r\n" + 
				"    <CommandLine>Examp2.EXE</CommandLine>  \r\n" + 
				"    <Input>Test Input</Input>  \r\n" + 
				"    <Output>10</Output>  \r\n" + 
				"  </Test>  \r\n" + 
				"</Tests>  ";
		
		
		Setting bookSettings = Setting.parseSetting(input, 1);
		System.out.println(bookSettings.getXML());
		
	}


}
