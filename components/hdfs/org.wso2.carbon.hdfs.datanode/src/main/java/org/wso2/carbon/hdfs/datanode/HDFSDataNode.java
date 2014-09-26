/*
*  Copyright (c) 2005-2011, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
package org.wso2.carbon.hdfs.datanode;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.server.datanode.DataNode;
import org.apache.hadoop.hdfs.server.datanode.SecureDataNodeStarter;
import org.apache.hadoop.hdfs.server.datanode.SecureDataNodeStarter.SecureResources;
import org.apache.hadoop.util.Daemon;
import org.apache.commons.daemon.DaemonContext;
import org.apache.commons.daemon.DaemonController;
import org.apache.hadoop.util.StringUtils;
import org.wso2.carbon.utils.ServerConstants;

import static org.apache.hadoop.fs.CommonConfigurationKeys.HADOOP_SECURITY_AUTHENTICATION;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.ServerSocketChannel;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.http.HttpServer;
import org.mortbay.jetty.nio.SelectChannelConnector;
/**
 * Start/Stop HDFS Data Node
 */
public class HDFSDataNode {

    private static Log log = LogFactory.getLog(HDFSDataNode.class);

    private static final String CORE_SITE_XML = "core-site.xml";
    private static final String HDFS_DEFAULT_XML = "hdfs-default.xml";
    private static final String HDFS_SITE_XML = "hdfs-site.xml";
    private static final String HADOOP_POLICY_XML = "hadoop-policy.xml";
    private static final String METRICS2_PROPERTIES = "hadoop-metrics2.properties";

    private Thread thread;
      
    private String [] args = {""};
    private SecureResources resources;
    Configuration configuration;

    public HDFSDataNode() {
        log.info("HDFS: Entered Data Node ");
        log.info("HDFS: v2.4.1-3 ");
    	
        configuration = new Configuration(false);
        String carbonHome = System.getProperty(ServerConstants.CARBON_HOME);
        String hadoopCoreSiteConf = carbonHome + File.separator + "repository" + File.separator +
                "conf" + File.separator + "etc" + File.separator + "hadoop" + File.separator + CORE_SITE_XML;
        String hdfsCoreSiteConf = carbonHome + File.separator + "repository" + File.separator +
                "conf" + File.separator + "etc" + File.separator + "hadoop" + File.separator + HDFS_SITE_XML;
        String hadoopPolicyConf = carbonHome + File.separator + "repository" + File.separator +
                "conf" + File.separator + "etc" + File.separator + "hadoop" + File.separator + HADOOP_POLICY_XML;
        String hadoopMetrics2Properties = carbonHome + File.separator + "repository" + File.separator +
                "conf" + File.separator + "etc" + File.separator + "hadoop" + File.separator + METRICS2_PROPERTIES;
        configuration.addResource(new Path(hadoopCoreSiteConf));
        configuration.addResource(new Path(hdfsCoreSiteConf));
        configuration.set("fs.hdfs.impl", 
                org.apache.hadoop.hdfs.DistributedFileSystem.class.getName()
            );
        configuration.set("fs.file.impl",
                org.apache.hadoop.fs.LocalFileSystem.class.getName()
            );
        
        thread = new Thread(new Runnable() {
            public void run() {
                if (log.isDebugEnabled()) {
                    log.debug("Activating the Hadoop Data Node");
                }
                
                try {
                	localSecureDataNodeStarter secureDataNodeStarter = new localSecureDataNodeStarter();
                    DaemonContext daemonContext = new DaemonContext() {
                        @Override
                        public DaemonController getController() {
                            return null;
                        }

                        @Override
                        public String[] getArguments() {
                            return new String[0];
                        }
                    };
                 
                    secureDataNodeStarter.setConfiguration(configuration);
                    secureDataNodeStarter.init(daemonContext);
                    secureDataNodeStarter.start();
                    
                    log.info("HDFS: Hadoop Secured  Datanode Started");
                } catch (Throwable e) {
                    log.error(e);
                }
            }
        }, "HadoopDataController");
        thread.start();
        
        log.info("HDFS: Hadoop Secured  Datanode Started");


        
        
    }

	public class localSecureDataNodeStarter extends SecureDataNodeStarter {

		Configuration configuration;
		private String[] args;
		private String[] argsDefault = { "-regular" };
		private SecureResources resources;

		public void setConfiguration(Configuration conf) {
			configuration = conf;
		}

		@Override
		public void init(DaemonContext context) throws Exception {
			System.err.println("Initializing secure datanode resources");

			args = context.getArguments();
			resources = getSecureResources(configuration);
		}

		@Override
		public void start() throws Exception {
			System.err.println("Starting regular datanode initialization");
			DataNode.secureMain(args, resources, configuration);
		}

	}

    /**
     * Starts the Hadoop Data Node
     */
    public void start() {
        thread = new Thread(new Runnable() {
            public void run() {
                if (log.isDebugEnabled()) {
                    log.debug("Activating the Hadoop Data Node");
                }
                new HDFSDataNode();
            }
        }, "HadoopDataController");
        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        thread.start();

    }

    /**
     * Stops the Hadoop Data Node
     */
    public void shutdown() {
        if (log.isDebugEnabled()) {
            log.debug("Deactivating the Hadoop Data Node");
        }
        try {
            thread.join();
        } catch (InterruptedException ignored) {
        }
    }
}
