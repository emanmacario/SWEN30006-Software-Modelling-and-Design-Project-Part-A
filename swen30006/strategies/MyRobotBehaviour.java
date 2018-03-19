/**
 * SWEN30006 Software Modelling and Design
 * Semester 1, 2017
 * Project - Part A
 * 
 * Name: Emmanuel Macario
 * Student Number: 831659
 * Last Modified: 19/03/18
 * 
 */

/** Package Name. */
package strategies;


/** Importing classes from the automail package. */
import automail.MailItem;
import automail.PriorityMailItem;
import automail.StorageTube;
import automail.Building;



public class MyRobotBehaviour implements IRobotBehaviour {
	
	private int weightLimit;
	private int newPriorityLevel;
	private boolean newPriority;
	
	
	public MyRobotBehaviour(boolean strong) {
		this.weightLimit = getWeightLimit(strong);
	}
	
	
	/**
	 * startDelivery() provides the robot the opportunity to 
	 * initialise state in support of the other methods below. 
	 */
	@Override
	public void startDelivery() {
		this.newPriority = false;
		this.newPriorityLevel = 0;
		
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
		
		MailItem mailItem = null;
		
		/* Get the uppermost mail item
		 * in the tube, if there is one.
		 */
		if (tube.getSize() > 0) {
			mailItem = tube.peek();
		}
		
		/* If there is a new priority item with priority level
		 * equal to 100, and our tube only contains a non-priority 
		 * mail with destination floor less than the middle floor
		 * of the building, then tell the robot to return.
		 */
		if (newPriority && newPriorityLevel == 100
						&& mailItem != null
						&& !(mailItem instanceof PriorityMailItem)
						&& tube.getSize() == 1
						&& mailItem.getDestFloor() < Building.FLOORS/2) {
			return true;
		}
		
		/* Otherwise, keep on delivering.
		 */
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
		
		/* Only consider priority items
		 * that this robot can carry.
		 */
		if (weight < this.weightLimit) {
			this.newPriority = true;
			this.newPriorityLevel = priority;
		}
	}
	
	
	/**
     * Gets the maximum weight limit (non-inclusive)
     * for a robot.
     * 
     * @param strong boolean indicating strong (true) or weak (false) robot.
     * @return weightLimit
     */
    private int getWeightLimit(boolean strong) {
    	return strong ? Integer.MAX_VALUE : 2000;
    }
}

