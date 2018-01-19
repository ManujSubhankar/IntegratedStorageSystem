package iss.client;

public class Status {
	public long Totalsize= 0;
	public long Usedsize= 0;
	public boolean Transfer= false;
	Thread thread= null;
	
	Status(){}

	public long getTotalsize() {
		return Totalsize;
	}

	public void setTotalsize(long totalsize) {
		Totalsize = totalsize;
	}

	public long getUsedsize() {
		return Usedsize;
	}

	public void setUsedsize(long usedsize) {
		Usedsize = usedsize;
	}

	public Thread getThread() {
		return thread;
	}

	public void setThread(Thread thread) {
		this.thread = thread;
	};
	
	public boolean getTransferAck(){
		return Transfer;
	}
	
	public void setTransferAck(boolean flag){
		this.Transfer= flag;
	}
	
}
