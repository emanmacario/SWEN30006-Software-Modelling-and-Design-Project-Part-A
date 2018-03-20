/**
 * SWEN30006 Software Modelling and Design
 * Semester 1, 2017
 * Project - Part A
 * 
 * Name: Emmanuel Macario
 * Student Number: 831659
 * Last Modified: 20/03/18
 * 
 */

/** Package Name. */
package strategies;


/** Importing classes from the automail package. */
import automail.MailItem;
import automail.PriorityMailItem;
import automail.StorageTube;
import automail.Building;


/**
 * MyRobotBehaviour is responsible for controlling
 * the behaviour of a robot, including the potential 
 * return of a robot to the mail room if certain circumstances
 * deem it as favourable. The class is also responsible for notifying 
 * the robot of any newly arrived priority mail, from which
 * the information gained is used as a basis for its decisions.
 */
public class MyRobotBehaviour implements IRobotBehaviour {
	
	/** Class constants. */
	private static int HIGH_PRIORITY = 100;
	
	/** Instance variables. */
	private int weightLimit;
	private int newPriorityLevel;
	private boolean newPriority;
	
	
	/**
	 * MyRobotBehaviour constructor.
	 * 
	 * @param strong true or false, indicating strong or weak robot.
	 * @return a new MyRobotBehaviour object.
	 */
	public MyRobotBehaviour(boolean strong) {
		this.weightLimit = getWeightLimit(strong);
	}
	
	
	/**
	 * Provides the robot the opportunity to initialise 
	 * state in support of the other methods below. 
	 * 
	 * @param void
	 * @return void
	 */
	@Override
	public void startDelivery() {
		
		/* At the start of the delivery, reset
		 * the flags indicating whether a new
		 * priority mail item has arrived in 
		 * the mail room.
		 */
		this.newPriority = false;
		this.newPriorityLevel = 0;
		
	}
	
	
	/** 
	 * Allows the robot to return with items still in the
	 * tube, if certain circumstances make this desirable.
	 * The robot will always return to the mail room when
	 * the tube is empty.
	 * 
	 * @param tube refers to the pack the robot uses to deliver mail.
	 * @return When this is true, the robot is returned to the mail room.
	 */
	@Override
	public boolean returnToMailRoom(StorageTube tube) {
		
		MailItem mailItem = null;
		
		/* Get the 'uppermost' mail item
		 * in the tube, if there is one.
		 */
		if (tube.getSize() > 0) {
			mailItem = tube.peek();
		}
		
		/* If there is a new priority item with the highest priority
		 * level, and our tube only contains a non-priority 
		 * mail with destination floor less than the middle floor
		 * of the building, then tell the robot to return.
		 */
		if (newPriority && newPriorityLevel == HIGH_PRIORITY
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
	 * Notifies a robot when a new priority mail
	 * item arrives at the building, with weight
	 * less than the maximum weight limit of a
	 * single item for the robot.
	 * 
     * @param priority 	Priority level of arriving mail item.
     * @param weight    The weight value of the priority item.
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

