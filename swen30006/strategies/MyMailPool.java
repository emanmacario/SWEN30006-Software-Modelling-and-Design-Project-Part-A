package strategies;

import java.util.Comparator;
import java.util.PriorityQueue;


import automail.MailItem;
import automail.StorageTube;

public class MyMailPool implements IMailPool {
	
	// Idea: use a priority queue.
	// The comparator needs to prioritise based on 3 factors: floor, weight, priority
	
	
	
	/**
     * Adds an item to the mail pool
     * @param mailItem the mail item being added.
     */
	@Override
	public void addToPool(MailItem mailItem) {
		// TODO Auto-generated method stub
		
	}
	
	/**
     * @param tube refers to the pack the robot uses to deliver mail.
     * @param strong is whether the tube belongs to a strong robot.
     */
	@Override
	public void fillStorageTube(StorageTube tube, boolean strong) {
		// TODO Auto-generated method stub
		
	}

}
