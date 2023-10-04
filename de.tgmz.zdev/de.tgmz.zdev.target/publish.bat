@rem Copyright (c) 04.10.2022 Thomas Zierer.
@rem All rights reserved. This program and the accompanying materials
@rem are made available under the terms of the Eclipse Public License v2.0         
@rem which accompanies this distribution, and is available at                      
@rem http://www.eclipse.org/legal/epl-v20.html                                     
@rem                                                                               
@rem Contributors:                                                                 
@rem    Thomas Zierer - initial API and implementation and/or initial documentation

@rem https://wiki.eclipse.org/Equinox/p2/Publisher

@rem eclipse -application org.eclipse.equinox.p2.publisher.UpdateSitePublisher -metadataRepository file:/C:/Daten/y08577.tgmzMASTER/Repositories/p2/zOS_Explorer/repository -artifactRepository file:/C:/Daten/y08577.tgmzMASTER/Repositories/p2/zOS_Explorer/repository -source http://public.dhe.ibm.com/software/htp/zos/tools/1/ -configs gtk.linux.x86 -compress -publishArtifacts

@rem java -jar "C:/Program Files/eclipse/eclipse-rcp-mars-R-win32-x86_64/plugins/org.eclipse.equinox.launcher_1.3.100.v20150511-1540.jar" -application org.eclipse.equinox.p2.publisher.UpdateSitePublisher -metadataRepository file:/C:/Daten/y08577.tgmzMASTER/Repositories/p2/zOS_Explorer/repository -artifactRepository file:/C:/Daten/y08577.tgmzMASTER/Repositories/p2/zOS_Explorer/repository -source /http://public.dhe.ibm.com/software/htp/zos/tools/1/ -configs gtk.linux.x86 -compress -publishArtifacts

@rem java -jar "C:/Program Files/eclipse/eclipse-rcp-mars-R-win32-x86_64/plugins/org.eclipse.equinox.launcher_1.3.100.v20150511-1540.jar" -application org.eclipse.equinox.p2.publisher.FeaturesAndBundlesPublisher -metadataRepository file:/C:/Daten/y08577.tgmzMASTER/Repositories/p2/zOS_Explorer/repository -artifactRepository file:/C:/Daten/y08577.tgmzMASTER/Repositories/p2/zOS_Explorer/repository -source "C:\Program Files\eclipse\eclipse-java-juno-SR2-win32-x86_64" -configs gtk.linux.x86 -compress -publishArtifacts

@rem java -jar "C:/Anwend/eclipse/eclipse-rcp-mars-R-win32-x86_64/plugins/org.eclipse.equinox.launcher_1.3.100.v20150511-1540.jar" -application org.eclipse.equinox.p2.publisher.FeaturesAndBundlesPublisher -metadataRepository file:/C:/Daten/y08577.tgmzMASTER/Repositories/p2/zOS_Explorer/repository -artifactRepository file:/C:/Daten/y08577.tgmzMASTER/Repositories/p2/zOS_Explorer/repository -source "C:\Anwend\eclipse\eclipse" -configs gtk.linux.x86 -compress -publishArtifacts

@rem java -jar "C:/Anwend/eclipse/eclipse-rcp-mars-R-win32-x86_64/plugins/org.eclipse.equinox.launcher_1.3.100.v20150511-1540.jar" -application org.eclipse.equinox.p2.publisher.FeaturesAndBundlesPublisher -metadataRepository file:/O:/Prj/tgmz/T/TFS/Repositories/p2/dmtool/repository -artifactRepository file:/O:/Prj/tgmz/T/TFS/Repositories/p2/dmtool/repository -source "C:\Anwend\eclipse\eclipse" -configs gtk.linux.x86 -compress -publishArtifacts

@rem java -jar "C:/Anwend/eclipse/eclipse-rcp-neon-3-win32-x86_64/plugins/org.eclipse.equinox.launcher_1.3.201.v20161025-1711.jar" -application org.eclipse.equinox.p2.publisher.FeaturesAndBundlesPublisher -metadataRepository file:/O:/Prj/tgmz/T/TFS/Repositories/p2/imsexplorerdev/repository -artifactRepository file:/O:/Prj/tgmz/T/TFS/Repositories/p2/imsexplorerdev/repository -source "C:\Program Files\IBM\SDPShared" -configs gtk.linux.x86 -compress -publishArtifacts

java -jar "C:/Anwend/eclipse/eclipse-rcp-neon-3-win32-x86_64/plugins/org.eclipse.equinox.launcher_1.3.201.v20161025-1711.jar" -application org.eclipse.equinox.p2.publisher.FeaturesAndBundlesPublisher -metadataRepository file:/O:/Prj/tgmz/T/TFS/Repositories/p2/zosconnect/repository -artifactRepository file:/O:/Prj/tgmz/T/TFS/Repositories/p2/zosconnect/repository -source "C:/Program Files/IBM/IBMIMShared" -configs gtk.linux.x86 -compress -publishArtifacts

pause
