set classpath=.;C:\InstalledPrograms\xalan\xalan.jar;C:\InstalledPrograms\xalan\xercesImpl.jar;C:\InstalledPrograms\xalan\xml-apis.jar

java org.apache.xalan.xslt.Process -IN ../DBBrowser-v0.1-Technical-Architecture-Document.xml -XSL DBBrowser-docbook-to-FO.xsl -out DBBrowser-v0.1-Technical-Architecture-Document.fo

pause

copy DBBrowser-v0.1-Technical-Architecture-Document.fo C:\InstalledPrograms\fop

C:\InstalledPrograms\fop\fop-dbbrowser.bat

pause

copy C:\InstalledPrograms\fop\DBBrowser-v0.1-Technical-Architecture-Document.pdf .

pause


