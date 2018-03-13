package strategies;

import automail.StorageTube;

public class MyRobotBehaviour implements IRobotBehaviour {
	
	private boolean strong;
	private boolean newPriority;
	private int newWeight;
	
	
	public MyRobotBehaviour(boolean strong) {
		this.strong = strong;
	}
	
	
	/**
	 * startDelivery() provides the robot the opportunity to 
	 * initialise state in support of the other methods below. 
	 */
	@Override
	public void startDelivery() {
		
		// If the robot started a delivery, don't want to return already.
		this.newPriority = false;
		this.newWeight = 0;
	}
	
	
	/** 
	 * @param tube refers to the pack the robot uses to deliver mail.
	 * @return When this is true, the robot is returned to the mail room.
	 * The robot will always return to the mail room when the tube is empty.
	 * This method allows the robot to return with items still in the tube,
	 * if circumstances make this desirable.
	 */
	@Override
	public boolean returnToMailRoom(StorageTube tube) {
		
		// Maximum weight limit of an item, for 
		// a given type of robot.
		int weightLimit = strong ? Integer.MAX_VALUE : 2000;
		
		return false;
	}
	
	
	/**
     * @param priority is that of the priority mail item which just arrived.
     * @param weight is that of the same item.
     * The automail system broadcasts this information to all robots
     * when a new priority mail items arrives at the building.
     */
	@Override
	public void priorityArrival(int priority, int weight) {
		this.newPriority = true;
		this.newWeight = weight;
	}
}

