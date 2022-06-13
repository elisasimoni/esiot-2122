package esiot.smart_cm;

public class MonitoringAgent extends Thread {

	SerialCommChannel channel;
	SmartCMView view;
	LogView logger;
	
	static final String CM_PREFIX 	=  "cm:";
	static final String LOG_PREFIX 	=  "lo:";
	static final String MSG_WELCOME 		= "we";
	static final String MSG_SLEEPING 		= "zz";
	static final String MSG_READY 			= "ok";
	static final String MSG_MAKING 			= "ma";
	static final String MSG_ASSISTANCE  	= "as";
	static final String MSG_NUM_ITEMS  		= "ni";
	static final String MSG_NUM_SELF_TESTS	= "nt";
	
	public MonitoringAgent(SerialCommChannel channel, SmartCMView view, LogView log) throws Exception {
		this.view = view;
		this.logger = log;
		this.channel = channel;
	}
	
	public void run(){
		while (true){
			try {
				String msg = channel.receiveMsg();
				if (msg.startsWith(CM_PREFIX)){
					String cmd = msg.substring(CM_PREFIX.length()); 
					logger.log("new command: "+cmd);				
					if (cmd.startsWith(MSG_WELCOME)){
						view.setState("Welcome");
					} else if (cmd.startsWith(MSG_SLEEPING)){
						view.setState("Sleeping");
					} else if (cmd.startsWith(MSG_READY)){
						view.setState("Ready");
					} else if (cmd.startsWith(MSG_MAKING)) {
						view.setState("Making a product");
					} else if (cmd.startsWith(MSG_ASSISTANCE)) {
						view.setState("Assistance");
						String reason = cmd.substring(3);
						if (reason.equals("ni")) {
							view.startMaintenanceForRefilling();
						} else {
							view.startMaintenanceForRecovering();
						}
					} else if (cmd.startsWith(MSG_NUM_ITEMS)) {
						view.setNumItems(cmd.substring(3));
					} else if (cmd.startsWith(MSG_NUM_SELF_TESTS)) {
						view.setNumSelfTests(cmd.substring(3));
					}
				} else if (msg.startsWith(LOG_PREFIX)){
					this.logger.log(msg.substring(LOG_PREFIX.length()));
				}
			} catch (Exception ex){
				ex.printStackTrace();
			}
		}
	}

}
