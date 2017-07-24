package tomcat;

import java.io.File;

import org.apache.catalina.core.AprLifecycleListener;
import org.apache.catalina.core.StandardServer;
import org.apache.catalina.startup.Tomcat;

public class tomcatDemo {
	private String hostname = "localhost";
	private int port = 8888;
	private String webAppDir = "webapp";
	private String contextPath = "";// /
	private String uriEncoding = "UTF-8";
	private String baseDir = ".";
	private String appBase = ".";
	private boolean gzipEnable = false;

	private Tomcat tomcat = null;

	public static void main(String[] args) {
		int port = 0;
		String webAppDir = null;
		String contextPath = null;
		String uriEncoding = null;
		String baseDir = null;
		String appBase = null;
		boolean gzipEnable = false;

		for (String arg : args) {
			if (arg.startsWith("-httpPort")) {
				port = Integer.parseInt(arg.substring(arg.indexOf("=") + 1));
			} else if (arg.startsWith("-webAppDir")) {
				webAppDir = arg.substring(arg.indexOf("=") + 1);
			} else if (arg.startsWith("-contextPath")) {
				contextPath = arg.substring(arg.indexOf("=") + 1);
			} else if (arg.startsWith("-URIEncoding")) {
				uriEncoding = arg.substring(arg.indexOf("=") + 1);
			} else if (arg.startsWith("-baseDir")) {
				baseDir = arg.substring(arg.indexOf("=") + 1);
			} else if (arg.startsWith("-appBase")) {
				appBase = arg.substring(arg.indexOf("=") + 1);
			} else if (arg.startsWith("-gzipEnable")) {
				String gzipEnableFlag = arg.substring(arg.indexOf("=") + 1);
				if (null != gzipEnableFlag && (gzipEnableFlag.equalsIgnoreCase("on") || gzipEnableFlag.equalsIgnoreCase("true"))) {
					gzipEnable = true;
				}
			}
		}

		tomcatDemo tomcatDemo = new tomcatDemo();
		if (port > 0) {
			tomcatDemo.setPort(port);
		}
		if ((webAppDir != null) && (webAppDir.trim().length() > 0)) {
			tomcatDemo.setWebAppDir(webAppDir);
		}
		if ((contextPath != null) && (contextPath.trim().length() > 0)) {
			tomcatDemo.setContextPath(contextPath);
		}
		if ((uriEncoding != null) && (uriEncoding.trim().length() > 0)) {
			tomcatDemo.setURIEncoding(uriEncoding);
		}
		if ((baseDir != null) && (baseDir.trim().length() > 0)) {
			tomcatDemo.setBaseDir(baseDir);
		}
		if ((appBase != null) && (appBase.trim().length() > 0)) {
			tomcatDemo.setAppBase(appBase);
		}
		tomcatDemo.setGzipEnable(gzipEnable);

		try {
			tomcatDemo.start();
		} catch (Exception e) {
			System.out.println("tomcat start error : " + e);
			System.exit(-1);
		}
	}

	private void start() throws Exception {
		this.tomcat = new Tomcat();

		this.tomcat.setHostname(this.hostname);
		this.tomcat.setPort(this.port);
		this.tomcat.setSilent(false);
		this.tomcat.setBaseDir(this.baseDir);// 设置工作目录,其实没什么用,tomcat需要使用这个目录进行写一些东西
		this.tomcat.getConnector().setURIEncoding(this.uriEncoding);
		if (gzipEnable) {
			this.tomcat.getConnector().setProperty("compression", "on");
			this.tomcat.getConnector().setProperty("compressionMinSize", "2048");
			this.tomcat.getConnector().setProperty("noCompressionUserAgents", "gozilla, traviata");
			this.tomcat.getConnector().setProperty("compressableMimeType", "text/html,text/xml,text/plain,text/css,application/json,application/javascript");
		}

		this.tomcat.getHost().setAppBase(System.getProperty("user.dir") + File.separator + this.appBase);// 放项目代码的地方

		StandardServer server = (StandardServer) this.tomcat.getServer();
		AprLifecycleListener listener = new AprLifecycleListener();
		server.addLifecycleListener(listener);// 这个listener主要是负责启动一些比如html native支持程序以及ipv6等信息配置（可以忽略）

		this.tomcat.addWebapp(this.contextPath, this.webAppDir);
		this.tomcat.start();
		this.tomcat.getServer().await();

		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				try {
					if (null != tomcatDemo.this.tomcat) {
						tomcatDemo.this.tomcat.stop();
					}
				} catch (Exception e) {
					System.out.println("tomcat stop error : " + e);
				}
			}
		});
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getWebAppDir() {
		return webAppDir;
	}

	public void setWebAppDir(String webAppDir) {
		this.webAppDir = webAppDir;
	}

	public String getContextPath() {
		return contextPath;
	}

	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
	}

	public String getURIEncoding() {
		return uriEncoding;
	}

	public void setURIEncoding(String uriEncoding) {
		this.uriEncoding = uriEncoding;
	}

	public String getBaseDir() {
		return baseDir;
	}

	public void setBaseDir(String baseDir) {
		this.baseDir = baseDir;
	}

	public String getAppBase() {
		return appBase;
	}

	public void setAppBase(String appBase) {
		this.appBase = appBase;
	}

	public boolean isGzipEnable() {
		return gzipEnable;
	}

	public void setGzipEnable(boolean gzipEnable) {
		this.gzipEnable = gzipEnable;
	}

}
