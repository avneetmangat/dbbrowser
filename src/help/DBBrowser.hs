<?xml version="1.0" encoding="ISO-8859-1"?>
<!DOCTYPE helpset PUBLIC "-//Sun Microsystems Inc.//DTD JavaHelp HelpSet Version 2.0//EN"
         "../dtd/helpset_2_0.dtd">
<helpset version="1.0">
	<!-- title -->
	<title>DBBrowser - Help</title>
	<!-- maps -->
	<maps>
		<homeID>intro</homeID>
		<mapref location="DBBrowserMap.jhm"/>
	</maps>
	<!-- views -->
	<view>
		<name>Table of contents</name>
		<label>Table of contents</label>
		<type>javax.help.TOCView</type>
		<data>DBBrowserTOC.xml</data>
	</view>

	<view mergetype="javax.help.SortMerge">
		<name>Glossary</name>
		<label>Glossary</label>
		<type>javax.help.GlossaryView</type>
		<data>DBBrowserGlossary.xml</data>
	</view>
	
	<view>
		<name>Search</name>
		<label>Search</label>
		<type>javax.help.SearchView</type>
		<data engine="com.sun.java.help.search.DefaultSearchEngine">
      JavaHelpSearch
    </data>
	</view>
</helpset>
